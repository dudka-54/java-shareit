package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Getter
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addNewItem(Long userId, CreateItemRequest request) {
        log.info("Запрос на добавление нового предмета");
        if (request == null) {
            throw new ConflictException("Email уже зарегистрирован");
        }
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest existingRequest = null;
        if (request.getRequestId() != null) {
            existingRequest = itemRequestRepository.findById(request.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос с id=" + request.getRequestId() + " не найден"));
        }
        Item newItem = ItemMapper.mapToItem(request);
        validateItem(newItem);
        newItem.setOwner(owner);
        if (request.getRequestId() != null) {
            newItem.setRequest(existingRequest);
        }

        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    public ItemDto patchItem(Long userId, Long itemId, CreateItemRequest patchItem) {
        log.info("Запрос на обновление предмета с id={} от пользователя {}", itemId, userId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id=" + itemId + " не найден"));

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        if (patchItem.getName() != null && !patchItem.getName().isBlank()) {
            item.setName(patchItem.getName());
        }
        if (patchItem.getDescription() != null && !patchItem.getDescription().isBlank()) {
            item.setDescription(patchItem.getDescription());
        }
        if (patchItem.getAvailable() != null) {
            item.setAvailable(patchItem.getAvailable());
        }

        Item updated = itemRepository.save(item);
        log.info("Предмет с id={} обновлен", itemId);

        return ItemMapper.toItemDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemBookingCommentDto getItem(Long userId, Long itemId) {
        log.info("Запрос на получение предмета с id={}", itemId);
        if (!(userRepository.existsById(userId))) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        LocalDateTime now = LocalDateTime.now();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден"));

        Booking lastBooking = null;
        Booking nextBooking = null;

        if (item.getOwner().getId().equals(userId)) {
            lastBooking = bookingRepository.findLastBooking(itemId, now).orElse(null);
            nextBooking = bookingRepository.findNextBooking(itemId, now).orElse(null);
        }
        return ItemMapper.toItemBookingCommentDto(item, lastBooking, nextBooking);
    }

    @Override
    public List<ItemBookingDto> getItems(Long userId) {
        log.info("Запрос на получение предметов пользователя {}", userId);
        LocalDateTime now = LocalDateTime.now();
        if (!userRepository.existsById(userId)) {
            return Collections.emptyList();
        }
        List<Item> userItems = itemRepository.findByOwnerId(userId);

        if (userItems.isEmpty()) {
            return Collections.emptyList();
        }

        return userItems.stream()
                .map(item -> {
                    Booking lastBooking = bookingRepository.findLastBooking(item.getId(), now)
                            .orElse(null);
                    Booking nextBooking = bookingRepository.findNextBooking(item.getId(), now)
                            .orElse(null);
                    return ItemMapper.toItemBookingDto(item, lastBooking, nextBooking);
                })
                .collect(Collectors.toList());
    }


    @Override
    public void delete(Long userId, Long itemId) {
        log.info("Запрос на удаление предмета с id={}", itemId);
        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }
        if (itemId == null) {
            throw new ValidationException("ID предмета не может быть null");
        }

        if (!(itemRepository.existsById(itemId))) {
            throw new NotFoundException("Предмет с id=" + itemId + " не найден");
        }
        if (!(userRepository.existsById(userId))) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        if (!(userIsOwner(userId, itemId))) {
            throw new ValidationException("Удалять вещь может только владелец");
        }

        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> searchItem(Long userId, String text) {
        log.info("Запрос на поиск предмета с текстом - {}", text);

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        return itemRepository.search(text).stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentRequest request) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id=" + itemId + " не найден"));

        boolean canComment = bookingRepository
                .existsByBookerIdAndItemIdAndEndBeforeAndStatus(
                        userId,
                        itemId,
                        LocalDateTime.now(),
                        BookingStatus.APPROVED
                );

        if (!canComment) {
            throw new ValidationException(
                    "Пользователь не может оставить комментарий: " +
                            "не арендовал эту вещь или бронирование не завершено"
            );
        }
        Comment comment = Comment.builder()
                .author(author)
                .item(item)
                .text(request.getText())
                .created(LocalDateTime.now())
                .build();
        Comment newComment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(newComment);
    }

    private void validateItem(Item item) {
        log.info("Использован метод валидации предмета");
        try {
            if (item.getName() == null || item.getName().isBlank()) {
                throw new ValidationException("Имя не должно быть пустым");
            }
            if (item.getDescription() == null || item.getDescription().isBlank()) {
                throw new ValidationException("Описание не должно быть пустым");
            }
        } catch (ValidationException e) {
            log.warn("Ошибка валидации: {}", String.valueOf(e));
            throw e;
        }
    }

    private boolean userIsOwner(Long userId, Long itemId) {
        log.info("Использован метод является ли пользователь владельцем");
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Предмет с id - {} не найден", itemId);
                    return new NotFoundException("Предмет не найден с id - " + itemId);
                });
        return Objects.equals(item.getOwner(), userId);
    }

}

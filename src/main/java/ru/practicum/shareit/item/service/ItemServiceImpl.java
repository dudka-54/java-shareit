package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Getter
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

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
        log.info("Запрос на обновление предмета с id={}", itemId);

        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }
        if (itemId == null) {
            throw new ValidationException("ID предмета не может быть null");
        }
        if (!(userIsOwner(userId, itemId))) {
            throw new ForbiddenException("Редактировать вещь может только владелец");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id=" + itemId + " не найден"));
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        ItemRequest existingRequest = null;
        if (item.getRequest() != null) {
            existingRequest = itemRequestRepository.findById(item.getRequest().getId())
                    .orElse(null);
        }

        if (patchItem.getName() != null) {
            item.setName(patchItem.getName());
        }
        if (patchItem.getAvailable() != null) {
            item.setAvailable(patchItem.getAvailable());
        }
        if (patchItem.getDescription() != null) {
            item.setDescription(patchItem.getDescription());
        }

        Item updated = itemRepository.save(item);

        return ItemMapper.toItemDto(updated);
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        log.info("Запрос на получение предмета с id={}", itemId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        ItemRequest existingRequest = null;
        if (item.getRequest() != null) {
            existingRequest = itemRequestRepository.findById(item.getRequest().getId())
                    .orElse(null);
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItems(Long userId) {
        log.info("Запрос на получение предметов пользователя {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Item> userItems = itemRepository.findByOwnerId(userId);

        if (userItems.isEmpty()) {
            return Collections.emptyList();
        }

        Set<ItemRequest> requests = userItems.stream()
                .map(Item::getRequest)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, ItemRequest> requestMap;
        if (!requests.isEmpty()) {
            requestMap = requests.stream()
                    .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));
        } else {
            requestMap = Collections.emptyMap();
        }

        return userItems.stream()
                .map(item -> {
                    ItemRequest request = requestMap.get(item.getRequest().getId());
                    return ItemMapper.toItemDto(item);
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

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id=" + itemId + " не найден"));
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        return itemRepository.search(text).stream()
                .filter(item -> item.getAvailable() == true)
                .map(item -> {
                    User owner = item.getOwner();
                    ItemRequest existingRequest = null;
                    if (item.getRequest() != null) {
                        existingRequest = itemRequestRepository.findById(item.getRequest().getId())
                                .orElse(null);
                    }
                    return ItemMapper.toItemDto(item);
                })
                .collect(Collectors.toList());
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

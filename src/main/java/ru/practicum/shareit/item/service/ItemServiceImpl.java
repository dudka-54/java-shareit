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
    public ItemDto addNewItem(Integer userId, CreateItemRequest request) {
        log.info("Запрос на добавление нового предмета");
        if (request == null) {
            throw new ConflictException("Email уже зарегистрирован");
        }
        User owner = userRepository.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest existingRequest = null;
        if (request.getRequestId() != null) {
            existingRequest = itemRequestRepository.getRequest(request.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос с id=" + request.getRequestId() + " не найден"));
        }
        Item newItem = ItemMapper.mapToItem(request);
        validateItem(newItem);
        newItem.setOwnerId(owner.getId());
        if (request.getRequestId() != null) {
            newItem.setRequestId(request.getRequestId());
        }

        return ItemMapper.toItemDto(itemRepository.save(newItem), owner, existingRequest);
    }

    @Override
    public ItemDto patchItem(Integer userId, Integer itemId, CreateItemRequest patchItem) {
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

        Item item = itemRepository.getItem(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id=" + itemId + " не найден"));
        User owner = userRepository.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        ItemRequest existingRequest = null;
        if (item.getRequestId() != null) {
            existingRequest = itemRequestRepository.getRequest(item.getRequestId())
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

        Item updated = itemRepository.update(item);

        return ItemMapper.toItemDto(updated, owner, existingRequest);
    }

    @Override
    public ItemDto getItem(Integer userId, Integer itemId) {
        log.info("Запрос на получение предмета с id={}", itemId);

        Item item = itemRepository.getItem(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден"));
        User user = userRepository.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        ItemRequest existingRequest = null;
        if (item.getRequestId() != null) {
            existingRequest = itemRequestRepository.getRequest(item.getRequestId())
                    .orElse(null);
        }
        return ItemMapper.toItemDto(item, user, existingRequest);
    }

    @Override
    public List<ItemDto> getItems(Integer userId) {
        log.info("Запрос на получение предметов");

        User user = userRepository.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Item> userItems = itemRepository.findByOwnerId(userId);

        if (userItems.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Integer, User> ownerMap = new HashMap<>();
        ownerMap.put(userId, user);

        Set<Integer> requestIds = userItems.stream()
                .map(Item::getRequestId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Integer, ItemRequest> requestMap = new HashMap<>();
        for (Integer requestId : requestIds) {
            itemRequestRepository.getRequest(requestId)
                    .ifPresent(r -> requestMap.put(requestId, r));
        }

        return userItems.stream()
                .map(item -> {
                    User owner = ownerMap.get(item.getOwnerId());
                    ItemRequest request = requestMap.get(item.getRequestId());
                    return ItemMapper.toItemDto(item, owner, request);
                })
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer userId, Integer itemId) {
        log.info("Запрос на удаление предмета с id={}", itemId);
        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }
        if (itemId == null) {
            throw new ValidationException("ID предмета не может быть null");
        }

        Item item = itemRepository.getItem(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id=" + itemId + " не найден"));
        User owner = userRepository.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        if (!(userIsOwner(userId, itemId))) {
            throw new ValidationException("Удалять вещь может только владелец");
        }

        itemRepository.delete(itemId);
    }

    @Override
    public List<ItemDto> searchItem(Integer userId, String text) {
        log.info("Запрос на поиск предмета с текстом - {}", text);

        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        User user = userRepository.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        String searchText = text.toLowerCase();

        return itemRepository.findAll().stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchText)
                        || item.getDescription().toLowerCase().contains(searchText))
                .map(item -> {
                    User owner = userRepository.getUser(item.getOwnerId())
                            .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

                    ItemRequest existingRequest = null;
                    if (item.getRequestId() != null) {
                        existingRequest = itemRequestRepository.getRequest(item.getRequestId())
                                .orElse(null);
                    }
                    return ItemMapper.toItemDto(item, owner, existingRequest);
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

    private boolean userIsOwner(Integer userId, Integer itemId) {
        log.info("Использован метод является ли пользователь владельцем");
        Item item = itemRepository.getItem(itemId)
                .orElseThrow(() -> {
                    log.warn("Предмет с id - {} не найден", itemId);
                    return new NotFoundException("Предмет не найден с id - " + itemId);
                });
        return Objects.equals(item.getOwnerId(), userId);
    }

}

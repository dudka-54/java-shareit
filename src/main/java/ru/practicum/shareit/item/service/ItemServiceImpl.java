package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
@Getter
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addNewItem(Integer userId, CreateItemRequest item) {
        Item newItem = ItemMapper.mapToItem(item);
        validateItem(newItem);
        User owner = userRepository.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (!(userIsOwner(userId, newItem.getId()))) {
            throw new ValidationException("Владельцы не совпадают");
        }
        newItem.setOwnerId(owner.getId());
        return ItemMapper.toItemDto(itemRepository.save(newItem),);
    }

    @Override
    public ItemDto patchItem(Integer userId, Integer itemId, CreateItemRequest patchItem) {
        return null;
    }

    @Override
    public ItemDto getItem(Integer userId, Integer itemId) {
        return null;
    }

    @Override
    public List<ItemDto> getItems(Integer userId) {
        return List.of();
    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public ItemDto searchItem(Integer userId, String text) {
        return null;
    }

    private void validateItem(Item item) {
        try {
            if (item.getName() == null || item.getName().isBlank()) {
                throw new ValidationException("Имя не должно быть пустым");
            }
            if (item.getDescription() == null || item.getDescription().isBlank()) {
                throw new ValidationException("Описание не должно быть пустым");
            }
            if (item.getRequestId() == null) {
                throw new ValidationException("id запроса не должно быть пустым");
            }
            if (item.getOwnerId() == null) {
                throw new ValidationException("id владельца не должно быть пустым");
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

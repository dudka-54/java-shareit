package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Getter
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public Item addNewItem(Integer userId, ItemDto itemDto) {
        return null;
    }

    @Override
    public Item patchItem(Integer userId, Integer itemId) {
        return null;
    }

    @Override
    public Item getItem(Integer userId, Integer itemId) {
        return null;
    }

    @Override
    public List<Item> getItems(Integer userId) {
        return List.of();
    }

    @Override
    public Item searchItem(Integer userId, String text) {
        return null;
    }

    private void validateItem(Item item) {

    }

    private boolean userIsOwner(Integer userId, Integer itemId) {
        log.info("Использован метод является ли пользователь владельцем");
        Item item = itemRepository.getItem(itemId)
                .orElseThrow(() -> {
                    log.warn("Предмет с id - {} не найден", itemId);
                    return new NotFoundException("Предмет не найден с id - " + itemId);
                });
        return item.getOwner().equals(userId);
    }
}

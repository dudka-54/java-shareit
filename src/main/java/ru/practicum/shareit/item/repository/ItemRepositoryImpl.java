package ru.practicum.shareit.item.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
@Getter
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Integer, Item> itemMap = new HashMap<>();

    @Override
    public List<Item> findByOwnerId(Integer ownerId) {
        return itemMap.values().stream()
                .filter(item -> Objects.equals(item.getOwnerId(), ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> findAll() {
        return itemMap.values();
    }

    @Override
    public Optional<Item> getItem(Integer itemId) {
        return Optional.ofNullable(itemMap.get(itemId));
    }

    @Override
    public Item save(Item item) {
        item.setId(getNextId());
        itemMap.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item newItem) {
        Item oldItem = itemMap.get(newItem.getId());
        if (oldItem == null) {
            log.warn("Предмет {} не найден", newItem.getId());
            throw new NotFoundException("Предмет не найден");
        }
        oldItem.setName(newItem.getName());
        oldItem.setAvailable(newItem.isAvailable());
        oldItem.setDescription(newItem.getDescription());
        return oldItem;
    }

    @Override
    public void delete(Integer id) {
        Item item = itemMap.get(id);
        if (item == null) {
            log.warn("Предмет {} не найден", id);
            throw new NotFoundException("Предмет не найден");
        }
        itemMap.remove(id);
    }

    private int getNextId() {
        int currentMaxId = getItemMap().keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

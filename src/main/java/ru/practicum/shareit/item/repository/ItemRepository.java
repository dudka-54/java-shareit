package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    public List<Item> findByOwnerId(Integer userId);

    public Collection<Item> findAll();

    public Optional<Item> getItem(Integer itemId);

    public Item save(Item item);

    public Item update(Item newItem);

    public void delete(Integer userId);
}

package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    public Item addNewItem(Integer userId, ItemDto itemDto);

    public Item patchItem(Integer userId, Integer itemId);

    public Item getItem(Integer userId, Integer itemId);

    public List<Item> getItems(Integer userId);

    public Item searchItem(Integer userId, String text);
}

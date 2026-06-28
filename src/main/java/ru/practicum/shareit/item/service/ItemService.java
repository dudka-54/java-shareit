package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemDto addNewItem(Long userId, CreateItemRequest item);

    ItemDto patchItem(Long userId, Long itemId, CreateItemRequest patchItem);

    ItemBookingCommentDto getItem(Long userId, Long itemId);

    List<ItemBookingDto> getItems(Long userId);

    void delete(Long userId, Long itemId);

    List<ItemDto> searchItem(Long userId, String text);

    CommentDto addComment(Long userId, Long itemId, CommentRequest request);
}

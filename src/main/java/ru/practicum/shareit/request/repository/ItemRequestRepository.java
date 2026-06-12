package ru.practicum.shareit.request.repository;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.Optional;

public interface ItemRequestRepository {
    Optional<ItemRequest> getRequest(Integer id);

    Collection<ItemRequest> findAll();

    ItemRequest save(ItemRequest request);

    ItemRequest update(ItemRequest request);

    void delete(Integer id);
}

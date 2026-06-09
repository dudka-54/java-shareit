package ru.practicum.shareit.request.repository;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.Optional;

public interface ItemRequestRepository {
    public Optional<ItemRequest> getRequest(Integer id);

    public Collection<ItemRequest> findAll();

    public ItemRequest save(ItemRequest request);

    public ItemRequest update(ItemRequest request);

    public void delete(Integer id);
}

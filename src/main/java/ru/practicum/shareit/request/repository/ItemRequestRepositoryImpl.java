package ru.practicum.shareit.request.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@RequiredArgsConstructor
@Repository
public class ItemRequestRepositoryImpl implements ItemRequestRepository {
    private final Map<Integer, ItemRequest> requestMap = new HashMap<>();

    @Override
    public Optional<ItemRequest> getRequest(Integer id) {
        return Optional.ofNullable(requestMap.get(id));
    }

    @Override
    public Collection<ItemRequest> findAll() {
        return requestMap.values();
    }

    @Override
    public ItemRequest save(ItemRequest request) {
        request.setId(getNextId());
        requestMap.put(request.getId(), request);
        return request;
    }

    @Override
    public ItemRequest update(ItemRequest request) {
        ItemRequest oldRequest = getRequest(request.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (request.getDescription() != null) {
            oldRequest.setDescription(request.getDescription());
        }
        return oldRequest;
    }

    @Override
    public void delete(Integer id) {
        ItemRequest request = getRequest(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        requestMap.remove(id);
    }

    private int getNextId() {
        int currentMaxId = getRequestMap().keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

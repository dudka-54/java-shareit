package ru.practicum.shareit.request.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final LocalDateTime now = LocalDateTime.now();

    @Override
    public ItemRequestDto getRequestOnId(Integer id, Integer ownerId) {
        return null;
    }

    @Override
    public Collection<ItemRequestDto> findAll(Integer ownerId) {
        return List.of();
    }

    @Override
    public UserDto save(CreateItemRequestDto newRequest, Integer ownerId) {
        return null;
    }

    @Override
    public UserDto update(CreateItemRequestDto request, Integer ownerId) {
        return null;
    }

    @Override
    public void delete(Integer id, Integer ownerId) {

    }

    private boolean userIsOwner(Integer ownerId, Integer requestId) {
        log.info("Использован метод является ли пользователь владельцем");
        ItemRequest request = requestRepository.getRequest(requestId)
                .orElseThrow(() -> {
                    log.warn("Запрос с id - {} не найден", requestId);
                    return new NotFoundException("Запрос не найден с id - " + requestId);
                });
        return Objects.equals(request.getRequestor(), ownerId);
    }
}

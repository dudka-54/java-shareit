package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.dto.CreateUpdateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto getRequestOnId(Integer id, Integer ownerId) {
        log.info("Запрос на получение запроса id={} от пользователя id={}", id, ownerId);

        User user = userRepository.getUser(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));

        // 2. Получаем запрос
        ItemRequest request = requestRepository.getRequest(id)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + id + " не найден"));

        if (!request.getRequestor().equals(ownerId)) {
            throw new ForbiddenException("Просматривать запрос может только его создатель");
        }

        return ItemRequestMapper.toDto(request, user);
    }

    @Override
    public Collection<ItemRequestDto> findAll(Integer ownerId) {
        log.info("Запрос на получение всех запросов пользователя id={}", ownerId);

        User user = userRepository.getUser(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));

        Collection<ItemRequest> allRequests = requestRepository.findAll();

        List<ItemRequestDto> userRequests = allRequests.stream()
                .filter(request -> request.getRequestor().equals(ownerId))
                .map(request -> ItemRequestMapper.toDto(request, user))
                .collect(Collectors.toList());

        log.info("Найдено {} запросов для пользователя id={}", userRequests.size(), ownerId);
        return userRequests;
    }

    @Override
    public ItemRequestDto save(CreateUpdateItemRequestDto newRequest, Integer ownerId) {
        log.info("Создание нового запроса от пользователя id={}", ownerId);

        User user = userRepository.getUser(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));

        if (newRequest.getDescription() == null || newRequest.getDescription().isBlank()) {
            throw new ValidationException("Описание запроса не может быть пустым");
        }

        ItemRequest request = new ItemRequest();
        request.setDescription(newRequest.getDescription());
        request.setRequestor(user.getId());

        ItemRequest saved = requestRepository.save(request);

        log.info("Создан запрос id={} для пользователя id={}", saved.getId(), ownerId);

        return ItemRequestMapper.toDto(saved, user);
    }

    @Override
    public ItemRequestDto update(CreateUpdateItemRequestDto requestDto, Integer ownerId) {
        log.info("Обновление запроса от пользователя id={}", ownerId);

        if (requestDto.getId() == null) {
            throw new ValidationException("ID запроса не может быть null");
        }

        User user = userRepository.getUser(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));

        ItemRequest existingRequest = requestRepository.getRequest(requestDto.getId())
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestDto.getId() + " не найден"));

        if (!existingRequest.getRequestor().equals(ownerId)) {
            throw new ForbiddenException("Редактировать запрос может только его создатель");
        }

        if (requestDto.getDescription() != null && !requestDto.getDescription().isBlank()) {
            existingRequest.setDescription(requestDto.getDescription());
        }

        ItemRequest updated = requestRepository.update(existingRequest);

        log.info("Обновлён запрос id={}", updated.getId());

        return ItemRequestMapper.toDto(updated, user);
    }

    @Override
    public void delete(Integer id, Integer ownerId) {
        log.info("Удаление запроса id={} пользователем id={}", id, ownerId);

        userRepository.getUser(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));

        ItemRequest request = requestRepository.getRequest(id)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + id + " не найден"));

        if (!request.getRequestor().equals(ownerId)) {
            throw new ForbiddenException("Удалять запрос может только его создатель");
        }

        requestRepository.delete(id);

        log.info("Удалён запрос id={}", id);
    }
}
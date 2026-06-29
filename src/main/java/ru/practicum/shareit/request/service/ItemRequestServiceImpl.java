package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto getRequestOnId(Long id, Long ownerId) {
        log.info("Запрос на получение запроса id={} от пользователя id={}", id, ownerId);

        if (!(userRepository.existsById(ownerId))) {
            throw new NotFoundException("Пользователь с id " + ownerId + " не найден");
        }
        ItemRequest request = requestRepository
                .findByIdWithItems(id)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + id + " не найден"));

        return ItemRequestMapper.toDto(request);
    }

    @Override
    public Collection<ItemRequestDto> findAllWithoutOwner(Long ownerId) {
        log.info("Запрос на получение всех запросов без запросов владельца id={}", ownerId);

        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        List<ItemRequest> requests = requestRepository
                .findAllByOtherUsersWithItems(ownerId);

        return requests.stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemRequestDto> findAllByOwner(Long ownerId) {
        log.info("Запрос на получение всех запросов пользователя id={}", ownerId);

        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        List<ItemRequest> requests = requestRepository
                .findAllByRequestorIdWithItems(ownerId);

        return requests.stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto save(CreateItemRequestDto newRequest, Long ownerId) {
        log.info("Создание нового запроса от пользователя id={}", ownerId);

        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));

        if (newRequest.getDescription() == null || newRequest.getDescription().isBlank()) {
            throw new ValidationException("Описание запроса не может быть пустым");
        }

        ItemRequest request = ItemRequest.builder()
                .description(newRequest.getDescription())
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        ItemRequest saved = requestRepository.save(request);
        return ItemRequestMapper.toDto(saved);
    }


    @Override
    public void delete(Long id, Long ownerId) {
        log.info("Удаление запроса id={} пользователем id={}", id, ownerId);

        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + ownerId + " не найден"));

        ItemRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + id + " не найден"));

        if (!request.getRequestor().getId().equals(ownerId)) {
            throw new ForbiddenException("Удалять запрос может только его создатель");
        }

        requestRepository.deleteById(id);

        log.info("Удалён запрос id={}", id);
    }
}
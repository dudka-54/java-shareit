package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Getter
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto getUserOnId(Integer id) {
        log.info("Запрос на получение конкретного пользователя по id - {}", id);
        User user = userRepository.getUser(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public Collection<UserDto> findAll() {
        log.info("Запрос на получение всех пользователей");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto save(CreateUserRequest user) {
        log.info("Запрос на создание пользователя");
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("Email уже зарегистрирован");
        }
        User newUser = UserMapper.mapToUser(user);
        validationUser(newUser);
        return UserMapper.toUserDto(userRepository.save(newUser));
    }

    @Override
    public UserDto patch(UpdateUserRequest request, Integer userId) {
        log.info("Запрос на обновление пользователя с id={}", userId);

        if (userId == null) {
            throw new ValidationException("ID пользователя не может быть null");
        }

        User existingUser = userRepository.getUser(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        if (request.getName() != null) {
            existingUser.setName(request.getName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ConflictException("Email уже используется другим пользователем");
            }
            existingUser.setEmail(request.getEmail());
        }

        User updatedUser = userRepository.patch(existingUser);

        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void delete(Integer id) {
        if (id == null) {
            log.warn("Пользователь {} не найден", id);
            throw new NotFoundException("Пользователь не найден по id - " + id);
        }
        userRepository.delete(id);
    }

    private void validationUser(User user) {
        try {
            if (user.getEmail() == null || user.getEmail().isBlank()) {
                throw new ValidationException("Email не должен быть пустым или содержать только пробелы");
            }
            if (!(user.getEmail().contains(String.valueOf('@')))) {
                throw new ValidationException("Email должен содержать символ - @ ");
            }
            if (user.getName() == null || user.getName().isBlank()) {
                throw new ValidationException("Имя не должно быть пустым");
            }
        } catch (ValidationException e) {
            log.warn("Ошибка валидации: {}", String.valueOf(e));
            throw e;
        }
    }
}

package ru.practicum.shareit.user.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@AllArgsConstructor
@Getter
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Optional<User> getUser(Integer id) {
        return Optional.ofNullable(users.remove(id));
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User save(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        User oldUser = getUser(newUser.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        return oldUser;
    }

    @Override
    public void delete(Integer id) {
        User user = users.get(id);
        users.remove(id);
    }

    private int getNextId() {
        int currentMaxId = getUsers().keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

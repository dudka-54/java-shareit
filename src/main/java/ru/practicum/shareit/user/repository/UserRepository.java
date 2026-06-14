package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    Optional<User> getUser(Integer id);

    Collection<User> findAll();

    User save(User user);

    User patch(User newUser);

    void delete(Integer id);

    boolean existsByEmail(String email);
}

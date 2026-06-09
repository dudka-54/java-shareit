package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    public Optional<User> getUser(Integer id);

    public Collection<User> findAll();

    public User save(User user);

    public User update(User newUser);

    public void delete(Integer id);
}

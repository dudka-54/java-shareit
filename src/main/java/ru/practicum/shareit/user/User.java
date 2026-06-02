package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;

/**
 * TODO Sprint add-controllers.
 */
public class User {
    private Integer id;

    private String name;

    @Email
    private String email;
}

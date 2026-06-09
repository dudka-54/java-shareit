package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;

    private String name;

    @Email
    private String email;
}

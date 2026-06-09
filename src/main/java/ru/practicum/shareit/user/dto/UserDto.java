package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotBlank
    private Integer id;

    @NotBlank
    private String name;

    @Email(message = "Неверный формат email")
    @NotBlank(message = "Email не может быть пустым")
    private String email;
}

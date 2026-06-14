package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
    @Email(message = "Неверный формат email")
    @NotBlank(message = "Email не может быть пустым")
    private String email;
}

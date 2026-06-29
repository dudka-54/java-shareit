package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateItemRequestDto {
    @NotBlank(message = "Описание не должно быть пустым")
    private String description;
}
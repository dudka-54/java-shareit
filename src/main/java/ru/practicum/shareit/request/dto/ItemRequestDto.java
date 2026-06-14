package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@Builder
public class ItemRequestDto {
    @NotNull(message = "id не должен быть null")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    @NotBlank(message = "Описание не должно быть пустым")
    private String description;

    @NotNull(message = "запросчик не должен быть null")
    private UserDto requestor;

    @PastOrPresent(message = "Дата создания не может быть в будущем")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime created;
}

package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    private String name;
    private String description;
    private Boolean available;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UserDto owner;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ItemRequest request;
}

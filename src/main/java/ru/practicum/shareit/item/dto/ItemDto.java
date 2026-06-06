package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */

@Data
public class ItemDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    private String name;
    private String description;
    private boolean available;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private User owner;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ItemRequest request;
}

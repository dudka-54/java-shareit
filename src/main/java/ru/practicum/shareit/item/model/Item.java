package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Item {
    private Integer id;
    private String name;
    private String description;
    private boolean available;
    private Integer ownerId;
    private Integer requestId;
}

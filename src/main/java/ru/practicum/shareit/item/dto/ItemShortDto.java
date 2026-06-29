package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemShortDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long ownerId;
}

package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime start;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime end;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BookingStatus status;

    private UserDto booker;
    private ItemDto item;

}

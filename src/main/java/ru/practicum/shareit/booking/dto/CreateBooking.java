package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateBooking {
    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    @NotBlank
    private Long itemId;
    private Long bookerId;
}

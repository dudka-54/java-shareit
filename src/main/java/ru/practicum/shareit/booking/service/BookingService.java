package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBooking;

import java.util.List;

public interface BookingService {
    BookingDto addNewBooking(Long userId, CreateBooking newBooking);

    BookingDto approveOrReject(Long ownerId, Long bookingId, String status);

    BookingDto getBooking(Long bookingId);

    List<BookingDto> getBookingsInOwnerAndState(Long ownerId, String state);

    List<BookingDto> getBookingsByBookerAndState(Long bookerId, String state);
}

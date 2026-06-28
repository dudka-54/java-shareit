package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.booking.service.BookingService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> addBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @Valid @RequestBody CreateBooking createBooking) {

        BookingDto bookingDto = bookingService.addNewBooking(userId, createBooking);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId,
            @RequestParam Boolean approved) {

        String status = approved ? "APPROVED" : "REJECTED";
        BookingDto bookingDto = bookingService.approveOrReject(userId, bookingId, status);
        return ResponseEntity.ok(bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId) {

        BookingDto bookingDto = bookingService.getBooking(bookingId);
        return ResponseEntity.ok(bookingDto);
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getBookingsByBooker(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state) {

        List<BookingDto> bookings = bookingService.getBookingsByBookerAndState(userId, state);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getBookingsByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") String state) {

        List<BookingDto> bookings = bookingService.getBookingsInOwnerAndState(userId, state);
        return ResponseEntity.ok(bookings);
    }
}

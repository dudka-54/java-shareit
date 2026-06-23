package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto addNewBooking(Long userId, CreateBooking newBooking) {
        if (newBooking == null) {
            throw new ConflictException("Не может быть null");
        }
        if (userId == null) {
            throw new NotFoundException("Поле userId не может быть null");
        }
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(newBooking.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));
        Booking booking = BookingMapper.mapToBooking(newBooking);
        if (item.getOwner().getId().equals(userId)) {
            throw new ConflictException("Владелец не может забронировать свою вещь");
        }
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        validateBooking(booking);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto approveOrReject(Long ownerId, Long bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Сущность бронирования не найдена"));
        if (userIsOwner(ownerId, booking.getItem().getId())) {
            throw new ConflictException("Только пользователь может задавать статус бронирования");
        }
        BookingStatus bookingStatus = mapStringToStatus(status);
        if (bookingStatus != BookingStatus.APPROVED && bookingStatus != BookingStatus.REJECTED) {
            throw new ConflictException("Используя данный метод enum должен быть APPROWED или REJECTED");
        }
        booking.setStatus(bookingStatus);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long bookingId) {
        if (bookingId == null) {
            throw new NotFoundException("Поле bookingId не может быть null");
        }
        return BookingMapper.toBookingDto(bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Сущность бронирования не найдена")));
    }


    @Override
    public List<BookingDto> getBookingsByBookerAndState(Long bookerId, String state) {
        if (bookerId == null) {
            throw new NotFoundException("Поле ownerId не может быть null");
        }
        LocalDateTime now = LocalDateTime.now();
        if (state.equalsIgnoreCase("CURRENT")) {
            return bookingRepository.findCurrentByBooker(bookerId, now).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (state.equalsIgnoreCase("PAST")) {
            return bookingRepository.findPastByBooker(bookerId, now).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (state.equalsIgnoreCase("FUTURE")) {
            return bookingRepository.findFutureByBooker(bookerId, now).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else {
            BookingStatus bookingStatus = mapStringToStatus(state);
            if (bookingStatus == null) { //ALL
                return bookingRepository.findByBookerIdOrderByStartDesc(bookerId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            return bookingRepository.findByBookerIdOrderByStartDesc(bookerId).stream()
                    .filter(booking -> booking.getStatus() == bookingStatus)
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<BookingDto> getBookingsInOwnerAndState(Long ownerId, String state) {
        if (ownerId == null) {
            throw new NotFoundException("Поле ownerId не может быть null");
        }
        LocalDateTime now = LocalDateTime.now();
        if (state.equalsIgnoreCase("CURRENT")) {
            return bookingRepository.findCurrentByOwner(ownerId, now).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (state.equalsIgnoreCase("PAST")) {
            return bookingRepository.findPastByOwner(ownerId, now).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else if (state.equalsIgnoreCase("FUTURE")) {
            return bookingRepository.findFutureByOwner(ownerId, now).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        } else {
            BookingStatus bookingStatus = mapStringToStatus(state);
            if (bookingStatus == null) { //ALL
                return bookingRepository.findByItem_OwnerIdOrderByStartDesc(ownerId).stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            }
            return bookingRepository.findByItem_OwnerIdOrderByStartDesc(ownerId).stream()
                    .filter(booking -> booking.getStatus() == bookingStatus)
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
    }

    private void validateBooking(Booking booking) {
        log.info("Использован метод валидации предмета");
        try {
            if (booking.getStart() == null) {
                throw new ValidationException("start не должно быть null");
            }
            if (booking.getEnd() == null) {
                throw new ValidationException("end не должно быть null");
            }
            if (booking.getStatus() == null) {
                throw new ValidationException("Статус не должен быть пустым");
            }
            if (booking.getItem() == null) {
                throw new ValidationException("Поле предмета не должно быть null");
            }
            if (booking.getBooker() == null) {
                throw new ValidationException("Имя не должно быть пустым");
            }
            if (booking.getStart().isAfter(booking.getEnd()) ||
                    booking.getStart().isEqual(booking.getEnd())) {
                throw new ConflictException("Дата начала должна быть раньше даты окончания");
            }
            if (booking.getStart().isBefore(LocalDateTime.now())) {
                throw new ConflictException("Дата начала не может быть в прошлом");
            }
            if (!booking.getItem().getAvailable()) {
                throw new ConflictException("Предмет недоступен для бронирования");
            }
        } catch (ValidationException | ConflictException e) {
            log.warn("Ошибка валидации: {}", String.valueOf(e));
            throw e;
        }
    }

    private BookingStatus mapStringToStatus(String state) {
        BookingStatus bookingStatus;
        if (state == null || state.equalsIgnoreCase("ALL")) {
            return null;
        }
        try {
            return bookingStatus = BookingStatus.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Не удалось привести String к enum");
            throw new ValidationException("Неизвестный статус: " + state);
        }
    }

    private boolean userIsOwner(Long userId, Long itemId) {
        log.info("Использован метод является ли пользователь владельцем");
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> {
                    log.warn("Предмет с id - {} не найден", itemId);
                    return new NotFoundException("Предмет не найден с id - " + itemId);
                });
        return Objects.equals(item.getOwner(), userId);
    }
}

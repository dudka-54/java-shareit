package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
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
        log.info("Запрос на создание нового бронирования");
        if (userId == null) {
            log.warn("Поле userId не может быть null");
            throw new NotFoundException("Поле userId не может быть null");
        }
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(newBooking.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));
        Booking booking = BookingMapper.mapToBooking(newBooking);
        if (userIsOwner(userId, item.getId())) {
            log.warn("Владелец не может забронировать свою вещь");
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
        log.info("Запрос на подтверждение или отказ бронирования ownerId - {} bookingId - {} status {}", ownerId, bookingId, status);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Сущность бронирования не найдена"));

        if (!userIsOwner(ownerId, booking.getItem().getId())) {
            log.warn("Только владелец может менять статус бронирования");
            throw new ForbiddenException("Только владелец может менять статус бронирования");
        }

        BookingStatus bookingStatus = mapStringToStatus(status);
        if (bookingStatus != BookingStatus.APPROVED && bookingStatus != BookingStatus.REJECTED) {
            log.warn("Используя данный метод enum должен быть APPROWED или REJECTED");
            throw new ConflictException("Используя данный метод enum должен быть APPROWED или REJECTED");
        }

        if (booking.getStatus() == BookingStatus.APPROVED || booking.getStatus() == BookingStatus.REJECTED) {
            log.warn("Статус бронирования уже установлен");
            throw new ConflictException("Статус бронирования уже установлен");
        }

        booking.setStatus(bookingStatus);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(Long bookingId) {
        log.info("Запрос на получение бронирования по bookingId - {}", bookingId);
        if (bookingId == null) {
            throw new NotFoundException("Поле bookingId не может быть null");
        }
        return BookingMapper.toBookingDto(bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Сущность бронирования не найдена")));
    }


    @Override
    public List<BookingDto> getBookingsByBookerAndState(Long bookerId, String state) {
        log.info("Получение списка всех бронирований текущего пользователя");
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
            if (bookingStatus == null) {
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
        log.info("Получение списка бронирований для всех вещей текущего пользователя");
        if (ownerId == null) {
            throw new NotFoundException("Поле ownerId не может быть null");
        }
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с id " + ownerId + " не найден");
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
            if (!booking.getItem().getAvailable()) {
                throw new ValidationException("Предмет недоступен для бронирования");
            }
            if (booking.getBooker() == null) {
                throw new ValidationException("Имя не должно быть пустым");
            }
            if (booking.getStart().isAfter(booking.getEnd()) ||
                    booking.getStart().isEqual(booking.getEnd())) {
                throw new ValidationException("Дата начала должна быть раньше даты окончания");
            }

        } catch (ValidationException e) {
            log.warn("Ошибка валидации: {}", String.valueOf(e));
            throw e;
        }
    }

    private BookingStatus mapStringToStatus(String state) {
        log.info("Использован метод преобразования объекта строки - {} в Enum ", state);
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
        log.info("Использован запрос является ли пользователь владельцем");
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет не найден с id - " + itemId));
        return item.getOwner().getId().equals(userId);
    }
}

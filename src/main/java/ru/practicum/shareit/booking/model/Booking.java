package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Builder
@Getter
@Setter
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDateTime start;

    @Column(nullable = false)
    private LocalDateTime end;

    @ManyToOne
    @JoinColumn(nullable = false, name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(nullable = false, name = "booker_id")
    private User booker;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status")
    private BookingStatus status;
}

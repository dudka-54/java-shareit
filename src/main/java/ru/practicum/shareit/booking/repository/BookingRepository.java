package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByItem_OwnerIdOrderByStartDesc(Long ownerId);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start <= :now " +
            "AND b.end > :now " +
            "ORDER BY b.start DESC ")
    List<Booking> findCurrentByBooker(@Param("bookerId") Long bookerId,
                                      @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start > :now " +
            "AND b.end > :now " +
            "ORDER BY b.start DESC ")
    List<Booking> findFutureByBooker(@Param("bookerId") Long bookerId,
                                     @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.booker.id = :bookerId " +
            "AND b.start < :now " +
            "AND b.end < :now " +
            "ORDER BY b.start DESC ")
    List<Booking> findPastByBooker(@Param("bookerId") Long bookerId,
                                   @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start <= :now " +
            "AND b.end > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentByOwner(@Param("ownerId") Long ownerId,
                                     @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.end < :now " +
            "ORDER BY b.start DESC")
    List<Booking> findPastByOwner(@Param("ownerId") Long ownerId,
                                  @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :ownerId " +
            "AND b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureByOwner(@Param("ownerId") Long ownerId,
                                    @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.end < :now " +
            "ORDER BY b.end DESC " +
            "LIMIT 1")
    Optional<Booking> findLastBooking(@Param("itemId") Long itemId,
                                      @Param("now") LocalDateTime now);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND b.start > :now " +
            "ORDER BY b.start ASC " +
            "LIMIT 1")
    Optional<Booking> findNextBooking(@Param("itemId") Long itemId,
                                      @Param("now") LocalDateTime now);

    boolean existsByBookerIdAndItemIdAndEndBeforeAndStatus(
            Long bookerId,
            Long itemId,
            LocalDateTime endBefore,
            BookingStatus status);

}


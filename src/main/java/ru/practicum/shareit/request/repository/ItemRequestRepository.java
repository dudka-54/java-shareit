package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT DISTINCT ir FROM ItemRequest ir " +
            "LEFT JOIN FETCH ir.items i " +
            "WHERE NOT ir.requestor.id = :ownerId " +
            "ORDER BY ir.created DESC")
    List<ItemRequest> findAllByOtherUsersWithItems(@Param("ownerId") Long ownerId);

    @Query("SELECT DISTINCT ir FROM ItemRequest ir " +
            "LEFT JOIN FETCH ir.items i " +
            "WHERE ir.requestor.id = :ownerId " +
            "ORDER BY ir.created DESC")
    List<ItemRequest> findAllByRequestorIdWithItems(@Param("ownerId") Long ownerId);


    Optional<ItemRequest> findByRequestorIdAndId(@Param("requestorId") Long requestorId,
                                                 @Param("id") Long id);

    @Query("SELECT DISTINCT ir FROM ItemRequest ir " +
            "LEFT JOIN FETCH ir.items i " +
            "WHERE ir.id = :requestId")
    Optional<ItemRequest> findByIdWithItems(@Param("requestId") Long requestId);
}

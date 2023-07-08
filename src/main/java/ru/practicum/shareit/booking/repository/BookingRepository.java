package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findAllByIdAndStartBeforeAndEndAfterOrderByStartAsc(
            int bookerId, LocalDateTime start, LocalDateTime end
    );

    List<Booking> findAllByIdAndStartAfterOrderByStartAsc(int bookerId, LocalDateTime start);

    List<Booking> findAllByIdAndStartBeforeAndEndBeforeOrderByStartAsc(
            int bookerId, LocalDateTime start, LocalDateTime end
    );

    List<Booking> findAllByIdAndStatusOrderByStartAsc(int bookerId, BookingStatus bookingStatus);

    List<Booking> findAllByIdOrderByStartAsc(int bookerId);

    List<Booking> findAllByItemIdInAndStartBeforeAndEndAfterOrderByStartAsc(
            List<Integer> itemIds, LocalDateTime start, LocalDateTime end
    );

    List<Booking> findAllByItemIdInAndStartAfterOrderByStartAsc(List<Integer> itemIds, LocalDateTime start);

    List<Booking> findAllByItemIdInAndStartBeforeAndEndBeforeOrderByStartAsc(
            List<Integer> itemIds, LocalDateTime start, LocalDateTime end
    );

    List<Booking> findAllByItemIdInAndStatusOrderByStartAsc(List<Integer> itemIds, BookingStatus bookingStatus);

    List<Booking> findAllByItemIdInOrderByStartAsc(List<Integer> itemIds);

    @Query("FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND (b.start BETWEEN ?2 AND ?3 " +
            "OR b.end BETWEEN  ?2 AND ?3) " +
            "ORDER BY b.start desc"
    )
    List<Booking> findAllByItemIdAndStartBetweenAndEndBetween(int itemId, LocalDateTime start, LocalDateTime end);
}
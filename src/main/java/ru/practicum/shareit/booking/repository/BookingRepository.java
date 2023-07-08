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

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            int bookerId, LocalDateTime start, LocalDateTime end
    );

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(int bookerId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndBeforeOrderByStartDesc(
            int bookerId, LocalDateTime start, LocalDateTime end
    );

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(int bookerId, BookingStatus bookingStatus);

    List<Booking> findAllByBookerIdOrderByStartDesc(int bookerId);

    List<Booking> findAllByItemIdInAndStartBeforeAndEndAfterOrderByStartDesc(
            List<Integer> itemIds, LocalDateTime start, LocalDateTime end
    );

    List<Booking> findAllByItemIdInAndStartAfterOrderByStartDesc(List<Integer> itemIds, LocalDateTime start);

    List<Booking> findAllByItemIdInAndStartBeforeAndEndBeforeOrderByStartDesc(
            List<Integer> itemIds, LocalDateTime start, LocalDateTime end
    );

    List<Booking> findAllByItemIdInAndStatusOrderByStartDesc(List<Integer> itemIds, BookingStatus bookingStatus);

    List<Booking> findAllByItemIdInOrderByStartDesc(List<Integer> itemIds);

    @Query("FROM Booking b " +
            "WHERE b.item.id = ?1 " +
            "AND (b.start BETWEEN ?2 AND ?3 " +
            "OR b.end BETWEEN  ?2 AND ?3) " +
            "ORDER BY b.start desc"
    )
    List<Booking> findAllByItemIdAndStartBetweenAndEndBetween(int itemId, LocalDateTime start, LocalDateTime end);
}
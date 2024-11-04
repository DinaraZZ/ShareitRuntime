package com.practice.shareitzeinolla.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingJpaRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByUserIdOrderByFromDateDesc(Long userId);

    List<Booking> findAllByUserIdAndToDateAfterOrderByFromDateDesc(Long userId, LocalDateTime date);

    List<Booking> findAllByUserIdAndToDateBeforeOrderByFromDateDesc(Long userId, LocalDateTime date);

    List<Booking> findAllByUserIdAndFromDateAfterOrderByFromDateDesc(Long userId, LocalDateTime date);

    List<Booking> findAllByUserIdAndStatusOrderByFromDateDesc(Long userId, BookingStatus status);


    List<Booking> findAllByItem_UserIdOrderByFromDateDesc(Long userId);

    List<Booking> findAllByItem_UserIdAndToDateAfterOrderByFromDateDesc(Long userId, LocalDateTime date);

    List<Booking> findAllByItem_UserIdAndToDateBeforeOrderByFromDateDesc(Long userId, LocalDateTime date);

    List<Booking> findAllByItem_UserIdAndFromDateAfterOrderByFromDateDesc(Long userId, LocalDateTime date);

    List<Booking> findAllByItem_UserIdAndStatusOrderByFromDateDesc(Long userId, BookingStatus status);


    Optional<Booking> findByUserIdAndItemId(Long userId, Long itemId);

    /*@Query(value = """
            select b from Booking b
            where b.item.id = :itemId
            and b.toDate < CURRENT_TIMESTAMP
            order by b.toDate desc
            limit 1""")
    Optional<Booking> findLastBooking(@Param("itemId") Long itemId);*/
    @Query(value = """
            select b from Booking b
            where b.item.id = :itemId
            and b.fromDate < :date
            and b.status = :status
            order by b.fromDate desc
            limit 1""")
    Optional<Booking> findLastBooking(@Param("itemId") Long itemId, BookingStatus status, LocalDateTime date);

    @Query(value = """
    select b from Booking b
    where b.item.id = :itemId
    and (b.fromDate < :toDate and b.toDate > :fromDate) order by b.fromDate desc limit 1
    """)
    Optional<Booking> findBookingBetweenDates(
            @Param("itemId") Long itemId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}

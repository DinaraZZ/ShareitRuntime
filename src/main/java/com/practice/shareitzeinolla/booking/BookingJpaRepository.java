package com.practice.shareitzeinolla.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

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
}

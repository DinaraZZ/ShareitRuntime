package com.practice.shareitzeinolla.booking;

import com.practice.shareitzeinolla.exception.NotFoundException;
import com.practice.shareitzeinolla.exception.ValidationException;
import com.practice.shareitzeinolla.item.Item;
import com.practice.shareitzeinolla.item.ItemJpaRepository;
import com.practice.shareitzeinolla.user.User;
import com.practice.shareitzeinolla.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingJpaService {
    private final BookingJpaRepository bookingRepository;
    private final UserJpaRepository userRepository;
    private final ItemJpaRepository itemRepository;

    public Booking create(Booking booking, Long userId) { // ??
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (booking.getItem() == null) { // ?
            throw new ValidationException("Товар не может быть пустым");
        }

        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new NotFoundException("Товар не найден"));

        if (!item.getAvailable()) {
            throw new ValidationException("Товар недоступен.");
        }

        if (booking.getFromDate() == null) { // ?
            throw new ValidationException("Даты не должны быть пустыми.");
        }

        if (booking.getFromDate().isAfter(booking.getToDate()) ||
                booking.getFromDate().equals(booking.getToDate())) {
            throw new ValidationException(
                    "Дата начала бронирования должна быть раньше даты окончания бронирования");
        }

        booking.setUser(user);
        booking.setStatus(BookingStatus.WAITING);

        bookingRepository.save(booking);
        return booking;
    }

    public Booking update(Long bookingId, Long userId, Boolean approved) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено."));

        if (!booking.getItem().getUser().equals(user)) {
            throw new ValidationException("Менять статус брони может только владелец товара.");
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        bookingRepository.save(booking);

        return booking;
    }

    public Booking findById(Long bookingId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено."));

        if (!booking.getUser().equals(user) && !booking.getItem().getUser().equals(user)) {
            throw new ValidationException("У данного пользователя нет доступа к этому бронированию.");
        }

        return booking;
    }

    public List<Booking> findAllByUser(Long userId, String state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Booking> bookings = Collections.emptyList();
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByUserIdOrderByFromDateDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByUserIdAndToDateAfterOrderByFromDateDesc(userId, LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findAllByUserIdAndToDateBeforeOrderByFromDateDesc(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByUserIdAndFromDateAfterOrderByFromDateDesc(userId, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByUserIdAndStatusOrderByFromDateDesc(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByUserIdAndStatusOrderByFromDateDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                break;
        }

        return bookings;
    }

    public List<Booking> findAllByOwner(Long userId, String state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Booking> bookings = Collections.emptyList();
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItem_UserIdOrderByFromDateDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByItem_UserIdAndToDateAfterOrderByFromDateDesc(userId, LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItem_UserIdAndToDateBeforeOrderByFromDateDesc(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItem_UserIdAndFromDateAfterOrderByFromDateDesc(userId, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByItem_UserIdAndStatusOrderByFromDateDesc(userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByItem_UserIdAndStatusOrderByFromDateDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                break;
        }

        return bookings;
    }
}

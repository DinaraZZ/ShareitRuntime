package com.practice.shareitzeinolla.booking;

import com.practice.shareitzeinolla.exception.NotFoundException;
import com.practice.shareitzeinolla.exception.ValidationException;
import com.practice.shareitzeinolla.item.Item;
import com.practice.shareitzeinolla.item.ItemJpaRepository;
import com.practice.shareitzeinolla.user.User;
import com.practice.shareitzeinolla.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

        Item item = itemRepository.findById(booking.getItem().getId())
                .orElseThrow(() -> new NotFoundException("Товар не найден"));
        booking.setItem(item);

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

//        System.out.println(booking);
//        System.out.println(bookingRepository.findBookingBetweenDates(
//                item.getId(), booking.getFromDate(), booking.getToDate()));

        Booking itemAlreadyBooked = bookingRepository.findBookingBetweenDates(
                        item.getId(), booking.getFromDate(), booking.getToDate())
                .orElse(null);

        System.out.println(itemAlreadyBooked);
        if (itemAlreadyBooked == null) {
            booking.setUser(user);
            booking.setStatus(BookingStatus.WAITING);

            bookingRepository.save(booking);
        } else {
            throw new ValidationException("Данный товар уже забронирован на эти даты.");
        }

        return booking;
    }

    public Booking update(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено."));

        if (!booking.getItem().getUser().getId().equals(userId)) {
            throw new ValidationException("Менять статус брони может только владелец товара.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

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

    public List<Booking> findAllByUser(Long userId, String state, Integer fromIndex, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Pageable pageable = PageRequest.of(fromIndex / size, size);

        List<Booking> bookings = Collections.emptyList();
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByUserIdOrderByFromDateDesc(userId, pageable);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByUserIdAndFromDateBeforeAndToDateAfterOrderByFromDateDesc(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByUserIdAndToDateBeforeOrderByFromDateDesc(userId, LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByUserIdAndFromDateAfterOrderByFromDateDesc(userId, LocalDateTime.now(), pageable);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByUserIdAndStatusOrderByFromDateDesc(userId, BookingStatus.WAITING, pageable);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByUserIdAndStatusOrderByFromDateDesc(userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                break;
        }

        return bookings;
    }

    public List<Booking> findAllByOwner(Long userId, String state, Integer fromIndex, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Pageable pageable = PageRequest.of(fromIndex / size, size);

        List<Booking> bookings = Collections.emptyList();
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItem_UserIdOrderByFromDateDesc(userId, pageable);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByItem_UserIdAndFromDateBeforeAndToDateAfterOrderByFromDateDesc(userId, LocalDateTime.now(), LocalDateTime.now(), pageable);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItem_UserIdAndToDateBeforeOrderByFromDateDesc(userId, LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItem_UserIdAndFromDateAfterOrderByFromDateDesc(userId, LocalDateTime.now(), pageable);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByItem_UserIdAndStatusOrderByFromDateDesc(userId, BookingStatus.WAITING, pageable);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByItem_UserIdAndStatusOrderByFromDateDesc(userId, BookingStatus.REJECTED, pageable);
                break;
            default:
                break;
        }

        return bookings;
    }
}

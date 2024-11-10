package com.practice.shareitzeinolla.booking;

import com.practice.shareitzeinolla.booking.dto.BookingCreateDto;
import com.practice.shareitzeinolla.booking.dto.BookingMapper;
import com.practice.shareitzeinolla.booking.dto.BookingResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static com.practice.shareitzeinolla.util.RequestConstants.USER_HEADER;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
//    private static final Logger log = LoggerFactory.getLogger(BookingController.class);
    private final BookingJpaService bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto create(@RequestHeader(name = USER_HEADER) Long userId,
                                     @Valid @RequestBody BookingCreateDto booking) {
        log.debug("Получен запрос POST userId: {}, /bookings: {}", userId, booking);

        return bookingMapper.toResponse(
                bookingService.create(bookingMapper.fromBookingCreate(booking), userId));
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto update(@RequestHeader(name = USER_HEADER) Long userId,
                                     @PathVariable Long bookingId,
                                     @RequestParam(name = "approved") Boolean approved) {
        log.debug("Получен запрос PATCH userId: {}, /bookings/{}?approved={}",
                userId, bookingId, approved);

        return bookingMapper.toResponse(
                bookingService.update(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingResponseDto findById(@RequestHeader(name = USER_HEADER) Long userId,
                                       @PathVariable Long bookingId) {
        log.debug("Получен запрос GET userId: {}, /bookings/{}", userId, bookingId);

        return bookingMapper.toResponse(
                bookingService.findById(bookingId, userId));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<BookingResponseDto> findAllByUser(
            @RequestHeader(name = USER_HEADER) Long userId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(name = "from", defaultValue = "0") Integer fromIndex,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.debug("Получен запрос GET userId: {}, /bookings?state={}", userId, state);

        return bookingService.findAllByUser(userId, state, fromIndex, size).stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public Collection<BookingResponseDto> findAllByOwner(
            @RequestHeader(name = USER_HEADER) Long userId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(name = "from", defaultValue = "0") Integer fromIndex,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.debug("Получен запрос GET userId: {}, /bookings/owner?state={}", userId, state);

        return bookingService.findAllByOwner(userId, state, fromIndex, size).stream()
                .map(bookingMapper::toResponse)
                .toList();
    }
}

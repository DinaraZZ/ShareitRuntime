package com.practice.shareitzeinolla.booking;

import com.practice.shareitzeinolla.exception.NotFoundException;
import com.practice.shareitzeinolla.item.Item;
import com.practice.shareitzeinolla.item.ItemJpaRepository;
import com.practice.shareitzeinolla.user.User;
import com.practice.shareitzeinolla.user.UserJpaRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@AutoConfigureTestDatabase
@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    BookingJpaRepository bookingRepository;

    @Autowired
    ItemJpaRepository itemRepository;

    @Autowired
    private UserJpaRepository userRepository;

    private Item item;
    private User user;
    private User owner;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        item = new Item("BRepositoryTestSave1", "BRepositoryTestSave1", true);
        itemRepository.save(item);

        if (testInfo.getTags().contains("requiresUser")) {
            user = new User("BRepositoryTestUser", "brepositoryuser@test.com");
            userRepository.save(user);
        }
        if (testInfo.getTags().contains("requiresOwner")) {
            owner = new User("BRepositoryTestUser2", "brepositoryuser2@test.com");
            userRepository.save(owner);

            item.setUser(owner);
            itemRepository.save(item);
        }
    }

    @Test
    void saveBooking_shouldSave_whenBookingCorrect() {
        Booking booking = new Booking(item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1));
        bookingRepository.save(booking);

        Booking savedBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        Assertions.assertEquals(booking.getItem(), savedBooking.getItem());
        Assertions.assertEquals(booking.getFromDate(), savedBooking.getFromDate());
        Assertions.assertEquals(booking.getToDate(), savedBooking.getToDate());
    }

    @Test
    void findById_shouldFind_whenBookingExists() {
        Booking booking = new Booking(item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1));
        bookingRepository.save(booking);

        Booking foundBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        Assertions.assertEquals(booking.getId(), foundBooking.getId());
    }

    @Test
    void findById_shouldNotFind_whenBookingDoesNotExist() {
        String expectedMessage = "Бронирование не найдено";
        Long notExistingId = 100L;

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingRepository.findById(notExistingId)
                        .orElseThrow(() -> new NotFoundException(expectedMessage))
        );

        Assertions.assertEquals(exception.getMessage(), expectedMessage);
    }

    @Test
    void findAll_shouldReturn_whenBookingsExist() {
        List<Booking> bookings = List.of(
                new Booking(item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1)),
                new Booking(item, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3))
        );
        int expectedSize = bookings.size();
        bookingRepository.saveAll(bookings);

        List<Booking> foundBookings = bookingRepository.findAll();

        Assertions.assertEquals(expectedSize, foundBookings.size());
    }

    @Test
    void findAll_shouldNotFind_whenBookingsDoNotExist() {
        List<Booking> bookings = bookingRepository.findAll();

        Assertions.assertTrue(bookings.isEmpty());
    }

    @Test
    void deleteById_shouldDelete_whenBookingExists() {
        Long existingId = 1L;
        String expectedMessage = "Бронирование не найдено";
        Booking booking = new Booking(item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1));
        booking.setId(existingId);
        bookingRepository.save(booking);

        bookingRepository.deleteById(existingId);
        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingRepository.findById(booking.getId())
                        .orElseThrow(() -> new NotFoundException(expectedMessage))
        );

        Assertions.assertEquals(exception.getMessage(), expectedMessage);
    }

    @Test
    @Tag("requiresUser")
    void findAllByUserAll_shouldReturn_whenBookingsExist() {
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1)),
                new Booking(user, item, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3))
        );
        int expectedSize = bookings.size();
        bookingRepository.saveAll(bookings);

        List<Booking> foundBookings = bookingRepository.findAllByUserIdOrderByFromDateDesc(user.getId(), Pageable.unpaged());
        Assertions.assertEquals(expectedSize, foundBookings.size());
    }

    @Test
    @Tag("requiresUser")
    void findAllByUserCurrent_shouldReturn_whenBookingsExist() {
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1))
        );
        int expectedSize = bookings.size();
        bookingRepository.saveAll(bookings);

        List<Booking> foundBookings = bookingRepository.findAllByUserIdAndFromDateBeforeAndToDateAfterOrderByFromDateDesc(
                user.getId(), LocalDateTime.now(), LocalDateTime.now(), Pageable.unpaged());

        Assertions.assertEquals(expectedSize, foundBookings.size());
    }

    @Test
    @Tag("requiresUser")
    void findAllByUserPast_shouldReturn_whenBookingsExist() throws InterruptedException {
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusSeconds(1))
        );
        int expectedSize = bookings.size();
        bookingRepository.saveAll(bookings);

        Thread.sleep(1100);

        List<Booking> foundBookings = bookingRepository.findAllByUserIdAndToDateBeforeOrderByFromDateDesc(
                user.getId(), LocalDateTime.now(), Pageable.unpaged());

        Assertions.assertEquals(expectedSize, foundBookings.size());
    }

    @Test
    @Tag("requiresUser")
    void findAllByUserFuture_shouldReturn_whenBookingsExist() {
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2)),
                new Booking(user, item, LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(4)),
                new Booking(user, item, LocalDateTime.now().plusHours(5), LocalDateTime.now().plusHours(6))
        );
        int expectedSize = bookings.size();
        bookingRepository.saveAll(bookings);

        List<Booking> foundBookings = bookingRepository.findAllByUserIdAndFromDateAfterOrderByFromDateDesc(
                user.getId(), LocalDateTime.now(), Pageable.unpaged());

        Assertions.assertEquals(expectedSize, foundBookings.size());
    }

    @Test
    @Tag("requiresUser")
    void findAllByUserWaiting_shouldReturn_whenBookingsExist() {
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1)),
                new Booking(user, item, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3))
        );
        int expectedSize = bookings.size();
        for (Booking booking : bookings) {
            booking.setStatus(BookingStatus.WAITING);
        }
        bookingRepository.saveAll(bookings);

        List<Booking> foundBookings = bookingRepository.findAllByUserIdAndStatusOrderByFromDateDesc(
                user.getId(), BookingStatus.WAITING, Pageable.unpaged());

        Assertions.assertEquals(expectedSize, foundBookings.size());
    }

    @Test
    @Tag("requiresUser")
    void findAllByUserRejected_shouldReturn_whenBookingsExist() {
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1)),
                new Booking(user, item, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3))
        );
        int expectedSize = bookings.size();
        for (Booking booking : bookings) {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.saveAll(bookings);

        List<Booking> foundBookings = bookingRepository.findAllByUserIdAndStatusOrderByFromDateDesc(
                user.getId(), BookingStatus.REJECTED, Pageable.unpaged());

        Assertions.assertEquals(expectedSize, foundBookings.size());
    }

    @Test
    @Tag("requiresUser")
    @Tag("requiresOwner")
    void findAllByOwnerAll_shouldReturn_whenBookingsExist() {
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1)),
                new Booking(user, item, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3)),
                new Booking(user, item, LocalDateTime.now().plusHours(5), LocalDateTime.now().plusHours(10))
        );
        int expectedSize = bookings.size();
        bookingRepository.saveAll(bookings);

        List<Booking> foundBookings = bookingRepository.findAllByItem_UserIdOrderByFromDateDesc(owner.getId(), Pageable.unpaged());

        Assertions.assertEquals(expectedSize, foundBookings.size());
    }

    @Test
    @Tag("requiresUser")
    @Tag("requiresOwner")
    void findAllByOwnerCurrent_shouldReturn_whenBookingsExist() {
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1))
        );
        int expectedSize = bookings.size();
        bookingRepository.saveAll(bookings);

        List<Booking> foundBookings = bookingRepository.findAllByItem_UserIdAndFromDateBeforeAndToDateAfterOrderByFromDateDesc(
                owner.getId(), LocalDateTime.now(), LocalDateTime.now(), Pageable.unpaged());

        Assertions.assertEquals(expectedSize, foundBookings.size());
    }

    @Test
    @Tag("requiresUser")
    @Tag("requiresOwner")
    void findAllByOwnerPast_shouldReturn_whenBookingsExist() throws InterruptedException {
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusSeconds(1))
        );
        int expectedSize = bookings.size();
        bookingRepository.saveAll(bookings);

        Thread.sleep(1100);

        List<Booking> foundBookings = bookingRepository.findAllByItem_UserIdAndToDateBeforeOrderByFromDateDesc(
                owner.getId(), LocalDateTime.now(), Pageable.unpaged());

        Assertions.assertEquals(expectedSize, foundBookings.size());
    }

    @Test
    @Tag("requiresUser")
    @Tag("requiresOwner")
    void findAllByOwnerFuture_shouldReturn_whenBookingsExist() {
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2)),
                new Booking(user, item, LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(4)),
                new Booking(user, item, LocalDateTime.now().plusHours(5), LocalDateTime.now().plusHours(6))
        );
        int expectedSize = bookings.size();
        bookingRepository.saveAll(bookings);

        List<Booking> foundBookings = bookingRepository.findAllByItem_UserIdAndFromDateAfterOrderByFromDateDesc(
                owner.getId(), LocalDateTime.now(), Pageable.unpaged());

        Assertions.assertEquals(expectedSize, foundBookings.size());
    }

    @Test
    @Tag("requiresUser")
    @Tag("requiresOwner")
    void findAllByOwnerWaiting_shouldReturn_whenBookingsExist() {
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1)),
                new Booking(user, item, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3))
        );
        int expectedSize = bookings.size();
        for (Booking booking : bookings) {
            booking.setStatus(BookingStatus.WAITING);
        }
        bookingRepository.saveAll(bookings);

        List<Booking> foundBookings = bookingRepository.findAllByItem_UserIdAndStatusOrderByFromDateDesc(
                owner.getId(), BookingStatus.WAITING, Pageable.unpaged());

        Assertions.assertEquals(expectedSize, foundBookings.size());
    }

    @Test
    @Tag("requiresUser")
    @Tag("requiresOwner")
    void findAllByOwnerRejected_shouldReturn_whenBookingsExist() {
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1)),
                new Booking(user, item, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3))
        );
        int expectedSize = bookings.size();
        for (Booking booking : bookings) {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.saveAll(bookings);

        List<Booking> foundBookings = bookingRepository.findAllByItem_UserIdAndStatusOrderByFromDateDesc(
                owner.getId(), BookingStatus.REJECTED, Pageable.unpaged());

        Assertions.assertEquals(expectedSize, foundBookings.size());
    }

    @Test
    @Tag("requiresUser")
    void findByUserIdAndItemId_shouldReturn_whenBookingExists() {
        Booking booking = new Booking(user, item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1));
        bookingRepository.save(booking);

        Booking foundBooking = bookingRepository.findByUserIdAndItemId(user.getId(), item.getId()).orElseThrow();

        Assertions.assertEquals(booking.getUser().getId(), foundBooking.getUser().getId());
        Assertions.assertEquals(booking.getItem().getId(), foundBooking.getItem().getId());
    }

    @Test
    void findLastBooking_shouldReturn_whenBookingExists() {
        List<Booking> bookings = List.of(
                new Booking(item,
                        LocalDateTime.of(2000, 1, 1, 0, 0),
                        LocalDateTime.of(2001, 1, 1, 0, 0)),
                new Booking(item,
                        LocalDateTime.of(2010, 1, 1, 0, 0),
                        LocalDateTime.of(2011, 1, 1, 0, 0))
        );
        for (Booking booking : bookings) {
            booking.setStatus(BookingStatus.APPROVED);
        }
        bookingRepository.saveAll(bookings);

        Booking foundBooking = bookingRepository.findLastBooking(item.getId(), BookingStatus.APPROVED, LocalDateTime.now())
                .orElseThrow();

        Assertions.assertEquals(bookings.get(1).getFromDate(), foundBooking.getFromDate());
    }

    @Test
    void findBookingBetweenDates_shouldReturn_whenBookingExists() {
        Booking booking = new Booking(item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1));
        bookingRepository.save(booking);

        Booking foundBooking = bookingRepository.findBookingBetweenDates(item.getId(),
                        LocalDateTime.now(), LocalDateTime.now().plusSeconds(1))
                .orElseThrow();

        Assertions.assertEquals(booking.getItem().getId(), foundBooking.getItem().getId());
        Assertions.assertEquals(booking.getFromDate(), foundBooking.getFromDate());
    }
}

package com.practice.shareitzeinolla.booking;

import com.practice.shareitzeinolla.exception.NotFoundException;
import com.practice.shareitzeinolla.exception.ValidationException;
import com.practice.shareitzeinolla.item.Item;
import com.practice.shareitzeinolla.item.ItemJpaRepository;
import com.practice.shareitzeinolla.user.User;
import com.practice.shareitzeinolla.user.UserJpaRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    BookingJpaService bookingService;

    @Mock
    BookingJpaRepository bookingRepository;

    @Mock
    UserJpaRepository userRepository;

    @Mock
    ItemJpaRepository itemRepository;

    private User user;
    private Item item;
    private User owner;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        bookingService = new BookingJpaService(bookingRepository, userRepository, itemRepository);

        item = new Item("BServiceTestCreate", "BServiceTestCreate", true);
        item.setId(1L);

        if (testInfo.getTags().contains("requiresUser")) {
            user = new User("BServiceTestUser", "bservice@user.com");
            user.setId(1L);
        }

        if (testInfo.getTags().contains("requiresOwner")) {
            owner = new User("BServiceTestOwner", "bservice@owner.com");
            owner.setId(2L);
            item.setUser(owner);
        }
    }

    @Test
    @Tag("requiresUser")
    void createBooking_shouldCreate_whenBookingCorrect() {
        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Booking booking = new Booking(user, item,
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class)))
                .thenReturn(booking);

        Booking createdBooking = bookingService.create(booking, user.getId());

        Assertions.assertNotNull(createdBooking);
        Assertions.assertEquals(booking.getUser().getId(), createdBooking.getUser().getId());
        Assertions.assertEquals(booking.getItem().getId(), createdBooking.getItem().getId());
    }

    @Test
    void createBooking_shouldThrowException_whenUserDoesNotExist() {
        String expectedMessage = "Пользователь не найден";
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.create(new Booking(), 1L)
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @Tag("requiresUser")
    void createBooking_shouldThrowException_whenItemDoesNotExist() {
        String expectedMessage = "Товар не найден";

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Booking booking = new Booking(user, item,
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));

        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.create(booking, 1L)
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @Tag("requiresUser")
    void createBooking_shouldThrowException_whenItemIsNotAvailable() {
        String expectedMessage = "Товар недоступен.";
        item.setAvailable(false);

        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Booking booking = new Booking(user, item,
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.create(booking, 1L)
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @Tag("requiresUser")
    void createBooking_shouldThrowException_whenBookingDatesAreNull() {
        String expectedMessage = "Даты не должны быть пустыми.";

        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Booking booking = new Booking(user, item, null, null);

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.create(booking, 1L)
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @Tag("requiresUser")
    void createBooking_shouldThrowException_whenBookingEndDateIsBeforeStartDate() {
        String expectedMessage = "Дата начала бронирования должна быть раньше даты окончания бронирования";

        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Booking booking = new Booking(user, item,
                LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(1));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.create(booking, 1L)
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @Tag("requiresUser")
    void createBooking_shouldThrowException_whenBookingStartDateEqualsEndDate() {
        String expectedMessage = "Дата начала бронирования должна быть раньше даты окончания бронирования";

        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Booking booking = new Booking(user, item,
                LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(2));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.create(booking, 1L)
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @Tag("requiresUser")
    void createBooking_shouldThrowException_whenBookingAlreadyBusy() {
        String expectedMessage = "Данный товар уже забронирован на эти даты.";

        Mockito.when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(item));

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Booking booking = new Booking(user, item,
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        Mockito.when(bookingRepository.findBookingBetweenDates(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.create(booking, 1L)
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @Tag("requiresUser")
    void updateBookingApproved_shouldUpdateBooking_whenBookingCorrect() {
        item.setUser(user);
        Booking booking = new Booking(user, item,
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        booking.setId(1L);
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Booking result = bookingService.update(booking.getId(), user.getId(), true);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(booking.getUser().getId(), result.getUser().getId());
        Assertions.assertEquals(booking.getItem().getId(), result.getItem().getId());
        Assertions.assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    @Tag("requiresUser")
    void updateBookingRejected_shouldUpdateBooking_whenBookingCorrect() {
        item.setUser(user);
        Booking booking = new Booking(user, item,
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        booking.setId(1L);
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Booking result = bookingService.update(booking.getId(), user.getId(), false);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(booking.getUser().getId(), result.getUser().getId());
        Assertions.assertEquals(booking.getItem().getId(), result.getItem().getId());
        Assertions.assertEquals(BookingStatus.REJECTED, result.getStatus());
    }

    @Test
    void updateBooking_shouldNotUpdate_whenBookingDoesNotExist() {
        String expectedMessage = "Бронирование не найдено.";
        long notExistingId = 100L;

        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.update(notExistingId, notExistingId, true)
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @Tag("requiresUser")
    void updateBooking_shouldNotUpdate_whenOwnerIsDifferent() {
        String expectedMessage = "Менять статус брони может только владелец товара.";
        long notExistingId = 100L;

        item.setUser(user);
        Booking booking = new Booking(item,
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        booking.setId(1L);
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.update(booking.getId(), notExistingId, true)
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @Tag("requiresUser")
    void updateBooking_shouldNotUpdate_whenUserDoesNotExist() {
        String expectedMessage = "Пользователь не найден";
        item.setUser(user);
        Booking booking = new Booking(item,
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        booking.setId(1L);
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.update(booking.getId(), user.getId(), true)
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @Tag("requiresUser")
    void findById_shouldFind_whenBookingExists() {
        item.setUser(user);
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        Booking booking = new Booking(user, item,
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        booking.setId(1L);
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        Booking result = bookingService.findById(booking.getId(), user.getId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(booking.getUser().getId(), result.getUser().getId());
        Assertions.assertEquals(booking.getItem().getId(), result.getItem().getId());
    }

    @Test
    void findById_shouldNotFind_whenUserDoesNotExist() {
        String expectedMessage = "Пользователь не найден";
        long notExistingId = 100L;

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.findById(notExistingId, notExistingId)
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @Tag("requiresUser")
    void findById_shouldNotFind_whenBookingDoesNotExist() {
        String expectedMessage = "Бронирование не найдено.";
        long notExistingId = 100L;

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.findById(notExistingId, user.getId())
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @Tag("requiresUser")
    void findById_shouldNotFind_whenUserDoesNotMatch() {
        String expectedMessage = "У данного пользователя нет доступа к этому бронированию.";
        long notExistingId = 100L;

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        User secondUser = new User("BServiceTestCreate2", "bservice2@create.com");
        secondUser.setId(1L);

        Booking booking = new Booking(secondUser, item,
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        booking.setId(1L);
        Mockito.when(bookingRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(booking));

        item.setUser(secondUser);

        final ValidationException exception = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.findById(booking.getId(), notExistingId)
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @Tag("requiresUser")
    void findAllByUserAll_shouldFind_whenBookingsExist() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));

        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1)),
                new Booking(user, item, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3))
        );
        Mockito.when(bookingRepository.findAllByUserIdOrderByFromDateDesc(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(bookings);

        List<Booking> result = bookingService.findAllByUser(user.getId(), "ALL", 0, 10);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookings.size(), result.size());
    }

    @Test
    @Tag("requiresUser")
    void findAllByUserCurrent_shouldFind_whenBookingsExist() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1))
        );

        Mockito.when(bookingRepository.findAllByUserIdAndFromDateBeforeAndToDateAfterOrderByFromDateDesc(
                        Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(bookings);

        List<Booking> result = bookingService.findAllByUser(user.getId(), "CURRENT", 0, 10);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookings.size(), result.size());
    }

    @Test
    @Tag("requiresUser")
    void findAllByUserPast_shouldFind_whenBookingsExist() throws InterruptedException {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusSeconds(1))
        );
        Thread.sleep(1100);

        Mockito.when(bookingRepository.findAllByUserIdAndToDateBeforeOrderByFromDateDesc(
                        Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(bookings);

        List<Booking> result = bookingService.findAllByUser(user.getId(), "PAST", 0, 10);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookings.size(), result.size());
    }

    @Test
    @Tag("requiresUser")
    void findAllByUserFuture_shouldFind_whenBookingsExist() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2)),
                new Booking(user, item, LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(4)),
                new Booking(user, item, LocalDateTime.now().plusHours(5), LocalDateTime.now().plusHours(6))
        );

        Mockito.when(bookingRepository.findAllByUserIdAndFromDateAfterOrderByFromDateDesc(
                        Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(bookings);

        List<Booking> result = bookingService.findAllByUser(user.getId(), "FUTURE", 0, 10);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookings.size(), result.size());
    }

    @Test
    @Tag("requiresUser")
    void findAllByUserWaiting_shouldFind_whenBookingsExist() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1)),
                new Booking(user, item, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3))
        );
        for (Booking booking : bookings) {
            booking.setStatus(BookingStatus.WAITING);
        }
        Mockito.when(bookingRepository.findAllByUserIdAndStatusOrderByFromDateDesc(
                        Mockito.anyLong(), Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(bookings);

        List<Booking> result = bookingService.findAllByUser(user.getId(), "WAITING", 0, 10);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookings.size(), result.size());
    }

    @Test
    @Tag("requiresUser")
    void findAllByUserRejected_shouldFind_whenBookingsExist() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1)),
                new Booking(user, item, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3))
        );
        for (Booking booking : bookings) {
            booking.setStatus(BookingStatus.REJECTED);
        }

        Mockito.when(bookingRepository.findAllByUserIdAndStatusOrderByFromDateDesc(
                        Mockito.anyLong(), Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(bookings);

        List<Booking> result = bookingService.findAllByUser(user.getId(), "REJECTED", 0, 10);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookings.size(), result.size());
    }

    @Test
    void findAllByUser_shouldNotFind_whenUserDoesNotExist() {
        long notExistingUserId = 100L;
        String expectedMessage = "Пользователь не найден";

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.findAllByUser(notExistingUserId, "ALL", 0, 10)
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @Tag("requiresUser")
    @Tag("requiresOwner")
    void findAllByOwnerAll_shouldFind_whenBookingsExist() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1)),
                new Booking(user, item, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3)),
                new Booking(user, item, LocalDateTime.now().plusHours(5), LocalDateTime.now().plusHours(10))
        );

        Mockito.when(bookingRepository.findAllByItem_UserIdOrderByFromDateDesc(
                        Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(bookings);

        List<Booking> result = bookingService.findAllByOwner(owner.getId(), "ALL", 0, 10);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookings.size(), result.size());
        Assertions.assertEquals(owner.getId(), result.get(0).getItem().getUser().getId());
        Assertions.assertEquals(user.getId(), result.get(0).getUser().getId());
    }

    @Test
    @Tag("requiresUser")
    @Tag("requiresOwner")
    void findAllByOwnerCurrent_shouldFind_whenBookingsExist() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1))
        );

        Mockito.when(bookingRepository.findAllByItem_UserIdAndFromDateBeforeAndToDateAfterOrderByFromDateDesc(
                        Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                        Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(bookings);

        List<Booking> result = bookingService.findAllByOwner(owner.getId(), "CURRENT", 0, 10);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookings.size(), result.size());
        Assertions.assertEquals(owner.getId(), result.get(0).getItem().getUser().getId());
        Assertions.assertEquals(user.getId(), result.get(0).getUser().getId());
    }

    @Test
    @Tag("requiresUser")
    @Tag("requiresOwner")
    void findAllByOwnerPast_shouldFind_whenBookingsExist() throws InterruptedException {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusSeconds(1))
        );
        Thread.sleep(1100);

        Mockito.when(bookingRepository.findAllByItem_UserIdAndToDateBeforeOrderByFromDateDesc(
                        Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(bookings);

        List<Booking> result = bookingService.findAllByOwner(owner.getId(), "PAST", 0, 10);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookings.size(), result.size());
        Assertions.assertEquals(owner.getId(), result.get(0).getItem().getUser().getId());
        Assertions.assertEquals(user.getId(), result.get(0).getUser().getId());
    }

    @Test
    @Tag("requiresUser")
    @Tag("requiresOwner")
    void findAllByOwnerFuture_shouldFind_whenBookingsExist() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2)),
                new Booking(user, item, LocalDateTime.now().plusHours(3), LocalDateTime.now().plusHours(4)),
                new Booking(user, item, LocalDateTime.now().plusHours(5), LocalDateTime.now().plusHours(6))
        );

        Mockito.when(bookingRepository.findAllByItem_UserIdAndFromDateAfterOrderByFromDateDesc(
                        Mockito.anyLong(), Mockito.any(LocalDateTime.class), Mockito.any(Pageable.class)))
                .thenReturn(bookings);

        List<Booking> result = bookingService.findAllByOwner(owner.getId(), "FUTURE", 0, 10);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookings.size(), result.size());
        Assertions.assertEquals(owner.getId(), result.get(0).getItem().getUser().getId());
        Assertions.assertEquals(user.getId(), result.get(0).getUser().getId());
    }

    @Test
    @Tag("requiresUser")
    @Tag("requiresOwner")
    void findAllByOwnerWaiting_shouldFind_whenBookingsExist() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1)),
                new Booking(user, item, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3))
        );
        for (Booking booking : bookings) {
            booking.setStatus(BookingStatus.WAITING);
        }

        Mockito.when(bookingRepository.findAllByItem_UserIdAndStatusOrderByFromDateDesc(
                        Mockito.anyLong(), Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(bookings);

        List<Booking> result = bookingService.findAllByOwner(owner.getId(), "WAITING", 0, 10);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookings.size(), result.size());
        Assertions.assertEquals(owner.getId(), result.get(0).getItem().getUser().getId());
        Assertions.assertEquals(user.getId(), result.get(0).getUser().getId());
    }

    @Test
    @Tag("requiresUser")
    @Tag("requiresOwner")
    void findAllByOwnerRejected_shouldFind_whenBookingsExist() {
        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(user));
        List<Booking> bookings = List.of(
                new Booking(user, item, LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1)),
                new Booking(user, item, LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3))
        );
        for (Booking booking : bookings) {
            booking.setStatus(BookingStatus.REJECTED);
        }

        Mockito.when(bookingRepository.findAllByItem_UserIdAndStatusOrderByFromDateDesc(
                        Mockito.anyLong(), Mockito.any(BookingStatus.class), Mockito.any(Pageable.class)))
                .thenReturn(bookings);

        List<Booking> result = bookingService.findAllByOwner(owner.getId(), "REJECTED", 0, 10);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(bookings.size(), result.size());
        Assertions.assertEquals(owner.getId(), result.get(0).getItem().getUser().getId());
        Assertions.assertEquals(user.getId(), result.get(0).getUser().getId());
    }

    @Test
    void findAllByOwnerRejected_shouldNotFind_whenUserDoesNotExist() {
        long notExistingId = 100L;
        String expectedMessage = "Пользователь не найден";

        Mockito.when(userRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.findAllByOwner(notExistingId, "ALL", 0, 10)
        );

        Assertions.assertEquals(expectedMessage, exception.getMessage());
    }
}
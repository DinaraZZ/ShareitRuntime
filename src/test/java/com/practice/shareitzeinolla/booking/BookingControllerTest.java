package com.practice.shareitzeinolla.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.practice.shareitzeinolla.booking.dto.BookingCreateDto;
import com.practice.shareitzeinolla.item.Item;
import com.practice.shareitzeinolla.user.User;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.practice.shareitzeinolla.util.RequestConstants.USER_HEADER;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private Long userId;
    private Long itemId;
    private Long bookingId;

    private LocalDateTime start;
    private LocalDateTime end;

    private int allBookings;
    private int currentBookings;
    private int pastBookings;
    private int futureBookings;
    private int rejectedBookings;
    private int waitingBookings;

    private int ownerAllBookings;
    private int ownerCurrentBookings;
    private int ownerFutureBookings;
    private int ownerPastBookings;
    private int ownerRejectedBookings;
    private int ownerWaitingBookings;

    @BeforeEach
    void setUp(TestInfo testInfo) throws Exception {
        // create User
        User user = new User("BControllerTestUser", "bcontrolleruser@test.com");
        String jsonUser = objectMapper.writeValueAsString(user);
        ResultActions postUser = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonUser));
        String userIdJson = postUser.andReturn().getResponse().getContentAsString();
        userId = ((Integer) JsonPath.read(userIdJson, "$.id")).longValue();

        // create Item
        Item item = new Item("BControllerTestItem", "BControllerTestItem", true);
        String jsonItem = objectMapper.writeValueAsString(item);
        ResultActions postItem = mockMvc.perform(MockMvcRequestBuilders.post("/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonItem)
                .header(USER_HEADER, userId));
        String itemIdJson = postItem.andReturn().getResponse().getContentAsString();
        itemId = ((Integer) JsonPath.read(itemIdJson, "$.id")).longValue();

        // create Booking
        if (testInfo.getTags().contains("requiresBooking")) {
            start = LocalDateTime.of(2000, 1, 1, 0, 0);
            end = LocalDateTime.of(3000, 1, 1, 0, 0);
            BookingCreateDto booking = new BookingCreateDto(itemId, start, end);
            String bookingJson = objectMapper.writeValueAsString(booking);
            ResultActions postBooking = mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(bookingJson)
                    .header(USER_HEADER, userId));
            String bookingIdJson = postBooking.andReturn().getResponse().getContentAsString();
            bookingId = ((Integer) JsonPath.read(bookingIdJson, "$.id")).longValue();
        }

        // create Bookings list
        if (testInfo.getTags().contains("requiresBookingsList")) {
            // second Owner
            User secondUser = new User("BControllerTestUser2", "bcontrolleruser2@test.com");
            String jsonSecondUser = objectMapper.writeValueAsString(secondUser);
            ResultActions postSecondUser = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonSecondUser));
            String secondUserIdJson = postSecondUser.andReturn().getResponse().getContentAsString();
            Long secondUserId = ((Integer) JsonPath.read(secondUserIdJson, "$.id")).longValue();

            // second Item
            Item secondItem = new Item("BControllerTestItem2", "BControllerTestItem2", true);
            String jsonSecondItem = objectMapper.writeValueAsString(secondItem);
            ResultActions postSecondItem = mockMvc.perform(MockMvcRequestBuilders.post("/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonSecondItem)
                    .header(USER_HEADER, secondUserId));
            String secondItemIdJson = postSecondItem.andReturn().getResponse().getContentAsString();
            Long secondItemId = ((Integer) JsonPath.read(secondItemIdJson, "$.id")).longValue();

            // Bookings list
            List<BookingCreateDto> bookings = List.of(
                    // past = 2
                    new BookingCreateDto(itemId,
                            LocalDateTime.now().minusYears(2),
                            LocalDateTime.now().plusSeconds(1)),
                    new BookingCreateDto(secondItemId,
                            LocalDateTime.now().minusYears(1),
                            LocalDateTime.now().plusSeconds(1)),
                    // future = 3 (+ 1)
                    new BookingCreateDto(itemId,
                            LocalDateTime.now().plusYears(10),
                            LocalDateTime.now().plusYears(11)),
                    new BookingCreateDto(itemId,
                            LocalDateTime.now().plusYears(12),
                            LocalDateTime.now().plusYears(13)),
                    new BookingCreateDto(secondItemId,
                            LocalDateTime.now().plusYears(14),
                            LocalDateTime.now().plusYears(15))
            );
            allBookings += bookings.size();

            for (BookingCreateDto booking : bookings) {
                if (booking.getStart().isAfter(LocalDateTime.now())) {
                    futureBookings++;
                }
                if (booking.getItemId().equals(itemId)) {
                    ownerAllBookings++;
                    if (booking.getStart().isAfter(LocalDateTime.now())) {
                        ownerFutureBookings++;
                    }
                }

                String postJson = objectMapper.writeValueAsString(booking);
                mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postJson)
                        .header(USER_HEADER, userId));
            }

            Thread.sleep(1200);

            // current = 2
            bookings = List.of(
                    new BookingCreateDto(itemId,
                            LocalDateTime.now(),
                            LocalDateTime.now().plusDays(5)),
                    new BookingCreateDto(secondItemId,
                            LocalDateTime.now(),
                            LocalDateTime.now().plusWeeks(2))
            );
            allBookings += bookings.size();

            for (BookingCreateDto booking : bookings) {
                if (booking.getEnd().isAfter(LocalDateTime.now())) {
                    currentBookings++;
                }
                if (booking.getItemId().equals(itemId)) {
                    ownerAllBookings++;
                    if (booking.getEnd().isAfter(LocalDateTime.now())) {
                        ownerCurrentBookings++;
                    }
                }

                String postJson = objectMapper.writeValueAsString(booking);
                mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postJson)
                        .header(USER_HEADER, userId));
            }

            // rejected = 1
            bookings = List.of(
                    // future = (3) + 1
                    new BookingCreateDto(secondItemId,
                            LocalDateTime.now().plusYears(16),
                            LocalDateTime.now().plusYears(17))
            );
//            futureBookings = 4;
            rejectedBookings = bookings.size();
            allBookings += bookings.size();

            for (BookingCreateDto booking : bookings) {
                if (booking.getStart().isAfter(LocalDateTime.now())) {
                    futureBookings++;
                }
                if (booking.getItemId().equals(itemId)) {
                    ownerAllBookings++;
                    ownerRejectedBookings++;
                    if (booking.getStart().isAfter(LocalDateTime.now())) {
                        ownerFutureBookings++;
                    }
                }

                String postJson = objectMapper.writeValueAsString(booking);
                ResultActions listPerform = mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postJson)
                        .header(USER_HEADER, userId));
                String bookingIdJson = listPerform.andReturn().getResponse().getContentAsString();
                long listBookingId = ((Integer) JsonPath.read(bookingIdJson, "$.id")).longValue();
                mockMvc.perform(MockMvcRequestBuilders.patch(
                                "/bookings/" + listBookingId + "?approved=" + false)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_HEADER, secondUserId));
            }

            waitingBookings = allBookings - rejectedBookings;
            pastBookings = allBookings - currentBookings - futureBookings;
            ownerWaitingBookings = ownerAllBookings - ownerRejectedBookings;
            ownerPastBookings = ownerAllBookings - ownerCurrentBookings - ownerFutureBookings;
        }
    }

    @Test
    @SneakyThrows
    void bookingCreate_shouldCreate_whenBookingCorrect() {
        LocalDateTime fromDate = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime toDate = LocalDateTime.of(3000, 1, 1, 0, 0);
        BookingCreateDto booking = new BookingCreateDto(itemId, fromDate, toDate);
        String json = objectMapper.writeValueAsString(booking);

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header(USER_HEADER, userId));

        perform.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.start", Matchers.equalTo(fromDate.format(DATE_FORMATTER))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.end", Matchers.equalTo(toDate.format(DATE_FORMATTER))));
    }

    @Test
    @SneakyThrows
    @Tag("requiresBooking")
    void bookingUpdateApproved_shouldUpdate_whenBookingExists() {
        // PATCH booking
        ResultActions patchResult = mockMvc.perform(MockMvcRequestBuilders.patch(
                        "/bookings/" + bookingId + "?approved=" + true)
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_HEADER, userId));
        patchResult.andExpect(MockMvcResultMatchers.status().isOk());

        // GET booking
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/bookings/" + bookingId)
                .header(USER_HEADER, userId));
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.start", Matchers.equalTo(start.format(DATE_FORMATTER))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.end", Matchers.equalTo(end.format(DATE_FORMATTER))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.equalTo(BookingStatus.APPROVED.toString())));
    }

    @Test
    @SneakyThrows
    @Tag("requiresBooking")
    void bookingUpdateRejected_shouldUpdate_whenBookingExists() {
        // PATCH booking
        ResultActions patchResult = mockMvc.perform(MockMvcRequestBuilders.patch(
                        "/bookings/" + bookingId + "?approved=" + false)
                .contentType(MediaType.APPLICATION_JSON)
                .header(USER_HEADER, userId));
        patchResult.andExpect(MockMvcResultMatchers.status().isOk());

        // GET booking
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/bookings/" + bookingId)
                .header(USER_HEADER, userId));
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.start", Matchers.equalTo(start.format(DATE_FORMATTER))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.end", Matchers.equalTo(end.format(DATE_FORMATTER))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.equalTo(BookingStatus.REJECTED.toString())));
    }

    @Test
    @SneakyThrows
    @Tag("requiresBooking")
    void bookingFindById_shouldFind_whenBookingExists() {
        // GET booking
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/bookings/" + bookingId)
                .header(USER_HEADER, userId));
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.start", Matchers.equalTo(start.format(DATE_FORMATTER))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.end", Matchers.equalTo(end.format(DATE_FORMATTER))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.item.id", Matchers.equalTo(itemId.intValue())));
    }

    @Test
    @SneakyThrows
    @Tag("requiresBookingsList")
    void bookingFindAllByUserAll_shouldFind_whenBookingsExist() {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/bookings?state=ALL")
                .header(USER_HEADER, userId));
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(allBookings)));
    }

    @Test
    @SneakyThrows
    @Tag("requiresBookingsList")
    void bookingFindAllByUserCurrent_shouldFind_whenBookingsExist() {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/bookings?state=CURRENT")
                .header(USER_HEADER, userId));
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(currentBookings)));
    }

    @Test
    @SneakyThrows
    @Tag("requiresBookingsList")
    void bookingFindAllByUserPast_shouldFind_whenBookingsExist() {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/bookings?state=PAST")
                .header(USER_HEADER, userId));
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(pastBookings)));
    }

    @Test
    @SneakyThrows
    @Tag("requiresBookingsList")
    void bookingFindAllByUserFuture_shouldFind_whenBookingsExist() {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/bookings?state=FUTURE")
                .header(USER_HEADER, userId));
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(futureBookings)));
    }

    @Test
    @SneakyThrows
    @Tag("requiresBookingsList")
    void bookingFindAllByUserWaiting_shouldFind_whenBookingsExist() {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/bookings?state=WAITING")
                .header(USER_HEADER, userId));
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(waitingBookings)));
    }

    @Test
    @SneakyThrows
    @Tag("requiresBookingsList")
    void bookingFindAllByUserRejected_shouldFind_whenBookingsExist() {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/bookings?state=REJECTED")
                .header(USER_HEADER, userId));
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(rejectedBookings)));
    }

    @Test
    @SneakyThrows
    @Tag("requiresBookingsList")
    void bookingFindAllByOwnerAll_shouldFind_whenBookingsExist() {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner?state=ALL")
                .header(USER_HEADER, userId));
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(ownerAllBookings)));
    }

    @Test
    @SneakyThrows
    @Tag("requiresBookingsList")
    void bookingFindAllByOwnerCurrent_shouldFind_whenBookingsExist() {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner?state=CURRENT")
                .header(USER_HEADER, userId));
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(ownerCurrentBookings)));
    }

    @Test
    @SneakyThrows
    @Tag("requiresBookingsList")
    void bookingFindAllByOwnerPast_shouldFind_whenBookingsExist() {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner?state=PAST")
                .header(USER_HEADER, userId));
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(ownerPastBookings)));
    }

    @Test
    @SneakyThrows
    @Tag("requiresBookingsList")
    void bookingFindAllByOwnerFuture_shouldFind_whenBookingsExist() {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner?state=FUTURE")
                .header(USER_HEADER, userId));
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(ownerFutureBookings)));
    }

    @Test
    @SneakyThrows
    @Tag("requiresBookingsList")
    void bookingFindAllByOwnerWaiting_shouldFind_whenBookingsExist() {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner?state=WAITING")
                .header(USER_HEADER, userId));
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(ownerWaitingBookings)));
    }

    @Test
    @SneakyThrows
    @Tag("requiresBookingsList")
    void bookingFindAllByOwnerRejected_shouldFind_whenBookingsExist() {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner?state=REJECTED")
                .header(USER_HEADER, userId));
        perform.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(ownerRejectedBookings)));
    }
}
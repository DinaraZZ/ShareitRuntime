package com.practice.shareitzeinolla.booking.dto;

import com.practice.shareitzeinolla.booking.BookingStatus;
import com.practice.shareitzeinolla.item.Item;
import com.practice.shareitzeinolla.user.User;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingCreateDto {
    private User user;

//    @NotNull(message = "Товар не может быть пустым.")
    private Item item;
    private BookingStatus status;

//    @NotNull(message = "Дата начала бронирования не может быть пустой")
    @FutureOrPresent(message = "Дата начала бронирования не может быть в прошлом")
    private LocalDateTime fromDate;

//    @NotNull(message = "Дата окончания бронирования не может быть пустой")
    @Future(message = "Дата окончания бронирования может быть только в будущем")
    private LocalDateTime toDate;
}

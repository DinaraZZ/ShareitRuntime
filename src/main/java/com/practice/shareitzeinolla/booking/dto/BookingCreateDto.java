package com.practice.shareitzeinolla.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingCreateDto {

    @NotNull(message = "Товар не может быть пустым.")
    private Long itemId;

//    @NotNull(message = "Дата начала бронирования не может быть пустой")
//    @FutureOrPresent(message = "Дата начала бронирования не может быть в прошлом")
    private LocalDateTime start;

//    @NotNull(message = "Дата окончания бронирования не может быть пустой")
    @Future(message = "Дата окончания бронирования может быть только в будущем")
    private LocalDateTime end;
}

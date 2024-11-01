package com.practice.shareitzeinolla.booking.dto;

import com.practice.shareitzeinolla.booking.BookingStatus;
import com.practice.shareitzeinolla.item.Item;
import com.practice.shareitzeinolla.user.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingResponseDto {
    private Long id;
    private User booker;
    private Item item;
    private BookingStatus status;
    private LocalDateTime start;
    private LocalDateTime end;
}

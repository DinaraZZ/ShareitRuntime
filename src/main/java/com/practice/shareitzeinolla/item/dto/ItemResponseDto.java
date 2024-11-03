package com.practice.shareitzeinolla.item.dto;

import com.practice.shareitzeinolla.booking.Booking;
import com.practice.shareitzeinolla.item.Comment;
import com.practice.shareitzeinolla.user.User;
import lombok.Data;

import java.util.List;

@Data
public class ItemResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User user;
    private List<Comment> comments;
    private Booking lastBooking; // ?
    private Booking nextBooking; // ?
    private Long requestId;
}

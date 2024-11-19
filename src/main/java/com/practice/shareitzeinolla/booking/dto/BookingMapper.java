package com.practice.shareitzeinolla.booking.dto;

import com.practice.shareitzeinolla.booking.Booking;
import com.practice.shareitzeinolla.item.Item;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    public Booking fromBookingCreate(BookingCreateDto bookingCreateDto) {
        Booking booking = new Booking();
        booking.setItem(new Item(bookingCreateDto.getItemId()));
        booking.setFromDate(bookingCreateDto.getStart());
        booking.setToDate(bookingCreateDto.getEnd());
        return booking;
    }

    public BookingResponseDto toResponse(Booking booking) {
        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(booking.getId());
        responseDto.setBooker(booking.getUser());
        responseDto.setItem(booking.getItem());
        responseDto.setStatus(booking.getStatus());
        responseDto.setStart(booking.getFromDate());
        responseDto.setEnd(booking.getToDate());
        return responseDto;
    }
}

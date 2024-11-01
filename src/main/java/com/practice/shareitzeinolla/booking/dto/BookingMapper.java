package com.practice.shareitzeinolla.booking.dto;

import com.practice.shareitzeinolla.booking.Booking;
import com.practice.shareitzeinolla.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    public Booking fromBookingCreate(BookingCreateDto bookingCreateDto) {
        Booking booking = new Booking();
        booking.setUser(bookingCreateDto.getUser());
        booking.setItem(bookingCreateDto.getItem());
        booking.setStatus(bookingCreateDto.getStatus());
        booking.setFromDate(bookingCreateDto.getFromDate());
        booking.setToDate(bookingCreateDto.getToDate());
        return booking;
    }

    public Booking fromBookingUpdate(BookingUpdateDto bookingUpdateDto) {
        Booking booking = new Booking();
        booking.setUser(bookingUpdateDto.getUser());
        booking.setItem(bookingUpdateDto.getItem());
        booking.setStatus(bookingUpdateDto.getStatus());
        booking.setFromDate(bookingUpdateDto.getFromDate());
        booking.setToDate(bookingUpdateDto.getToDate());
        return booking;
    }

    public BookingResponseDto toResponse(Booking booking) {
        BookingResponseDto responseDto = new BookingResponseDto();
        responseDto.setId(booking.getId());
        responseDto.setUser(booking.getUser());
        responseDto.setItem(booking.getItem());
        responseDto.setStatus(booking.getStatus());
        responseDto.setFromDate(booking.getFromDate());
        responseDto.setToDate(booking.getToDate());
        return responseDto;
    }

    public void merge(Booking existingBooking, Booking updatedBooking) {
        if (updatedBooking.getUser() == null && updatedBooking.getItem() == null &&
                updatedBooking.getStatus() == null && updatedBooking.getFromDate() == null &&
                updatedBooking.getToDate() == null) {
            throw new ValidationException("Все поля не могут быть пустыми.");
        }
        if (updatedBooking.getUser() != null) {
            existingBooking.setUser(updatedBooking.getUser());
        }
        if (updatedBooking.getItem() != null) {
            existingBooking.setItem(updatedBooking.getItem());
        }
        if (updatedBooking.getStatus() != null) {
            existingBooking.setStatus(updatedBooking.getStatus());
        }
        if (updatedBooking.getFromDate() != null) {
            existingBooking.setFromDate(updatedBooking.getFromDate());
        }
        if (updatedBooking.getToDate() != null) {
            existingBooking.setToDate(updatedBooking.getToDate());
        }
    }
}

package com.upgrad.Booking.services;

import com.sun.istack.NotNull;
import com.upgrad.Booking.model.entity.BookingInfoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingService {

    BookingInfoEntity acceptBookingDetails(@NotNull BookingInfoEntity booking);

    List<BookingInfoEntity> acceptMultipleBookingDetails(List<BookingInfoEntity> bookings);

    BookingInfoEntity getBookingDetails(@NotNull int id);

    BookingInfoEntity updateBookingDetails(int id, BookingInfoEntity bookings);

    boolean CancelBooking(int id);

    List<BookingInfoEntity> getAllBooking();

    Page<BookingInfoEntity> getPaginatedBookingDetails(Pageable pageable);
}
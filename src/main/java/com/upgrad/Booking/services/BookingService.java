package com.upgrad.Booking.services;

import com.upgrad.Booking.entities.BookingInfoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingService {
    public BookingInfoEntity acceptBookingDetails(BookingInfoEntity booking);

    public List<BookingInfoEntity> acceptMultipleBookingDetails(List<BookingInfoEntity> bookings);

    public BookingInfoEntity getBookingDetails(int id);

    public BookingInfoEntity updateBookingDetails(int id , BookingInfoEntity bookings);

    public boolean CancelBooking(int id);

    public List<BookingInfoEntity> getAllBooking();

    public Page<BookingInfoEntity> getPaginatedBookingDeatails(Pageable pageable);
}

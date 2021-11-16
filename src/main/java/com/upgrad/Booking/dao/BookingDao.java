package com.upgrad.Booking.dao;

import com.upgrad.Booking.model.entity.BookingInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingDao extends JpaRepository<BookingInfoEntity, Integer> {
}

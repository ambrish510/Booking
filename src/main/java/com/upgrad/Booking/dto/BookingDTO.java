package com.upgrad.Booking.dto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookingDTO {

    private int Id ;

    private LocalDate fromDate ;

    private LocalDate toDate ;

    private String aadharNumber;

    private int numOfRooms;

    private String roomNumbers;

    private int roomPrice;

    private int transactionId;

    private LocalDateTime bookedOn;
}
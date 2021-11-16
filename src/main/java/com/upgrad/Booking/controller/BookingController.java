package com.upgrad.Booking.controller;

import com.upgrad.Booking.model.dto.BookingDTO;
import com.upgrad.Booking.model.dto.TransactionDTO;
import com.upgrad.Booking.model.entity.BookingInfoEntity;
import com.upgrad.Booking.services.BookingServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/booking")
public class BookingController {

    private final BookingServiceImpl bookingService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    public BookingController(BookingServiceImpl bookingService) {
        this.bookingService = bookingService;
    }

    /*
    Hotel Booking Creation
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookingInfoEntity> createBooking(@RequestBody BookingDTO bookingDTO) {
        BookingInfoEntity newBooking = modelMapper.map(bookingDTO, BookingInfoEntity.class);
        BookingInfoEntity savedBooking = bookingService.acceptBookingDetails(newBooking);
        System.out.println(savedBooking.toString());
        //BookingDTO SavedBookingDTO = modelMapper.map(savedBooking, BookingDTO.class);
        return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
    }

    /*
    Make Transaction for the booking
     */
    @PostMapping(value = "/{bookingId}/transaction", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookingInfoEntity> makePayment(@PathVariable int bookingId, @RequestBody TransactionDTO transactionDTO) {

        BookingInfoEntity SavedBooking = bookingService.makePayment(transactionDTO);
        //BookingDTO bookingDTO = modelMapper.map(SavedBooking, BookingDTO.class);
        return new ResponseEntity<>(SavedBooking, HttpStatus.CREATED);
    }
}

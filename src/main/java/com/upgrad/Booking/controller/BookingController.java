package com.upgrad.Booking.controller;

import com.upgrad.Booking.dto.BookingDTO;
import com.upgrad.Booking.dto.TransactionDTO;
import com.upgrad.Booking.entities.BookingInfoEntity;
import com.upgrad.Booking.services.BookingServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping(value = "/booking")
public class BookingController {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    ModelMapper modelMapper;

    /*
    Hotel Booking Creation
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingDTO bookingDTO) {
        BookingInfoEntity newBooking = modelMapper.map(bookingDTO, BookingInfoEntity.class);
        BookingInfoEntity savedBooking = bookingService.acceptBookingDetails(newBooking);
        BookingDTO SavedBookingDTO = modelMapper.map(savedBooking, BookingDTO.class);
        return new ResponseEntity<>(SavedBookingDTO, HttpStatus.CREATED);
    }

    /*
    Make Transaction for the booking
     */
    @PostMapping(value = "/{bookingId}/transaction", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> makePayment(@PathVariable int bookingId, @RequestBody TransactionDTO transactionDTO) {

        if (transactionDTO.getPaymentMode().equalsIgnoreCase("CARD") || transactionDTO.getPaymentMode().equalsIgnoreCase("UPI")) {
            BookingInfoEntity SavedBooking = bookingService.makePayment(transactionDTO);
            BookingDTO bookingDTO = modelMapper.map(SavedBooking, BookingDTO.class);
            return new ResponseEntity<>(bookingDTO, HttpStatus.CREATED);

        } else {

            HashMap<Object, Object> invalidPaymentMode = new HashMap<>();
            invalidPaymentMode.put("message", "Invalid mode of payment");
            invalidPaymentMode.put("statusCode", 400);
            return new ResponseEntity<>(invalidPaymentMode, HttpStatus.BAD_REQUEST);
        }
    }
}

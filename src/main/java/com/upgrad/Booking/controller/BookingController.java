package com.upgrad.Booking.controller;

import com.upgrad.Booking.Config.KafkaConfig;
import com.upgrad.Booking.dao.BookingDao;
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
    KafkaConfig kafkaConfig;

    @Autowired
    private BookingServiceImpl bookingService ;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    BookingDao bookingDao;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookingInfoEntity> createBooking(@RequestBody BookingDTO bookingDTO) {
        BookingInfoEntity newBoooking= modelMapper.map(bookingDTO,BookingInfoEntity.class);
        BookingInfoEntity savedBooking = bookingService.acceptBookingDetails(newBoooking);
        BookingDTO SavedBookingDTO = modelMapper.map(savedBooking,BookingDTO.class);
        return new ResponseEntity(SavedBookingDTO, HttpStatus.CREATED);
    }

    @PostMapping(value="/{bookingId}/transaction",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookingInfoEntity> makePayment(@PathVariable int bookingId, @RequestBody TransactionDTO transactionDTO){

        if(transactionDTO.getPaymentMode().equalsIgnoreCase("CARD") || transactionDTO.getPaymentMode().equalsIgnoreCase("UPI")) {
            TransactionDTO newTransaction = modelMapper.map(transactionDTO, TransactionDTO.class);
            BookingInfoEntity SaveBooking = bookingService.makePayment(newTransaction);
            BookingInfoEntity SavedBooking = bookingDao.save(SaveBooking);
            BookingDTO bookingDTO = modelMapper.map(SavedBooking, BookingDTO.class);
            String message = "Booking confirmed for user with aadhaar number: " + SavedBooking.getAadharNumber() +    "    |    "  + "Here are the booking details:    " + SavedBooking.toString();
            System.out.println(message);
            kafkaConfig.publish("chat_message","chat",message);
            System.out.println("message posted");
            return new ResponseEntity(bookingDTO, HttpStatus.CREATED);

        }
        else{
            HashMap<String,String> invalidPaymentMode = new HashMap<>();
            invalidPaymentMode.put("message","Invalid mode of payment");
            invalidPaymentMode.put("stausCode", String.valueOf(HttpStatus.BAD_REQUEST));
            return new ResponseEntity(invalidPaymentMode,HttpStatus.BAD_REQUEST);
        }
    }
}

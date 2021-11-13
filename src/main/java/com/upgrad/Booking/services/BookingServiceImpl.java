package com.upgrad.Booking.services;

import com.sun.istack.NotNull;
import com.upgrad.Booking.Config.KafkaConfig;
import com.upgrad.Booking.dao.BookingDao;
import com.upgrad.Booking.dto.TransactionDTO;
import com.upgrad.Booking.entities.BookingInfoEntity;
import com.upgrad.Booking.exception.RecordNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
public class BookingServiceImpl implements BookingService {

    /*
    Reading the Payment Service End Point URL
     */
    @Value("${paymentServiceEndPointURL}")
    private String paymentServiceEndPointURL;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    BookingDao bookingDao;

    @Autowired
    KafkaConfig kafkaConfig;

    @Override
    public BookingInfoEntity acceptBookingDetails(@NotNull BookingInfoEntity booking) {

        ArrayList<String> roomNumbers = getRandomNumbers(booking.getNumOfRooms());
        StringBuilder roomNumbs = new StringBuilder();

        for (String roomNumber : roomNumbers) {

            roomNumbs.append(",").append(roomNumber);
        }

        booking.setRoomNumbers(roomNumbs.substring(1));
        booking.setRoomPrice(1000 * booking.getNumOfRooms() * DAYS.between(booking.getFromDate(), booking.getToDate()));
        booking.setBookedOn(LocalDateTime.now());
        return bookingDao.save(booking);
    }

    @Override
    public List<BookingInfoEntity> acceptMultipleBookingDetails(List<BookingInfoEntity> bookings) {
        List<BookingInfoEntity> savedBooking = new ArrayList<>();
        for (BookingInfoEntity booking : bookings) {
            savedBooking.add(acceptBookingDetails(booking));
        }
        return savedBooking;
    }

    public static ArrayList<String> getRandomNumbers(int count) {
        Random rand = new Random();
        int upperBound = 100;
        ArrayList<String> numberList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            numberList.add(String.valueOf(rand.nextInt(upperBound)));
        }

        return numberList;
    }

    @Override
    public BookingInfoEntity getBookingDetails(@NotNull int id) {

        return bookingDao.findById(id).orElseThrow(() -> new RecordNotFoundException("Invalid Booking Id"));
    }

    @Override
    public BookingInfoEntity updateBookingDetails(int id, BookingInfoEntity bookings) {
        BookingInfoEntity savedBooking = getBookingDetails(id);
        savedBooking.setAadharNumber(bookings.getAadharNumber());
        savedBooking.setBookedOn(bookings.getBookedOn());
        savedBooking.setFromDate(bookings.getFromDate());
        savedBooking.setNumOfRooms(bookings.getNumOfRooms());
        savedBooking.setRoomPrice(bookings.getRoomPrice());
        savedBooking.setToDate(bookings.getToDate());
        savedBooking.setRoomNumbers(bookings.getRoomNumbers());
        bookingDao.save(savedBooking);
        return savedBooking;
    }

    @Override
    public boolean CancelBooking(int id) {
        BookingInfoEntity savedPayment = getBookingDetails(id);

        if (savedPayment == null) {
            return false;
        }

        bookingDao.delete(savedPayment);
        return true;
    }

    @Override
    public List<BookingInfoEntity> getAllBooking() {
        return bookingDao.findAll();
    }

    @Override
    public Page<BookingInfoEntity> getPaginatedBookingDetails(Pageable pageable) {
        return bookingDao.findAll(pageable);
    }

    public BookingInfoEntity makePayment(TransactionDTO transactionDTO) throws RecordNotFoundException {

        BookingInfoEntity fetchBooking = getBookingDetails(transactionDTO.getBookingId());
        TransactionDTO getTransaction = restTemplate.postForObject(paymentServiceEndPointURL, transactionDTO, TransactionDTO.class);
        if (getTransaction != null)
            fetchBooking.setTransactionId(getTransaction.getTransactionId());
        BookingInfoEntity saveBooking = bookingDao.save(fetchBooking);
        String message = "Booking confirmed for user with aadhaar number: " + saveBooking.getAadharNumber() + "    |    " + "Here are the booking details:    " + saveBooking.toString();
        System.out.println(message);
        kafkaConfig.publish("booking_confirmation_message", "chat", message);
        System.out.println("message posted");
        return saveBooking;
    }
}

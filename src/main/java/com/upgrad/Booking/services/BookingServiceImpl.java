package com.upgrad.Booking.services;

import com.sun.istack.NotNull;
import com.upgrad.Booking.Config.KafkaConfig;
import com.upgrad.Booking.dao.BookingDao;
import com.upgrad.Booking.exception.BookingNotFoundException;
import com.upgrad.Booking.exception.InvalidPaymentModeException;
import com.upgrad.Booking.model.dto.TransactionDTO;
import com.upgrad.Booking.model.entity.BookingInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
public class BookingServiceImpl implements BookingService {

    /*
    Reading the Payment Service End Point URL
     */
    @Value("${url.service.payment}")
    private String paymentServiceEndPointURL;

    /*
    Reading the Topic Name from application.properties
     */
    @Value("${topic.name}")
    private String topicName;

    /*
    Reading the room price per day from application.properties
     */
    @Value("${room.price.per.day}")
    private int roomPricePerDay;

    RestTemplate restTemplate;
    BookingDao bookingDao;
    KafkaConfig kafkaConfig;

    @Autowired
    public BookingServiceImpl(RestTemplate restTemplate, BookingDao bookingDao, KafkaConfig kafkaConfig) {
        this.restTemplate = restTemplate;
        this.bookingDao = bookingDao;
        this.kafkaConfig = kafkaConfig;
    }

    @Override
    public BookingInfoEntity acceptBookingDetails(@NotNull BookingInfoEntity booking) {

        ArrayList<String> roomNumbers = getRandomNumbers(booking.getNumOfRooms());
        StringBuilder roomNumbs = new StringBuilder();

        for (String roomNumber : roomNumbers) {

            roomNumbs.append(",").append(roomNumber);
        }

        booking.setRoomNumbers(roomNumbs.substring(1));
        booking.setRoomPrice(roomPricePerDay * booking.getNumOfRooms() * DAYS.between(booking.getFromDate(), booking.getToDate()));
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

        return bookingDao.findById(id).orElseThrow(() -> new BookingNotFoundException("Invalid Booking Id"));
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

    public BookingInfoEntity makePayment(TransactionDTO transactionDTO) {
        if (!(transactionDTO.getPaymentMode().trim().equalsIgnoreCase("CARD") | transactionDTO.getPaymentMode().trim().equalsIgnoreCase("UPI"))) {
            throw new InvalidPaymentModeException("Invalid Mode of Payment");
        } else {
            Optional<BookingInfoEntity> fetchBooking = bookingDao.findById(transactionDTO.getBookingId());
            if (fetchBooking.isPresent()) {
                BookingInfoEntity bookingInfoEntity = fetchBooking.get();
                TransactionDTO getTransaction = restTemplate.postForObject(paymentServiceEndPointURL, transactionDTO, TransactionDTO.class);
                bookingInfoEntity.setTransactionId(getTransaction.getTransactionId());
                bookingDao.save(bookingInfoEntity);
                System.out.println(bookingDao.toString());
                String message = "Booking confirmed for user with aadhaar number: " + bookingInfoEntity.getAadharNumber() + "    |    " + "Here are the booking details:    " + bookingInfoEntity.toString();
                kafkaConfig.publish(topicName, "chat", message);
                System.out.println("Booking Confirmation Message Posted Successfully to Kafka Topic : " + message);
                return bookingInfoEntity;
            } else {
                throw new BookingNotFoundException("Invalid Booking Id");
            }
        }
    }
}

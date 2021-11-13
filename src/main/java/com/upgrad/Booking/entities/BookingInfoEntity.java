package com.upgrad.Booking.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.*;

@Entity
@Table(name = "booking")
@Getter
@Setter
public class BookingInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int Id;

    @Column
    private LocalDate fromDate;

    @Column
    private LocalDate toDate;

    @Column
    private String aadharNumber;

    @Column
    private int numOfRooms;

    @Column
    private String roomNumbers;

    @Column(nullable = false)
    private long roomPrice;

    @Column
    private int transactionId;

    @Column
    private LocalDateTime bookedOn;

    @Override
    public String toString() {
        return "BookingInfoEntity{" +
                "Id=" + Id +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", aadhaarNumber='" + aadharNumber + '\'' +
                ", numOfRooms=" + numOfRooms +
                ", roomNumbers='" + roomNumbers + '\'' +
                ", roomPrice=" + roomPrice +
                ", transactionId=" + transactionId +
                ", bookedOn=" + bookedOn +
                '}';
    }
}

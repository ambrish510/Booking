package com.upgrad.Booking.entities;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.*;

@Entity
@Table(name="booking")
@Getter
@Setter
public class BookingInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int Id ;

    @Column
    private LocalDate fromDate ;

    @Column(nullable = true)
    private LocalDate toDate ;

    @Column(nullable = true)
    private String aadharNumber;

    @Column(nullable = true)
    private int numOfRooms;

    @Column(nullable = true)
    private String roomNumbers;

    @Column(nullable = false)
    private long roomPrice;

    @Column(nullable = true)
    private int transactionId;

    @Column(nullable = true)
    private LocalDateTime bookedOn;

    @Override
    public String toString() {
        return "BookingInfoEntity{" +
                "Id=" + Id +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", aadharNumber='" + aadharNumber + '\'' +
                ", numOfRooms=" + numOfRooms +
                ", roomNumbers='" + roomNumbers + '\'' +
                ", roomPrice=" + roomPrice +
                ", transactionId=" + transactionId +
                ", bookedOn=" + bookedOn +
                '}';
    }
}

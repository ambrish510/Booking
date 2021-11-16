package com.upgrad.Booking.exception;

public class InvalidPaymentModeException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public InvalidPaymentModeException(String message){
       super(message);
   }
}

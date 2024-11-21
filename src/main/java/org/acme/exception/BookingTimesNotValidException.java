package org.acme.exception;

public class BookingTimesNotValidException extends RuntimeException {

    public BookingTimesNotValidException(String message) {
        super(message);
    }
}

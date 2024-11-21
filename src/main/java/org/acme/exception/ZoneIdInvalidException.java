package org.acme.exception;

public class ZoneIdInvalidException extends RuntimeException {

    public ZoneIdInvalidException(String message) {
        super(message);
    }
}

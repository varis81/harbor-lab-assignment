package org.acme.exception;

public class RoomExistsException extends RuntimeException {

    public RoomExistsException(String message) {
        super(message);
    }
}

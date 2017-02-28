package org.rutebanken.tiamat.rest.exception;

public class ErrorResponseEntity {

    public ErrorResponseEntity(String message) {
        this.message = message;
    }

    public String message;
}

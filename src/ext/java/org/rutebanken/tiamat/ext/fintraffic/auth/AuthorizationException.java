package org.rutebanken.tiamat.ext.fintraffic.auth;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException(String message, Throwable e) {
        super(message, e);
    }
}

package org.rutebanken.tiamat.netex.id;

public class IdGeneratorException extends RuntimeException {

    public IdGeneratorException() {    }

    public IdGeneratorException(String message) {
        super(message);
    }

    public IdGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }
}

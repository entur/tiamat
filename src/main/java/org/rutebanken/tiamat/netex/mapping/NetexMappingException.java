package org.rutebanken.tiamat.netex.mapping;

public class NetexMappingException extends RuntimeException {
    public NetexMappingException(String message, Throwable cause) {
        super(message, cause);
    }
    public NetexMappingException(String message) {
        super(message);
    }
}

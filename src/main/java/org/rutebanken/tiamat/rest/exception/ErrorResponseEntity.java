package org.rutebanken.tiamat.rest.exception;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ErrorResponseEntity {

    public ErrorResponseEntity() {
    }

    public ErrorResponseEntity(String message) {
        this.message = message;
    }

    public String message;

}

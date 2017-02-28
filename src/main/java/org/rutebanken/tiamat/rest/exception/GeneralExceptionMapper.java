package org.rutebanken.tiamat.rest.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.NoSuchElementException;

@Provider
public class GeneralExceptionMapper implements ExceptionMapper<Exception> {
    public Response toResponse(Exception ex) {

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponseEntity(ex.getMessage()))
                .build();
    }
}

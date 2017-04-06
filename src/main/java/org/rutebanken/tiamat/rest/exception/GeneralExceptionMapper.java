package org.rutebanken.tiamat.rest.exception;

import com.google.common.collect.Sets;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OptimisticLockException;
import javax.validation.ValidationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

@Provider
public class GeneralExceptionMapper implements ExceptionMapper<Exception> {

    private Map<Response.Status, Set<Class<?>>> mapping;

    public GeneralExceptionMapper() {
        mapping = new HashMap<>();
        mapping.put(Response.Status.BAD_REQUEST,
                Sets.newHashSet(ValidationException.class, OptimisticLockException.class, EntityNotFoundException.class, DataIntegrityViolationException.class));
        mapping.put(Response.Status.CONFLICT, Sets.newHashSet(EntityExistsException.class));
        mapping.put(Response.Status.FORBIDDEN, Sets.newHashSet(AccessDeniedException.class));
    }


    public Response toResponse(Exception ex) {
        return Response.status(toStatus(ex))
                       .entity(new ErrorResponseEntity(ex.getMessage()))
                       .build();
    }

    protected int toStatus(Throwable e) {
        Throwable rootCause = getRootCause(e);

        for (Map.Entry<Response.Status, Set<Class<?>>> entry : mapping.entrySet()) {
            if (entry.getValue().stream().anyMatch(c -> c.isAssignableFrom(rootCause.getClass()))) {
                return entry.getKey().getStatusCode();
            }
        }
        return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
    }

    private Throwable getRootCause(Throwable e) {
        Throwable rootCause = e;

        if (e instanceof NestedRuntimeException) {
            NestedRuntimeException nestedRuntimeException = ((NestedRuntimeException) e);
            if (nestedRuntimeException.getRootCause() != null) {
                rootCause = nestedRuntimeException.getRootCause();
            }
        }
        return rootCause;
    }
}

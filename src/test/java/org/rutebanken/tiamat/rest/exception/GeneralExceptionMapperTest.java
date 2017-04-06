package org.rutebanken.tiamat.rest.exception;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.TransactionSystemException;

import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;


public class GeneralExceptionMapperTest {


    @Test
    public void rawAccessDeniedExceptionYieldsForbidden() {
        Response rsp = new GeneralExceptionMapper().toResponse(new AccessDeniedException("Nope"));
        Assert.assertEquals(Response.Status.FORBIDDEN.getStatusCode(), rsp.getStatus());
    }

    @Test
    public void nestedAccessDeniedExceptionYieldsForbidden() {
        Response rsp = new GeneralExceptionMapper().toResponse(new TransactionSystemException("", new AccessDeniedException("Nope")));
        Assert.assertEquals(Response.Status.FORBIDDEN.getStatusCode(), rsp.getStatus());
    }


    @Test
    public void nestedValidationExceptionYieldsBadRequest() {
        Response rsp = new GeneralExceptionMapper().toResponse(new TransactionSystemException("", new ValidationException()));
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), rsp.getStatus());
    }

    @Test
    public void nestedUnknownExceptionYieldsInternalServerError() {
        Response rsp = new GeneralExceptionMapper().toResponse(new TransactionSystemException("", new RuntimeException()));
        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), rsp.getStatus());
    }

    @Test
    public void rawUnknownExceptionYieldsInternalServerError() {
        Response rsp = new GeneralExceptionMapper().toResponse(new FileNotFoundException());
        Assert.assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), rsp.getStatus());
    }
}

package org.rutebanken.tiamat.ext.fintraffic.filter;

import jakarta.servlet.Filter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class FintrafficMdcFilterConfigTest {

    @AfterEach
    public void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private Filter filter() {
        return new FintrafficMdcFilterConfig().mdcFilterRegistrationBean().getFilter();
    }

    @Test
    public void testUserIdSetFromJwt() throws Exception {
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").subject("test-subject").build();
        JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter().doFilter(request, response, (req, res) ->
                Assertions.assertEquals("test-subject", MDC.get("userId"))
        );
        Assertions.assertNull(MDC.get("userId"), "userId should be removed from MDC after filter");
    }

    @Test
    public void testUserIdFallsBackToUnknownWhenUnauthenticated() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter().doFilter(request, response, (req, res) ->
                Assertions.assertEquals("<unknown user>", MDC.get("userId"))
        );
        Assertions.assertNull(MDC.get("userId"), "userId should be removed from MDC after filter");
    }

    @Test
    public void testAwsHeadersSetInMdc() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Amz-Cf-Id", "test-cf-id");
        request.addHeader("X-Amzn-Trace-Id", "test-trace-id");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter().doFilter(request, response, (req, res) -> {
            Assertions.assertEquals("test-cf-id", MDC.get("awsCfId"));
            Assertions.assertEquals("test-trace-id", MDC.get("awsAmznTraceId"));
        });
        Assertions.assertNull(MDC.get("awsCfId"), "awsCfId should be removed from MDC after filter");
        Assertions.assertNull(MDC.get("awsAmznTraceId"), "awsAmznTraceId should be removed from MDC after filter");
    }

    @Test
    public void testAwsHeadersAbsentWhenNotProvided() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter().doFilter(request, response, (req, res) -> {
            Assertions.assertNull(MDC.get("awsCfId"), "awsCfId should not be set when header is absent");
            Assertions.assertNull(MDC.get("awsAmznTraceId"), "awsAmznTraceId should not be set when header is absent");
        });
    }

    @Test
    public void testMdcIsCleanedUpEvenWhenFilterChainThrows() {
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").subject("throwing-subject").build();
        JwtAuthenticationToken auth = new JwtAuthenticationToken(jwt);
        SecurityContextHolder.getContext().setAuthentication(auth);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Amz-Cf-Id", "cf-id");
        request.addHeader("X-Amzn-Trace-Id", "trace-id");
        MockHttpServletResponse response = new MockHttpServletResponse();

        Assertions.assertThrows(RuntimeException.class, () ->
                filter().doFilter(request, response, (req, res) -> { throw new RuntimeException("simulated failure"); })
        );
        Assertions.assertNull(MDC.get("userId"));
        Assertions.assertNull(MDC.get("awsCfId"));
        Assertions.assertNull(MDC.get("awsAmznTraceId"));
    }
}

package org.rutebanken.tiamat.ext.fintraffic.auth;

import jakarta.servlet.Filter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rutebanken.helper.organisation.user.UserInfoExtractor;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FintrafficSecurityConfigTest {
    private final UserInfoExtractor userInfoExtractor = mock(UserInfoExtractor.class);

    private void mockAuthenticatedUser() {
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").subject("test-subject").build();
        mockPrincipal(jwt);
    }

    private void mockAuthentication(Authentication authentication) {
        SecurityContext securityContextMock = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContextMock);
        when(securityContextMock.getAuthentication()).thenReturn(authentication);
    }

    private void mockPrincipal(OAuth2Token principal) {
        JwtAuthenticationToken authenticationMock = mock(JwtAuthenticationToken.class);
        mockAuthentication(authenticationMock);
        when(authenticationMock.getPrincipal()).thenReturn(principal);
    }

    @Test
    public void testUserNameFetcherAuthenticated() {
        mockAuthenticatedUser();
        UsernameFetcher usernameFetcher = new FintrafficSecurityConfig().usernameFetcher(userInfoExtractor);
        Assertions.assertEquals("test-subject", usernameFetcher.getUserNameForAuthenticatedUser());
    }

    @Test
    public void testUserNameFetcherUnauthenticated() {
        UsernameFetcher usernameFetcher = new FintrafficSecurityConfig().usernameFetcher(userInfoExtractor);
        Assertions.assertEquals("<unknown user>", usernameFetcher.getUserNameForAuthenticatedUser());

        mockAuthentication(null);
        Assertions.assertEquals("<unknown user>", usernameFetcher.getUserNameForAuthenticatedUser());
    }

    @Test
    public void testAwsTraceIdFilterWithHeader() {
        // 1. Create new HTTP request to simulate incoming request with AWS trace ID header
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("X-Amz-Cf-Id", "test-cf-id");
        request.addHeader("X-Amzn-Trace-Id", "test-trace-id");

        // 2. Create the filter and invoke it
        FintrafficSecurityConfig config = new FintrafficSecurityConfig();
        Filter filter = config.awsTraceIdFilterRegistrationBean().getFilter();
        try {
            filter.doFilter(request, response, (req, res) -> {
                // 3. Inside the filter chain, check that the AWS trace ID is set in the MDC
                String cfIdInMDC = org.slf4j.MDC.get("awsCfId");
                String traceIdInMDC = org.slf4j.MDC.get("awsAmznTraceId");
                Assertions.assertEquals("test-cf-id", cfIdInMDC);
                Assertions.assertEquals("test-trace-id", traceIdInMDC);
            });
        } catch (Exception e) {
            Assertions.fail("Filter threw an exception: " + e.getMessage());
        } finally {
            // 4. After the filter, check that the AWS trace ID is removed from the MDC
            String cfIdInMDC = org.slf4j.MDC.get("awsCfId");
            String traceIdInMDC = org.slf4j.MDC.get("awsAmznTraceId");
            Assertions.assertNull(cfIdInMDC, "awsCfId should be removed from MDC after filter execution");
            Assertions.assertNull(traceIdInMDC, "awsAmznTraceId should be removed from MDC after filter execution");
        }
    }

    @Test
    public void testAwsTraceIdFilterWithoutHeader() {
        // 1. Create new HTTP request without AWS trace ID headers
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // 2. Create the filter and invoke it
        FintrafficSecurityConfig config = new FintrafficSecurityConfig();
        Filter filter = config.awsTraceIdFilterRegistrationBean().getFilter();
        try {
            filter.doFilter(request, response, (req, res) -> {
                // 3. Inside the filter chain, check that the AWS trace ID is not set in the MDC
                String cfIdInMDC = org.slf4j.MDC.get("awsCfId");
                String traceIdInMDC = org.slf4j.MDC.get("awsAmznTraceId");
                Assertions.assertNull(cfIdInMDC, "AWS CF ID should not be set in MDC when header is missing");
                Assertions.assertNull(traceIdInMDC, "AWS trace ID should not be set in MDC when header is missing");
            });
        } catch (Exception e) {
            Assertions.fail("Filter threw an exception: " + e.getMessage());
        }
    }
}

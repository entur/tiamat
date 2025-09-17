package org.rutebanken.tiamat.ext.fintraffic.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rutebanken.helper.organisation.user.UserInfoExtractor;
import org.rutebanken.tiamat.auth.UsernameFetcher;
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
}

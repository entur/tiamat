package org.rutebanken.tiamat.ext.fintraffic.auth;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rutebanken.tiamat.model.VehicleModeEnumeration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TrivoreAuthorizationsTest {

    private TrivoreAuthorizations trivoreAuthorizations;

    @BeforeAll
    static void beforeAll() {
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").subject("test-subject").build();
        SecurityContext securityContextMock = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContextMock);
        JwtAuthenticationToken authenticationMock = mock(JwtAuthenticationToken.class);
        when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        when(authenticationMock.getPrincipal()).thenReturn(jwt);
    }

    @BeforeEach
    void setUp() {
        WebClient webClient = WebClient.builder()
                .exchangeFunction(request ->
                        switch (request.url().getPath()) {
                            case "/api/rest/v1/user/test-subject/groupmembership" -> Mono.just(
                                    ClientResponse.create(HttpStatus.OK)
                                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                            .body("[{\"id\": \"test-id\",\"name\": \"group-name\",\"customFields\":{\"codespaces\": \"ABC,XYZ\"}}]")
                                            .build()
                            );
                            case "/api/rest/v1/user/test-subject/externalpermissions" -> Mono.just(
                                    ClientResponse.create(HttpStatus.OK)
                                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                            .body("[{\"permissionId\": \"external-permission-id\",\"permissionGroupId\": \"permission-group-id\"}]")
                                            .build()
                            );
                            case "/api/rest/v1/externalpermission/group/permission-group-id/permission/external-permission-id" -> Mono.just(
                                    ClientResponse.create(HttpStatus.OK)
                                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                            .body("{\"name\":\"StopPlace:bus:edit\"}")
                                            .build()
                            );
                            default -> throw new UnsupportedOperationException("Unsupported path: " + request.url().getPath());
                        }
                )
                .build();
        trivoreAuthorizations = new TrivoreAuthorizations(webClient, "", "", "", true);
    }

    @Test
    void generatesCascadingApplicablePermissionsBasedOnGivenValues() {
        String entityType = "StopPlace";
        String transportMode = "bus";
        TrivorePermission permission = TrivorePermission.EDIT;
        List<String> permissionsToTest = trivoreAuthorizations.generateCascadingPermissions(entityType, transportMode, permission);
        
        assertThat(permissionsToTest, equalTo(List.of(
                "{entities}:{all}:administer",
                "{entities}:{all}:manage",
                "{entities}:{all}:edit",
                "{entities}:bus:administer",
                "{entities}:bus:manage",
                "{entities}:bus:edit",
                "StopPlace:{all}:administer",
                "StopPlace:{all}:manage",
                "StopPlace:{all}:edit",
                "StopPlace:bus:administer",
                "StopPlace:bus:manage",
                "StopPlace:bus:edit"
        )));
    }

    @Test
    void generatingCascadingPermissionsDoesNotDuplicateCommonSegments() {
        String entityType = "{entities}";
        String transportMode = "{all}";
        TrivorePermission permission = TrivorePermission.ADMINISTER;
        List<String> permissionsToTest = trivoreAuthorizations.generateCascadingPermissions(entityType, transportMode, permission);

        assertThat(permissionsToTest, equalTo(List.of(
                "{entities}:{all}:administer"
        )));
    }

    @Test
    void testHasAccessToCodespace() {
        assertThat(trivoreAuthorizations.hasAccessToCodespace("ABC"), equalTo(true));
        assertThat(trivoreAuthorizations.hasAccessToCodespace("NOT"), equalTo(false));
    }

    @Test
    void testGetAccessibleCodespaces() {
        Set<String> codespaces = trivoreAuthorizations.getAccessibleCodespaces();
        assertThat(codespaces, equalTo(Set.of("ABC", "XYZ")));
    }

    @Test
    void testHasAccess() {
        assertThat(trivoreAuthorizations.hasAccess("StopPlace", "bus", TrivorePermission.EDIT), equalTo(true));
        for (VehicleModeEnumeration vehicleMode : VehicleModeEnumeration.values()) {
            if (vehicleMode != VehicleModeEnumeration.BUS) {
                assertThat(trivoreAuthorizations.hasAccess("StopPlace", vehicleMode.value(), TrivorePermission.EDIT), equalTo(false));
            }
        }

    }
}
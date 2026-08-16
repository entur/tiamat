package org.rutebanken.tiamat.rest.write.controllers;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.auth.MethodSecurityTestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The write API's coarse access control is a single @PreAuthorize on the Jersey resources, and
 * TiamatSecurityConfig, the only @EnableMethodSecurity, is @Profile("!test"). Method security is
 * therefore off in every other test, so nothing verified that the annotation is honoured on a
 * JAX-RS resource at all. A @PreAuthorize that silently fails to apply to a Jersey resource is a
 * well known failure mode, and here it would leave the write API ungated.
 * <p>
 * Method security is enabled for this context only, through a profile gated configuration.
 * Requests carry no authentication, so canUseWriteApi denies and each endpoint must answer 403
 * rather than accept the request.
 */
@AutoConfigureTestRestTemplate
@ActiveProfiles(MethodSecurityTestConfig.PROFILE)
public class WriteApiPreAuthorizeTest extends TiamatIntegrationTest {

    private static final String WRITE_ENDPOINT = "/services/stop_places/write";

    private static final String STOP_PLACE_XML = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace version="1">
                    <Name>Gated Stop</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                </StopPlace>
            </stopPlaces>
            """;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void createIsForbiddenWithoutWriteApiRole() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                WRITE_ENDPOINT, xmlRequest(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void updateIsForbiddenWithoutWriteApiRole() {
        ResponseEntity<String> response = restTemplate.exchange(
                WRITE_ENDPOINT, HttpMethod.PUT, xmlRequest(), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void deleteIsForbiddenWithoutWriteApiRole() {
        ResponseEntity<String> response = restTemplate.exchange(
                WRITE_ENDPOINT + "/NSR:StopPlace:1", HttpMethod.DELETE, null, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void readIsForbiddenWithoutWriteApiRole() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                WRITE_ENDPOINT + "/NSR:StopPlace:1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void jobStatusIsForbiddenWithoutWriteApiRole() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                WRITE_ENDPOINT + "/jobs/1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    private static HttpEntity<String> xmlRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        return new HttpEntity<>(STOP_PLACE_XML, headers);
    }
}

package org.rutebanken.tiamat.rest.write;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.rest.write.dto.StopPlaceJobDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@TestPropertySource(properties = {
    "tiamat.write-api.queue-capacity=0",
    "authorization.enabled=false",
    "tiamat.hazelcast.port-auto-increment=true"
})
@DirtiesContext
public class BackPressureIntegrationTest extends TiamatIntegrationTest {

    private static final String WRITE_ENDPOINT = "/services/stop_places/write";

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoSpyBean
    private StopPlaceWriteDomainService stopPlaceWriteDomainService;

    @Test
    public void whenQueueIsFullSubsequentRequestsReturn503() throws Exception {
        setUpSecurityContext();

        // This latch blocks the worker thread inside createStopPlace so it stays busy.
        CountDownLatch workerBlocked = new CountDownLatch(1);
        // This latch lets us know the worker has actually started (thread is occupied).
        CountDownLatch workerStarted = new CountDownLatch(1);

        doAnswer(invocation -> {
            workerStarted.countDown(); // signal: worker thread is now occupied
            workerBlocked.await(10, TimeUnit.SECONDS); // hold the thread until released
            var stopPlace = new StopPlace();
            stopPlace.setNetexId("SAM:StopPlace:1");
            return stopPlace;
        })
            .when(stopPlaceWriteDomainService)
            .createStopPlace(any());

        String xml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace id="SAM:StopPlace:1">
                    <Name>Blocker Stop</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                </StopPlace>
            </stopPlaces>
            """;

        // First request: occupies the single worker thread (async, so returns immediately).
        Thread firstRequest = new Thread(() ->
            postXml(xml, StopPlaceJobDto.class)
        );
        firstRequest.start();

        // Wait until the worker thread is actually blocked before sending the next request.
        boolean started = workerStarted.await(5, TimeUnit.SECONDS);
        assertThat(started).as("Worker thread did not start in time").isTrue();

        // Second request: queue is full (capacity=0) and the worker is busy → should get 503.
        ResponseEntity<String> response = postXml(xml, String.class);

        assertThat(response.getStatusCode())
            .as("Expected 503 Service Unavailable when the write queue is full")
            .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);

        // Release the blocked worker so the test can finish cleanly.
        workerBlocked.countDown();
        firstRequest.join(5_000);
    }

    private <T> ResponseEntity<T> postXml(String xml, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> request = new HttpEntity<>(xml, headers);
        return restTemplate.postForEntity(
            WRITE_ENDPOINT,
            request,
            responseType
        );
    }
}

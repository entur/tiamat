package org.rutebanken.tiamat.rest.write;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class BackPressureIntegrationTest extends TiamatIntegrationTest {

    private static final String WRITE_ENDPOINT = "/services/stop_places/write";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    @Qualifier("stopPlaceWriteExecutor")
    private Executor stopPlaceWriteExecutor;

    @Test
    public void whenQueueIsFullSubsequentRequestsReturn503() throws Exception {
        setUpSecurityContext();

        // Latch to keep the worker thread occupied.
        CountDownLatch workerBlocked = new CountDownLatch(1);
        // Latch to confirm the worker thread has started.
        CountDownLatch workerStarted = new CountDownLatch(1);

        // Submit a blocking task directly to the executor to occupy the single worker thread.
        // This avoids mocking the domain service and keeps the application context shared.
        stopPlaceWriteExecutor.execute(() -> {
            workerStarted.countDown();
            try {
                workerBlocked.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Wait until the worker thread is actually blocked.
        boolean started = workerStarted.await(5, TimeUnit.SECONDS);
        assertThat(started).as("Worker thread did not start in time").isTrue();

        String xml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace id="SAM:StopPlace:1">
                    <Name>Blocker Stop</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                </StopPlace>
            </stopPlaces>
            """;

        // The worker is busy and the queue capacity is 0, so this request should get 503.
        ResponseEntity<String> response = postXml(xml, String.class);

        assertThat(response.getStatusCode())
            .as("Expected 503 Service Unavailable when the write queue is full")
            .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);

        // Release the blocked worker so the thread pool recovers cleanly.
        workerBlocked.countDown();
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

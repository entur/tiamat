package org.rutebanken.tiamat.rest.write.controllers;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.rest.write.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StopPlaceControllerTest extends TiamatIntegrationTest {

    private JobService jobService = mock(JobService.class);

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCreateStopPlace() {
        String xml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace id="NSR:StopPlace:1">
                    <Name>Main Station</Name>
                </StopPlace>
            </stopPlaces>
            """;
        var job = new AsyncStopPlaceJob();
        job.setStatus(AsyncStopPlaceJobStatus.PROCESSING);
        when(jobService.createJob()).thenReturn(job);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> request = new HttpEntity<>(xml, headers);

        ResponseEntity<AsyncStopPlaceJob> response = restTemplate.postForEntity(
            "/services/stop_places/write",
            request,
            AsyncStopPlaceJob.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    }

    @Test
    public void testCreateStopPlace_FailureOnMalformedBody() {
        String xml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StoopPlace id="NSR:StopPlace:1">
                    <Name>Main Station</Name>
                </StopPlace>
                <StopPlace id="NSR:StopPlace:2">
                    <Name>Other Station</Name>
                </StoopPlace>
            </stopPlaces>
            """;
        var job = new AsyncStopPlaceJob();
        job.setStatus(AsyncStopPlaceJobStatus.PROCESSING);
        when(jobService.createJob()).thenReturn(job);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> request = new HttpEntity<>(xml, headers);

        ResponseEntity<AsyncStopPlaceJob> response = restTemplate.postForEntity(
            "/services/stop_places/write",
            request,
            AsyncStopPlaceJob.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}

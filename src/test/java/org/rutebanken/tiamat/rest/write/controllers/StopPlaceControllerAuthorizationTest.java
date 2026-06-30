package org.rutebanken.tiamat.rest.write.controllers;

import org.junit.After;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.auth.MockedRoleAssignmentExtractor;
import org.rutebanken.tiamat.auth.RoleAssignmentListBuilder;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.rest.write.dto.StopPlaceJobDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class StopPlaceControllerAuthorizationTest extends TiamatIntegrationTest {

    private static final String WRITE_ENDPOINT = "/services/stop_places/write";
    private static final String ACCESS_DENIED_MESSAGE = "You do not have permission to perform this operation.";

    @Autowired
    private MockedRoleAssignmentExtractor mockedRoleAssignmentExtractor;

    @Autowired
    private TestRestTemplate restTemplate;

    @After
    public void resetRoleAssignments() {
        mockedRoleAssignmentExtractor.reset();
    }

    @Test
    public void createStopPlaceWithNoRolesResultsInFailedJob() throws InterruptedException {
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(Collections.emptyList());
        mockedRoleAssignmentExtractor.setPersistent(true);

        String xml = """
                <stopPlaces xmlns="http://www.netex.org.uk/netex">
                    <StopPlace version="1">
                        <Name>Bus Stop</Name>
                        <StopPlaceType>busStation</StopPlaceType>
                    </StopPlace>
                </stopPlaces>
                """;

        ResponseEntity<StopPlaceJobDto> response = postXml(xml, StopPlaceJobDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        StopPlaceJobDto finalJob = awaitJobCompletion(response.getBody().jobId());
        assertThat(finalJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(finalJob.errorMessage()).isEqualTo(ACCESS_DENIED_MESSAGE);
    }

    @Test
    public void updateStopPlaceWithNoRolesResultsInFailedJob() throws InterruptedException {
        StopPlace existing = stopPlaceRepository.save(busStation("Existing Stop"));
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(Collections.emptyList());
        mockedRoleAssignmentExtractor.setPersistent(true);

        String xml = String.format("""
                <stopPlaces xmlns="http://www.netex.org.uk/netex">
                    <StopPlace id="%s">
                        <Name>Updated Stop</Name>
                        <StopPlaceType>busStation</StopPlaceType>
                    </StopPlace>
                </stopPlaces>
                """, existing.getNetexId());

        ResponseEntity<StopPlaceJobDto> response = putXml(xml, StopPlaceJobDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        StopPlaceJobDto finalJob = awaitJobCompletion(response.getBody().jobId());
        assertThat(finalJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(finalJob.errorMessage()).isEqualTo(ACCESS_DENIED_MESSAGE);
    }

    @Test
    public void deleteStopPlaceWithNoRolesResultsInFailedJob() throws InterruptedException {
        StopPlace existing = stopPlaceRepository.save(busStation("To Be Deleted"));
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(Collections.emptyList());
        mockedRoleAssignmentExtractor.setPersistent(true);

        ResponseEntity<StopPlaceJobDto> response = restTemplate.exchange(
                WRITE_ENDPOINT + "/" + existing.getNetexId(),
                HttpMethod.DELETE,
                null,
                StopPlaceJobDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        StopPlaceJobDto finalJob = awaitJobCompletion(response.getBody().jobId());
        assertThat(finalJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(finalJob.errorMessage()).isEqualTo(ACCESS_DENIED_MESSAGE);
    }

    @Test
    public void createStopPlaceWithWrongTypeResultsInFailedJob() throws InterruptedException {
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(
                RoleAssignmentListBuilder.builder().withStopPlaceOfType(StopTypeEnumeration.RAIL_STATION).build()
        );
        mockedRoleAssignmentExtractor.setPersistent(true);

        String xml = """
                <stopPlaces xmlns="http://www.netex.org.uk/netex">
                    <StopPlace version="1">
                        <Name>Bus Stop</Name>
                        <StopPlaceType>busStation</StopPlaceType>
                    </StopPlace>
                </stopPlaces>
                """;

        ResponseEntity<StopPlaceJobDto> response = postXml(xml, StopPlaceJobDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        StopPlaceJobDto finalJob = awaitJobCompletion(response.getBody().jobId());
        assertThat(finalJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(finalJob.errorMessage()).isEqualTo(ACCESS_DENIED_MESSAGE);
    }

    @Test
    public void updateStopPlaceWithWrongTypeResultsInFailedJob() throws InterruptedException {
        StopPlace existing = stopPlaceRepository.save(busStation("Existing Bus Stop"));
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(
                RoleAssignmentListBuilder.builder().withStopPlaceOfType(StopTypeEnumeration.RAIL_STATION).build()
        );
        mockedRoleAssignmentExtractor.setPersistent(true);

        String xml = String.format("""
                <stopPlaces xmlns="http://www.netex.org.uk/netex">
                    <StopPlace id="%s">
                        <Name>Updated Bus Stop</Name>
                        <StopPlaceType>busStation</StopPlaceType>
                    </StopPlace>
                </stopPlaces>
                """, existing.getNetexId());

        ResponseEntity<StopPlaceJobDto> response = putXml(xml, StopPlaceJobDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        StopPlaceJobDto finalJob = awaitJobCompletion(response.getBody().jobId());
        assertThat(finalJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(finalJob.errorMessage()).isEqualTo(ACCESS_DENIED_MESSAGE);
    }

    @Test
    public void deleteStopPlaceWithWrongTypeResultsInFailedJob() throws InterruptedException {
        StopPlace existing = stopPlaceRepository.save(busStation("To Be Deleted"));
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(
                RoleAssignmentListBuilder.builder().withStopPlaceOfType(StopTypeEnumeration.RAIL_STATION).build()
        );
        mockedRoleAssignmentExtractor.setPersistent(true);

        ResponseEntity<StopPlaceJobDto> response = restTemplate.exchange(
                WRITE_ENDPOINT + "/" + existing.getNetexId(),
                HttpMethod.DELETE,
                null,
                StopPlaceJobDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        StopPlaceJobDto finalJob = awaitJobCompletion(response.getBody().jobId());
        assertThat(finalJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(finalJob.errorMessage()).isEqualTo(ACCESS_DENIED_MESSAGE);
    }

    private StopPlace busStation(String name) {
        StopPlace sp = new StopPlace(new EmbeddableMultilingualString(name));
        sp.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        sp.setValidBetween(new ValidBetween(Instant.now()));
        return sp;
    }

    private StopPlaceJobDto awaitJobCompletion(Long jobId) throws InterruptedException {
        String jobUrl = WRITE_ENDPOINT + "/jobs/" + jobId;
        for (int i = 0; i < 20; i++) {
            ResponseEntity<StopPlaceJobDto> jobResponse =
                    restTemplate.getForEntity(jobUrl, StopPlaceJobDto.class);
            StopPlaceJobDto job = jobResponse.getBody();
            if (job != null && job.status() != AsyncStopPlaceJobStatus.PROCESSING) {
                return job;
            }
            Thread.sleep(200);
        }
        throw new AssertionError("Job did not complete within the expected time");
    }

    private <T> ResponseEntity<T> postXml(String xml, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> request = new HttpEntity<>(xml, headers);
        return restTemplate.postForEntity(WRITE_ENDPOINT, request, responseType);
    }

    private <T> ResponseEntity<T> putXml(String xml, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> request = new HttpEntity<>(xml, headers);
        return restTemplate.exchange(WRITE_ENDPOINT, HttpMethod.PUT, request, responseType);
    }
}

package org.rutebanken.tiamat.rest.write.controllers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rutebanken.helper.organisation.AuthorizationConstants;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.auth.MockedRoleAssignmentExtractor;
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

import static org.assertj.core.api.Assertions.assertThat;

public class StopPlaceControllerIntegrationTest extends TiamatIntegrationTest {

    @Autowired
    private MockedRoleAssignmentExtractor mockedRoleAssignmentExtractor;

    @Before
    public void setUpPersistentRoleAssignment() {
        RoleAssignment role = RoleAssignment.builder()
            .withRole("editStops")
            .withOrganisation("*")
            .withEntityClassification(AuthorizationConstants.ENTITY_TYPE, AuthorizationConstants.ENTITY_CLASSIFIER_ALL_TYPES)
            .build();
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(role);
        mockedRoleAssignmentExtractor.setPersistent(true);
    }

    @After
    public void tearDownPersistentRoleAssignment() {
        mockedRoleAssignmentExtractor.reset();
    }

    private static final String WRITE_ENDPOINT = "/services/stop_places/write";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void createStopPlaceReturnsAcceptedWithProcessingJob() throws InterruptedException {
        String xml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace version="1">
                    <Name>Test Station</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                </StopPlace>
            </stopPlaces>
            """;

        ResponseEntity<StopPlaceJobDto> response = postXml(
            xml,
            StopPlaceJobDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().jobId()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(
            AsyncStopPlaceJobStatus.PROCESSING
        );

        Long jobId = response.getBody().jobId();
        StopPlaceJobDto finalJob = awaitJobCompletion(jobId);

        assertThat(finalJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FINISHED);
    }

    @Test
    public void createStopPlaceWithMultipleStopPlacesReturnsFailedJob()
        throws InterruptedException {
        String xml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace version="1">
                    <Name>Station A</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                </StopPlace>
                <StopPlace version="1">
                    <Name>Station B</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                </StopPlace>
            </stopPlaces>
            """;

        ResponseEntity<StopPlaceJobDto> response = postXml(
            xml,
            StopPlaceJobDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().jobId()).isNotNull();

        Long jobId = response.getBody().jobId();
        StopPlaceJobDto finalJob = awaitJobCompletion(jobId);

        assertThat(finalJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(finalJob.errorMessage()).contains(
            "Exactly one stop place must be provided"
        );
    }

    @Test
    public void createStopPlaceWithMalformedXmlReturnsBadRequest() {
        String xml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace version="1">
                    <Name>Broken
            </stopPlaces>
            """;

        ResponseEntity<String> response = postXml(xml, String.class);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    public void deleteStopPlaceReturnsAcceptedWithProcessingJob() throws InterruptedException {
        StopPlace stopPlace = new StopPlace(
            new EmbeddableMultilingualString("To Be Deleted")
        );
        stopPlace.setValidBetween(
                new ValidBetween(Instant.now())
        );
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        StopPlace saved = stopPlaceRepository.save(stopPlace);

        ResponseEntity<StopPlaceJobDto> response = restTemplate.exchange(
            WRITE_ENDPOINT + "/" + saved.getNetexId(),
            HttpMethod.DELETE,
            null,
            StopPlaceJobDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().jobId()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(
            AsyncStopPlaceJobStatus.PROCESSING
        );

        Long jobId = response.getBody().jobId();
        StopPlaceJobDto finalJob = awaitJobCompletion(jobId);

        assertThat(finalJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FINISHED);
    }

    @Test
    public void updateStopPlaceReturnsAcceptedWithProcessingJob() throws InterruptedException {
        StopPlace stopPlace = new StopPlace(
            new EmbeddableMultilingualString("Original Name")
        );
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        StopPlace saved = stopPlaceRepository.save(stopPlace);

        String xml = String.format(
            """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace id="%s">
                    <Name>Updated Name</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                </StopPlace>
            </stopPlaces>
            """,
            saved.getNetexId()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> request = new HttpEntity<>(xml, headers);

        ResponseEntity<StopPlaceJobDto> response = restTemplate.exchange(
            WRITE_ENDPOINT,
            HttpMethod.PUT,
            request,
            StopPlaceJobDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().jobId()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(
            AsyncStopPlaceJobStatus.PROCESSING
        );

        Long jobId = response.getBody().jobId();
        StopPlaceJobDto finalJob = awaitJobCompletion(jobId);

        assertThat(finalJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FINISHED);
    }

    @Test
    public void getStopPlaceReturnsXml() {
        StopPlace stopPlace = new StopPlace(
            new EmbeddableMultilingualString("Test Stop Place")
        );
        stopPlace.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);
        StopPlace saved = stopPlaceRepository.save(stopPlace);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML_VALUE);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
            WRITE_ENDPOINT + "/" + saved.getNetexId(),
            HttpMethod.GET,
            request,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Test Stop Place");
    }

    @Test
    public void ignoresUserDefinedFields() throws InterruptedException {
        String xml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace version="666">
                    <ValidBetween>
                        <FromDate>2024-01-01</FromDate>
                        <ToDate>2024-12-31</ToDate>
                    </ValidBetween>
                    <Name>Evil Station</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                </StopPlace>
            </stopPlaces>
            """;

        ResponseEntity<StopPlaceJobDto> response = postXml(
                xml,
                StopPlaceJobDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().jobId()).isNotNull();

        Long jobId = response.getBody().jobId();
        StopPlaceJobDto finalJob = awaitJobCompletion(jobId);

        assertThat(finalJob.createdIds()).isNotEmpty();

        ResponseEntity<String> getResponse = restTemplate.exchange(
                WRITE_ENDPOINT + "/" + finalJob.createdIds().getFirst().createdId(),
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String responseBody = getResponse.getBody();
        assertThat(responseBody).contains("version=\"1\"");
        assertThat(responseBody).doesNotContain("<FromDate>2024-01-01</FromDate>");
        assertThat(responseBody).doesNotContain("<ToDate>2024-12-31</ToDate>");
    }

    @Test
    public void createsImportedId() throws InterruptedException {
        String xml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace version="666" id="NSR:StopPlace:999">
                    <Name>Imported Station</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                </StopPlace>
            </stopPlaces>
            """;

        ResponseEntity<StopPlaceJobDto> response = postXml(
                xml,
                StopPlaceJobDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().jobId()).isNotNull();

        Long jobId = response.getBody().jobId();
        StopPlaceJobDto finalJob = awaitJobCompletion(jobId);

        assertThat(finalJob.createdIds()).isNotEmpty();

        ResponseEntity<String> getResponse = restTemplate.exchange(
                WRITE_ENDPOINT + "/" + finalJob.createdIds().getFirst().createdId(),
                HttpMethod.GET,
                null,
                String.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String responseBody = getResponse.getBody();

        assertThat(responseBody).contains("version=\"1\"");
        assertThat(responseBody).doesNotContain("id=\"NSR:StopPlace:999\"");
        assertThat(responseBody).contains(
                "id=\"" + finalJob.createdIds().getFirst().createdId() + "\""
        );

        assertThat(responseBody.replaceAll("\n", "").replaceAll(" ", "")).contains(
                "<KeyValue><Key>imported-id</Key><Value>NSR:StopPlace:999</Value></KeyValue>"
        );
    }

    private StopPlaceJobDto awaitJobCompletion(Long jobId)
        throws InterruptedException {
        String jobUrl = WRITE_ENDPOINT + "/jobs/" + jobId;
        for (int i = 0; i < 20; i++) {
            ResponseEntity<StopPlaceJobDto> jobResponse =
                restTemplate.getForEntity(jobUrl, StopPlaceJobDto.class);
            StopPlaceJobDto job = jobResponse.getBody();
            if (
                job != null &&
                job.status() != AsyncStopPlaceJobStatus.PROCESSING
            ) {
                return job;
            }
            Thread.sleep(200);
        }
        throw new AssertionError(
            "Job did not complete within the expected time"
        );
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

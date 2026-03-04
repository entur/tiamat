package org.rutebanken.tiamat.rest.write.controllers;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
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

import static org.assertj.core.api.Assertions.assertThat;

public class StopPlaceControllerTest extends TiamatIntegrationTest {

    private static final String WRITE_ENDPOINT = "/services/stop_places/write";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void createStopPlaceReturnsAcceptedWithProcessingJob() {
        String xml = """
                <stopPlaces xmlns="http://www.netex.org.uk/netex">
                    <StopPlace version="1">
                        <Name>Test Station</Name>
                        <StopPlaceType>busStation</StopPlaceType>
                    </StopPlace>
                </stopPlaces>
                """;

        ResponseEntity<StopPlaceJobDto> response = postXml(xml, StopPlaceJobDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().jobId()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(AsyncStopPlaceJobStatus.PROCESSING);
    }

    @Test
    public void createStopPlaceWithMultipleStopPlacesReturnsFailedJob() {
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

        ResponseEntity<StopPlaceJobDto> response = postXml(xml, StopPlaceJobDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(response.getBody().errorMessage()).contains("Only one stop place allowed");
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
    public void deleteStopPlaceReturnsAcceptedWithProcessingJob() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("To Be Deleted"));
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
        assertThat(response.getBody().status()).isEqualTo(AsyncStopPlaceJobStatus.PROCESSING);
    }

    @Test
    public void updateStopPlaceReturnsAcceptedWithProcessingJob() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Original Name"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        StopPlace saved = stopPlaceRepository.save(stopPlace);

        String xml = String.format("""
                <stopPlaces xmlns="http://www.netex.org.uk/netex">
                    <StopPlace id="%s" version="%d">
                        <Name>Updated Name</Name>
                        <StopPlaceType>busStation</StopPlaceType>
                    </StopPlace>
                </stopPlaces>
                """, saved.getNetexId(), saved.getVersion() + 1);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> request = new HttpEntity<>(xml, headers);

        ResponseEntity<StopPlaceJobDto> response = restTemplate.exchange(
                WRITE_ENDPOINT,
                HttpMethod.PATCH,
                request,
                StopPlaceJobDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().jobId()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(AsyncStopPlaceJobStatus.PROCESSING);
    }

    @Test
    public void getStopPlaceReturnsXml() {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Test Stop Place"));
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

    private <T> ResponseEntity<T> postXml(String xml, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> request = new HttpEntity<>(xml, headers);
        return restTemplate.postForEntity(WRITE_ENDPOINT, request, responseType);
    }
}

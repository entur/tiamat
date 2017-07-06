package org.rutebanken.tiamat.rest.dto;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class DtoQuayResourceIntegrationTest extends TiamatIntegrationTest {


    @Autowired
    StopPlaceVersionedSaverService saverService;

    @Before
    public void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    public void testQuayIdMapping() {

        StopPlace stopPlace = new StopPlace();

        String spOrigId = "TST:555";
        stopPlace.getOriginalIds().add(spOrigId);
        stopPlace.setStopPlaceType(StopTypeEnumeration.AIRPORT);

        Set<Quay> quays = new HashSet<>();
        Quay q1 = new Quay();
        String originalId = "TST:123";
        String originalId2 = "TST:1234";
        q1.getOriginalIds().add(originalId);
        q1.getOriginalIds().add(originalId2);

        quays.add(q1);
        stopPlace.setQuays(quays);

        stopPlace = saverService.saveNewVersion(stopPlace);
        StopPlace newVersion = saverService.createCopy(stopPlace, StopPlace.class);
        String originalId3 = "TST:12345";
        newVersion.getQuays().forEach(quay -> quay.getOriginalIds().add(originalId3));

        saverService.saveNewVersion(stopPlace, newVersion);

        String url =  "/jersey/quay/id_mapping";
        String responseWithoutStopPlaceType = getIdMapping(url);

        assertThat(responseWithoutStopPlaceType).contains(originalId + "," + q1.getNetexId() + "\n");
        assertThat(responseWithoutStopPlaceType).contains(originalId2 + "," + q1.getNetexId() + "\n");
        assertThat(responseWithoutStopPlaceType).contains(originalId3 + "," + q1.getNetexId() + "\n");
        assertThat(responseWithoutStopPlaceType).doesNotContain(spOrigId);

        String responseWithStopPlaceType = getIdMapping(url + "?includeStopType=true");
        assertThat(responseWithStopPlaceType).contains(originalId + "," + StopTypeEnumeration.AIRPORT.value() + "," + q1.getNetexId() + "\n");
    }

    @Test
    public void testStopPlaceIdMapping() {
        String url =  "/jersey/stop_place/id_mapping";

        StopPlace stopPlace = new StopPlace();
        String originalId = "TST:111";
        String originalId2 = "TST:123";
        stopPlace.getOriginalIds().add(originalId);
        stopPlace.getOriginalIds().add(originalId2);

        Set<Quay> quays = new HashSet<>();

        Quay q1 = new Quay();
        String quayOrigId = "TST:222";
        String quayOrigId2 = "TST:333";

        q1.getOriginalIds().add(quayOrigId);
        q1.getOriginalIds().add(quayOrigId2);

        quays.add(q1);

        stopPlace.setQuays(quays);

        stopPlace = stopPlaceRepository.save(stopPlace);

        String responseWithoutStopPlaceType = getIdMapping(url);

        assertThat(responseWithoutStopPlaceType).contains(originalId + "," + stopPlace.getNetexId() + "\n");
        assertThat(responseWithoutStopPlaceType).contains(originalId2 + "," + stopPlace.getNetexId() + "\n");
        assertThat(responseWithoutStopPlaceType).doesNotContain(quayOrigId);
        assertThat(responseWithoutStopPlaceType).doesNotContain(quayOrigId2);

        String responseWithStopPlaceType = getIdMapping(url+"?includeStopType=true");

        assertThat(responseWithStopPlaceType).contains(originalId + ",," + stopPlace.getNetexId() + "\n");
        assertThat(responseWithStopPlaceType).contains(originalId2 + ",," + stopPlace.getNetexId() + "\n");
    }

    private String getIdMapping(String url) {
        return given()
                .port(port)
                .get(url)
                .prettyPrint();
    }
}

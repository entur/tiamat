/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.rest.dto;

import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.config.JerseyConfig.SERVICES_ADMIN_PATH;
import static org.rutebanken.tiamat.config.JerseyConfig.SERVICES_STOP_PLACE_PATH;
import static org.rutebanken.tiamat.repository.QuayRepositoryImpl.JBV_CODE;

public class DtoResourceIntegrationTest extends TiamatIntegrationTest {

    private static final String PATH_MAPPING_STOP_PLACE = SERVICES_STOP_PLACE_PATH + "/mapping/stop_place";
    private static final String PATH_MAPPING_QUAY = SERVICES_STOP_PLACE_PATH + "/mapping/quay";
    private static final String PATH_MAPPING_JBV_CODE = SERVICES_ADMIN_PATH + "/jbv_code_mapping";

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
        StopPlace newVersion = versionCreator.createCopy(stopPlace, StopPlace.class);
        String originalId3 = "TST:12345";
        newVersion.getQuays().forEach(quay -> quay.getOriginalIds().add(originalId3));

        saverService.saveNewVersion(stopPlace, newVersion);

        String responseWithoutStopPlaceType = getIdMapping(PATH_MAPPING_QUAY);

        assertThat(responseWithoutStopPlaceType).contains(originalId + "," + q1.getNetexId() + "\n");
        assertThat(responseWithoutStopPlaceType).contains(originalId2 + "," + q1.getNetexId() + "\n");
        assertThat(responseWithoutStopPlaceType).contains(originalId3 + "," + q1.getNetexId() + "\n");
        assertThat(responseWithoutStopPlaceType).doesNotContain(spOrigId);

        String responseWithStopPlaceType = getIdMapping(PATH_MAPPING_QUAY + "?includeStopType=true");
        assertThat(responseWithStopPlaceType).contains(originalId + "," + StopTypeEnumeration.AIRPORT.value() + "," + q1.getNetexId() + "\n");
    }

    @Test
    public void testQuayJbvCodeMapping() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);

        String jbvCode1 = "123";
        String jbvCode2 = "1234";

        stopPlace.getOrCreateValues(JBV_CODE).add(jbvCode1);
        stopPlace.getOrCreateValues(JBV_CODE).add(jbvCode2);

        Quay quay = new Quay();
        quay.setPublicCode("10-43");
        stopPlace.getQuays().add(quay);

        stopPlace = saverService.saveNewVersion(stopPlace);

        StopPlace newVersion = versionCreator.createCopy(stopPlace, StopPlace.class);
        String jbvCode3 = "12345";
        newVersion.getOrCreateValues(JBV_CODE).add(jbvCode3);

        saverService.saveNewVersion(stopPlace, newVersion);

        String response = getIdMapping(PATH_MAPPING_JBV_CODE);

        assertThat(response)
                .contains(jbvCode1 + ":" + quay.getPublicCode() + "," + quay.getNetexId() + "\n")
                .contains(jbvCode1 + ":" + quay.getPublicCode() + "," + quay.getNetexId() + "\n")
                .contains(jbvCode1 + ":" + quay.getPublicCode() + "," + quay.getNetexId() + "\n")
                .contains(jbvCode1 + "," + stopPlace.getNetexId() + "\n");

    }

    @Test
    public void testStopPlaceIdMapping() {

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

        String responseWithoutStopPlaceType = getIdMapping(PATH_MAPPING_STOP_PLACE);

        assertThat(responseWithoutStopPlaceType).contains(originalId + "," + stopPlace.getNetexId() + "\n");
        assertThat(responseWithoutStopPlaceType).contains(originalId2 + "," + stopPlace.getNetexId() + "\n");
        assertThat(responseWithoutStopPlaceType).doesNotContain(quayOrigId);
        assertThat(responseWithoutStopPlaceType).doesNotContain(quayOrigId2);

        String responseWithStopPlaceType = getIdMapping(PATH_MAPPING_STOP_PLACE +"?includeStopType=true");

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

package org.rutebanken.tiamat.rest.dto;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.rutebanken.tiamat.CommonSpringBootTest;
import org.rutebanken.tiamat.dtoassembling.dto.QuayDto;
import org.rutebanken.tiamat.dtoassembling.dto.StopPlaceDto;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashSet;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

public class DtoStopPlaceResourceIntegrationTest extends CommonSpringBootTest {

    private static final String BASE_URI_STOP_PLACE = "/jersey/stop_place/";

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    private GeometryFactory geometryFactory;

    @Value("${local.server.port}")
    private int port;

    @Before
    public void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    public void createStopPlace() throws Exception {
        given()
            .contentType(ContentType.JSON)
            .content("{\n" +
                    "\"name\": \"Bogen skole\",\n" +
                    "\"shortName\": null,\n" +
                    "\"description\": null,\n" +
                    "\"centroid\": {\n" +
                    "\"location\": {\n" +
                    "\"longitude\": 17.003237,\n" +
                    "\"latitude\": 68.51526\n" +
                    "}\n" +
                    "},\n" +
                    "\"allAreasWheelchairAccessible\": false,\n" +
                    "\"stopPlaceType\": null,\n" +
                    "\"quays\": [\n" +
                    "{\n" +
                    "\"name\": \"quay 1\",\n" +
                    "\"shortName\": null,\n" +
                    "\"description\": \"\",\n" +
                    "\"centroid\": {\n" +
                    "\"location\": {\n" +
                    "\"longitude\": 17.003836999999997,\n" +
                    "\"latitude\": 68.51606\n" +
                    "}\n" +
                    "},\n" +
                    "\"allAreasWheelchairAccessible\": true,\n" +
                    "\"quayType\": \"other\"\n" +
                    "}\n" +
                    "]\n" +
                    "}")
        .when()
             .post("/jersey/stop_place")
        .then()
            .body("name", is("Bogen skole"))
            .body("id", notNullValue())
            .body("centroid.location.longitude", equalTo(17.003237f))
            .body("centroid.location.latitude", equalTo(68.51526f))
            .body("quays", is(not(empty())))
            .body("quays[0].name", equalTo("quay 1"));
    }

    @Test
    public void retrieveStopPlaceWithTwoQuays() throws Exception {
        Quay quay = new Quay();
        String firstQuayName = "first quay name";
        quay.setName(new EmbeddableMultilingualString(firstQuayName));

        Quay secondQuay = new Quay();
        String secondQuayName = "second quay";
        secondQuay.setName(new EmbeddableMultilingualString(secondQuayName));

        String stopPlaceName = "StopPlace";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        stopPlace.setQuays(new HashSet<>());
        stopPlace.getQuays().add(quay);
        stopPlace.getQuays().add(secondQuay);

        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(5, 60)));
        stopPlaceRepository.save(stopPlace);


        when()
            .get(BASE_URI_STOP_PLACE + stopPlace.getId())
        .then()
            .log().body()
            .statusCode(200)
            .body(Matchers.notNullValue())
            .assertThat()
            .body("name", equalTo(stopPlaceName))
            .body("quays.name", hasItems(firstQuayName, secondQuayName))
            .body("quays.id", hasItems(quay.getId().toString(), secondQuay.getId().toString()));
    }

    @Test
    public void searchForStopPlaceNoParams() throws Exception {
        String stopPlaceName = "Eselstua";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));
        stopPlaceRepository.save(stopPlace);

        when()
            .get(BASE_URI_STOP_PLACE)
        .then()
            .log().body()
            .statusCode(200)
            .body(Matchers.notNullValue())
            .assertThat()
            .body("[0].name", equalTo(stopPlaceName));
    }

    @Test
    public void searchForStopPlaceByNameContainsCaseInsensitive() throws Exception {
        String stopPlaceName = "Grytnes";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.533212, 59.678080)));
        stopPlaceRepository.save(stopPlace);

        given()
            .param("q", "ytNES")
        .when()
            .get(BASE_URI_STOP_PLACE)
        .then()
            .log().body()
            .statusCode(200)
            .body(Matchers.notNullValue())
            .assertThat()
            .body("[0].name", equalTo(stopPlaceName));
    }

    @Test
    public void searchForStopsWithDifferentStopPlaceTypeShouldHaveNoResult() {

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Fyrstekakeveien"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_TRAM);
        stopPlaceRepository.save(stopPlace);

        given()
            .param("stopPlaceType", StopTypeEnumeration.FERRY_STOP.value())
        .when()
            .get(BASE_URI_STOP_PLACE)
        .then()
            .log().body()
            .statusCode(200)
            .assertThat()
                .body("$", Matchers.hasSize(0));
    }


    @Test
    public void searchForTramStopWithMunicipalityAndCounty() {

        TopographicPlace hordaland = new TopographicPlace(new EmbeddableMultilingualString("Hordaland"));
        topographicPlaceRepository.save(hordaland);

        TopographicPlace kvinnherad = createMunicipalityWithCountyRef("Kvinnherad", hordaland);

        StopPlace stopPlace = createStopPlaceWithMunicipalityRef("Anda", kvinnherad, StopTypeEnumeration.TRAM_STATION);
        stopPlaceRepository.save(stopPlace);

        given()
            .param("q", "A")
            .param("stopPlaceType", StopTypeEnumeration.TRAM_STATION.value())
            .param("municipalityReference", kvinnherad.getId().toString())
            .param("countyReference", hordaland.getId().toString())
        .when()
            .get(BASE_URI_STOP_PLACE)
        .then()
            .log().body()
            .statusCode(200)
            .body(Matchers.notNullValue())
            .assertThat()
            .body("$", hasSize(1))
            .body("[0].name", equalTo(stopPlace.getName().getValue()));
    }

    @Test
    public void searchForStopsInMunicipalityThenExpectNoResult() {
        // Stop Place not related to municipality
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Nesbru"));
        stopPlaceRepository.save(stopPlace);

        TopographicPlace asker = new TopographicPlace(new EmbeddableMultilingualString("Asker"));
        topographicPlaceRepository.save(asker);

        given()
            .param("municipalityReference", asker.getId().toString())
        .when()
            .get(BASE_URI_STOP_PLACE)
        .then()
            .log().body()
            .statusCode(200)
            .assertThat()
            .body("$", hasSize(0));
    }

    @Test
    public void searchForStopInMunicipalityOnly() {
        TopographicPlace akershus = topographicPlaceRepository.save(new TopographicPlace(new EmbeddableMultilingualString("Akershus")));
        TopographicPlace asker = createMunicipalityWithCountyRef("Asker", akershus);
        createStopPlaceWithMunicipalityRef("Nesbru", asker);

        StopPlaceDto[] result = given()
                .param("municipalityReference", asker.getId().toString())
                .get(BASE_URI_STOP_PLACE)
                .as(StopPlaceDto[].class);

        assertThat(result).extracting("name").contains("Nesbru");
    }

    @Test
    public void searchForStopsInTwoMunicipalities() {
        TopographicPlace asker = createMunicipalityWithCountyRef("Asker", null);
        TopographicPlace baerum = createMunicipalityWithCountyRef("Bærum", null);

        createStopPlaceWithMunicipalityRef("Nesbru", asker);
        createStopPlaceWithMunicipalityRef("Oksenøyveien", baerum);

        StopPlaceDto[] result = given()
            .param("municipalityReference", baerum.getId().toString())
            .param("municipalityReference", asker.getId().toString())
            .get(BASE_URI_STOP_PLACE)
                .as(StopPlaceDto[].class);

        assertThat(result).extracting("name").contains("Nesbru", "Oksenøyveien");
    }

    @Test
    public void searchForStopsInTwoCountiesAndTwoMunicipalities() {
        TopographicPlace akershus = topographicPlaceRepository.save(new TopographicPlace(new EmbeddableMultilingualString("Akershus")));
        TopographicPlace buskerud = topographicPlaceRepository.save(new TopographicPlace(new EmbeddableMultilingualString("Buskerud")));

        TopographicPlace lier = createMunicipalityWithCountyRef("Lier", buskerud);
        TopographicPlace asker = createMunicipalityWithCountyRef("Asker", akershus);

        createStopPlaceWithMunicipalityRef("Nesbru", asker);
        createStopPlaceWithMunicipalityRef("Hennumkrysset", asker);

        StopPlaceDto[] result = given()
                .param("countyReference", buskerud.getId().toString())
                .param("countyReference", akershus.getId().toString())
                .param("municipalityReference", lier.getId().toString())
                .param("municipalityReference", asker.getId().toString())
                .get(BASE_URI_STOP_PLACE)
                .as(StopPlaceDto[].class);

        assertThat(result).extracting("name").contains("Nesbru", "Hennumkrysset");
    }

    @Test
    public void searchForStopsInDifferentMunicipalitiesButSameCounty() {
        TopographicPlace akershus = topographicPlaceRepository.save(new TopographicPlace(new EmbeddableMultilingualString("Akershus")));
        TopographicPlace asker = createMunicipalityWithCountyRef("Asker", akershus);
        TopographicPlace baerum = createMunicipalityWithCountyRef("Bærum", akershus);

        createStopPlaceWithMunicipalityRef("Måsan", asker);
        createStopPlaceWithMunicipalityRef("Haslum", baerum);

        StopPlaceDto[] result = given()
                .param("countyReference", akershus.getId().toString())
                .get(BASE_URI_STOP_PLACE)
                .as(StopPlaceDto[].class);

        assertThat(result).extracting("name").contains("Måsan", "Haslum");
    }

    @Test
    public void searchForStopById() throws Exception {

        StopPlace stopPlace = createStopPlace("Espa");
        stopPlaceRepository.save(stopPlace);


        StopPlaceDto[] result = given()
                .param("q", stopPlace.getId())
                .get(BASE_URI_STOP_PLACE)
                .as(StopPlaceDto[].class);

        assertThat(result).extracting("name").contains("Espa");
    }

    /**
     * https://rutebanken.atlassian.net/browse/NRP-677
     */
    @Test
    public void createStopPlaceShouldExposeQuayIds() {
        StopPlaceDto stopPlaceDto = new StopPlaceDto();
        stopPlaceDto.quays = new ArrayList<>(1);
        stopPlaceDto.quays.add(new QuayDto());


        given()
                .contentType(ContentType.JSON)
                .body(stopPlaceDto)
        .when()
                .post(BASE_URI_STOP_PLACE)
        .then()
                .log().body()
                .assertThat()
                .body("quays[0].id", notNullValue());
    }

    /**
     * https://rutebanken.atlassian.net/browse/NRP-677
     */
    @Test
    public void createStopPlaceWithNewQuayShouldExposeQuayIds() {
        StopPlaceDto stopPlaceDto = new StopPlaceDto();
        stopPlaceDto.quays = new ArrayList<>(1);
        QuayDto quayDto = new QuayDto();
        quayDto.name = "quay 1";
        stopPlaceDto.quays.add(quayDto);


        // Create
        stopPlaceDto = given()
                .contentType(ContentType.JSON)
                .body(stopPlaceDto)
                .post(BASE_URI_STOP_PLACE)
                .as(StopPlaceDto.class);

        // Add new quay
        QuayDto anotherQuayDto = new QuayDto();
        anotherQuayDto.name = "quay 2";
        stopPlaceDto.quays.add(anotherQuayDto);

        // Update
        stopPlaceDto = given()
                .contentType(ContentType.JSON)
                .body(stopPlaceDto)
                .post(BASE_URI_STOP_PLACE + stopPlaceDto.id)
                .as(StopPlaceDto.class);

        assertThat(stopPlaceDto.quays)
                .hasSize(2)
                .extracting(actualQuayDto -> actualQuayDto.id)
                    .doesNotContain("null")
                    .isNotNull()
                    .isNotEmpty();

    }

    private StopPlace createStopPlaceWithMunicipalityRef(String name, TopographicPlace municipality, StopTypeEnumeration type) {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(name));
        stopPlace.setStopPlaceType(type);
        if(municipality != null) {
            stopPlace.setTopographicPlace(municipality);
        }
        stopPlaceRepository.save(stopPlace);
        return stopPlace;
    }

    private StopPlace createStopPlace(String name) {
        return createStopPlaceWithMunicipalityRef(name, null);
    }

    private StopPlace createStopPlaceWithMunicipalityRef(String name, TopographicPlace municipality) {
        return createStopPlaceWithMunicipalityRef(name, municipality, null);
    }

    private TopographicPlace createMunicipalityWithCountyRef(String name, TopographicPlace county) {
        TopographicPlace municipality = new TopographicPlace(new EmbeddableMultilingualString(name));
        if(county != null) {
            municipality.setParentTopographicPlace(county);
        }
        topographicPlaceRepository.save(municipality);
        return municipality;
    }

}

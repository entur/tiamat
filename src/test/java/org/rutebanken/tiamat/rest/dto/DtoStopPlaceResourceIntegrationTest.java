package org.rutebanken.tiamat.rest.dto;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.json.JSONArray;
import org.rutebanken.tiamat.TiamatTestApplication;
import org.rutebanken.tiamat.dtoassembling.dto.StopPlaceDto;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT, classes = TiamatTestApplication.class)
@ActiveProfiles("geodb")
public class DtoStopPlaceResourceIntegrationTest {

    private static final String BASE_URI_STOP_PLACE = "/jersey/stop_place/";

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;


    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private GeometryFactory geometryFactory;

    @Value("${local.server.port}")
    private int port;

    @Before
    public void configureRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Before
    public void clearRepositories() {
        stopPlaceRepository.deleteAll();
        quayRepository.deleteAll();
        topographicPlaceRepository.deleteAll();
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
        quay.setName(new MultilingualString(firstQuayName));

        quayRepository.save(quay);

        Quay secondQuay = new Quay();
        String secondQuayName = "second quay";
        secondQuay.setName(new MultilingualString(secondQuayName));

        quayRepository.save(secondQuay);

        String stopPlaceName = "StopPlace";
        StopPlace stopPlace = new StopPlace(new MultilingualString(stopPlaceName));

        stopPlace.setQuays(new ArrayList<>());
        stopPlace.getQuays().add(quay);
        stopPlace.getQuays().add(secondQuay);

        stopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(5, 60)))));
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
        StopPlace stopPlace = new StopPlace(new MultilingualString(stopPlaceName));
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
        StopPlace stopPlace = new StopPlace(new MultilingualString(stopPlaceName));
        stopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(10.533212, 59.678080)))));
        stopPlaceRepository.save(stopPlace);

        given()
            .queryParam("name", "ytNES")
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

        StopPlace stopPlace = new StopPlace(new MultilingualString("Fyrstekakeveien"));
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

        TopographicPlace hordaland = new TopographicPlace(new MultilingualString("Hordaland"));
        topographicPlaceRepository.save(hordaland);

        TopographicPlace kvinnherad = createMunicipalityWithCountyRef("Kvinnherad", hordaland);

        StopPlace stopPlace = createStopPlaceWithMunicipalityRef("Anda", kvinnherad, StopTypeEnumeration.TRAM_STATION);
        stopPlaceRepository.save(stopPlace);

        given()
            .param("name", "A")
            .param("stopPlaceType", "onstreetTram")
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
        StopPlace stopPlace = new StopPlace(new MultilingualString("Nesbru"));
        stopPlaceRepository.save(stopPlace);

        TopographicPlace asker = new TopographicPlace(new MultilingualString("Asker"));
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
        TopographicPlace akershus = topographicPlaceRepository.save(new TopographicPlace(new MultilingualString("Akershus")));
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
        TopographicPlace akershus = topographicPlaceRepository.save(new TopographicPlace(new MultilingualString("Akershus")));
        TopographicPlace buskerud = topographicPlaceRepository.save(new TopographicPlace(new MultilingualString("Buskerud")));

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
        TopographicPlace akershus = topographicPlaceRepository.save(new TopographicPlace(new MultilingualString("Akershus")));
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

    private StopPlace createStopPlaceWithMunicipalityRef(String name, TopographicPlace municipality, StopTypeEnumeration type) {
        StopPlace stopPlace = new StopPlace(new MultilingualString(name));
        stopPlace.setStopPlaceType(type);
        TopographicPlaceRefStructure municipalityRef = new TopographicPlaceRefStructure();
        municipalityRef.setRef(municipality.getId().toString());
        stopPlace.setTopographicPlaceRef(municipalityRef);
        stopPlaceRepository.save(stopPlace);
        return stopPlace;
    }

    private StopPlace createStopPlaceWithMunicipalityRef(String name, TopographicPlace municipality) {
        return createStopPlaceWithMunicipalityRef(name, municipality, null);
    }

    private TopographicPlace createMunicipalityWithCountyRef(String name, TopographicPlace county) {
        TopographicPlace municipality = new TopographicPlace(new MultilingualString(name));
        if(county != null) {
            TopographicPlaceRefStructure countyRef = new TopographicPlaceRefStructure();
            countyRef.setRef(county.getId().toString());
            municipality.setParentTopographicPlaceRef(countyRef);
        }
        topographicPlaceRepository.save(municipality);
        return municipality;
    }

}

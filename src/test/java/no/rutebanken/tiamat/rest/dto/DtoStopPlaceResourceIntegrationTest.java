package no.rutebanken.tiamat.rest.dto;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import no.rutebanken.tiamat.TiamatTestApplication;
import no.rutebanken.tiamat.model.*;
import no.rutebanken.tiamat.repository.QuayRepository;
import no.rutebanken.tiamat.repository.StopPlaceRepository;
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

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT, classes = TiamatTestApplication.class)
@ActiveProfiles("geodb")
public class DtoStopPlaceResourceIntegrationTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

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
        ;


    }

    @Test
    public void retrieveStopPlaceWithTwoQuays() throws Exception {

        // Create a stop place with two quays

        String lang = "en";

        Quay quay = new Quay();
        String firstQuayName = "first quay name";
        quay.setName(new MultilingualString(firstQuayName, lang, ""));

        quayRepository.save(quay);

        Quay secondQuay = new Quay();
        String secondQuayName = "second quay";
        secondQuay.setName(new MultilingualString(secondQuayName, lang, ""));

        quayRepository.save(secondQuay);

        StopPlace stopPlace = new StopPlace();

        String stopPlaceName = "StopPlace";

        stopPlace.setName(new MultilingualString(stopPlaceName, lang, ""));
        stopPlace.setQuays(new ArrayList<>());
        stopPlace.getQuays().add(quay);
        stopPlace.getQuays().add(secondQuay);

        stopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(5, 60)))));
        stopPlaceRepository.save(stopPlace);

        get("/jersey/stop_place/" + stopPlace.getId())
                .then()
                .log().body()
                .statusCode(200)
                .body(Matchers.notNullValue())
                .assertThat()
                .body("name", equalTo(stopPlaceName))
                .body("quays.name", hasItems(firstQuayName, secondQuayName))
                .body("quays.id", hasItems(quay.getId(), secondQuay.getId()));

    }


}
package no.rutebanken.tiamat.rest.dto;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import no.rutebanken.tiamat.TiamatTestApplication;
import no.rutebanken.tiamat.model.*;
import no.rutebanken.tiamat.repository.QuayRepository;
import no.rutebanken.tiamat.repository.StopPlaceRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

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
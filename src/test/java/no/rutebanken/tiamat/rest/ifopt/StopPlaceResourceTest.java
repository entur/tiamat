package no.rutebanken.tiamat.rest.ifopt;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import no.rutebanken.tiamat.TiamatIntegrationTestApplication;
import no.rutebanken.tiamat.repository.ifopt.QuayRepository;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.org.netex.netex.MultilingualString;
import uk.org.netex.netex.Quay;
import uk.org.netex.netex.SimplePoint;
import uk.org.netex.netex.StopPlace;

import java.util.ArrayList;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TiamatIntegrationTestApplication.class)
@WebIntegrationTest
@ActiveProfiles("geodb")
public class StopPlaceResourceTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private GeometryFactory geometryFactory;

    @Value("${local.server.port}")
    private int port;

    @Test
    public void testXmlExportOfStopPlaceWithTwoQuays() throws Exception {

        // Create a stop place with two quays

        String lang = "en";

        Quay quay = new Quay();
        String firstQuayName = "first quay name";
        quay.setName(new MultilingualString(firstQuayName, lang, ""));

        quayRepository.save(quay);

        Quay secondQuay = new Quay();
        secondQuay.setName(new MultilingualString("second quay", lang, ""));

        quayRepository.save(secondQuay);

        StopPlace stopPlace = new StopPlace();

        String stopPlaceName = "StopPlace";

        stopPlace.setName(new MultilingualString(stopPlaceName, lang, ""));
        stopPlace.setQuays(new ArrayList<>());
        stopPlace.getQuays().add(quay);
        stopPlace.getQuays().add(secondQuay);

        stopPlace.setCentroid(new SimplePoint(geometryFactory.createPoint(new Coordinate(60, 11))));
        stopPlaceRepository.save(stopPlace);

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        get("/jersey/stop_place/xml/" + stopPlace.getId())
                .then()
                .log().body()
                .statusCode(200)
                .body(notNullValue())
                .assertThat()
                .body(hasXPath("/StopPlace/Name[text()='"+stopPlaceName+"']"))
                .body(hasXPath("/StopPlace/Name[@lang='"+lang+"']"))
                .body(hasXPath("/StopPlace/quays"))
                .body(hasXPath("/StopPlace/quays/Quay"))
                .body(hasXPath("/StopPlace/quays/Quay/Name[text()='"+firstQuayName+"']"));

    }

    @Ignore
    @Test
    public void testXmlExportImportOfStopPlaces() throws Exception {
        Quay quay = new Quay();
        quay.setName(new MultilingualString("q", "en", ""));
//quay.setCentroid(new SimplePoint());
        quayRepository.save(quay);

        StopPlace stopPlace = new StopPlace();

        stopPlace.setQuays(new ArrayList<>());
        stopPlace.getQuays().add(quay);

        // Geometry factory needs JsonBackReference annotation
        stopPlace.setCentroid(new SimplePoint(geometryFactory.createPoint(new Coordinate(60, 11))));

        stopPlaceRepository.save(stopPlace);

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        String xml =
                get("/jersey/stop_place/xml")
                        .then()
                        .log().body()
                        .statusCode(200)
                        .body(notNullValue())
                        .extract().body().asString();

        given()
                .contentType(ContentType.XML)
                .content(xml)
                .when()
                .post("jersey/stop_place/xml")
                .then()
                .body(containsString("OK"));



    }

}
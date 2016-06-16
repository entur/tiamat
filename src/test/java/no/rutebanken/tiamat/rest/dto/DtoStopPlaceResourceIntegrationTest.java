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
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
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
import static com.jayway.restassured.path.xml.XmlPath.from;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TiamatTestApplication.class)
@WebIntegrationTest
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

        stopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(5, 60)))));
        stopPlaceRepository.save(stopPlace);

        get("/jersey/stop_place/xml/" + stopPlace.getId())
                .then()
                .log().body()
                .statusCode(200)
                .body(Matchers.notNullValue())
                .assertThat()
                .body(Matchers.hasXPath("/StopPlace/Name[text()='"+stopPlaceName+"']"))
                .body(Matchers.hasXPath("/StopPlace/Name[@lang='"+lang+"']"))
                .body(Matchers.hasXPath("/StopPlace/quays"))
                .body(Matchers.hasXPath("/StopPlace/quays/Quay"))
                .body(Matchers.hasXPath("/StopPlace/quays/Quay/Name[text()='"+firstQuayName+"']"));

    }

    @Test
    public void testXmlExportOfMultipleStopPlaces() throws Exception {

        StopPlace firstStopPlace = new StopPlace();
        String firstStopPlaceName = "first stop place name";
        firstStopPlace.setName(new MultilingualString(firstStopPlaceName, "en", ""));
        firstStopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(5, 60)))));
        stopPlaceRepository.save(firstStopPlace);

        StopPlace secondStopPlace = new StopPlace();
        secondStopPlace.setName(new MultilingualString("second stop place name", "en", ""));
        stopPlaceRepository.save(secondStopPlace);

        get("/jersey/stop_place/xml/")
                .then()
                .log().body()
                .statusCode(200)
                .body(Matchers.notNullValue())
                .assertThat()
                .body(Matchers.hasXPath("/stopPlaces/StopPlace/Name[text()='" + firstStopPlaceName + "']"));
    }

    @Ignore
    @Test
    public void testXmlExportImportOfStopPlaces() throws Exception {
        Quay quay = new Quay();
        quay.setName(new MultilingualString("q", "en", ""));
        quayRepository.save(quay);

        StopPlace stopPlace = new StopPlace();

        stopPlace.setQuays(new ArrayList<>());
        stopPlace.getQuays().add(quay);

        // Geometry factory needs JsonBackReference annotation
        stopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(11, 60)))));

        stopPlaceRepository.save(stopPlace);
        System.out.println(stopPlace.getId());

        // Export
        String xml = get("/jersey/stop_place/xml")
                        .then()
                        .log().body()
                        .statusCode(200)
                        .body(Matchers.notNullValue())
                        .extract().body().asString();

        stopPlaceRepository.delete(stopPlace);

        // Remove the id to save again like fresh
        xml = xml.replaceAll("id=\"[a-z0-9-]*\"", "");
        System.out.println("Removed IDs from xml: " + xml);

        // Post it back
        String response = given()
                .contentType(ContentType.XML)
                .content(xml)
                .when()
                .post("jersey/stop_place/xml")
                .then()
                .extract()
                .body()
                .asString();

        String createdStopPlaceId = XmlPath.from(response).get("stopPlaces[0]").toString();
        System.out.println("Got this id back: " + createdStopPlaceId);

        StopPlace stopPlaceImportedFromXml = stopPlaceRepository.findOne(createdStopPlaceId);
        AssertionsForClassTypes.assertThat(stopPlaceImportedFromXml).isNotNull();
        AssertionsForInterfaceTypes.assertThat(stopPlaceImportedFromXml.getQuays()).hasSize(stopPlace.getQuays().size());
        System.out.println(stopPlaceImportedFromXml.getQuays().get(0).getId());
    }

    @Ignore
    @Test
    public void validateStopPlacesXmlAgainstXsd() throws IOException, SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final Schema schema = schemaFactory.newSchema(new URL("https://raw.githubusercontent.com/StichtingOpenGeo/NeTEx/master/xsd/ifopt.xsd"));
        final Validator validator = schema.newValidator();
        validator.validate(new StreamSource("http://localhost:1888/jersey/stop_place/xml"));
    }

}
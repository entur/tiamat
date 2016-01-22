package no.rutebanken.tiamat.rest.ifopt;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import no.rutebanken.tiamat.TiamatTestApplication;
import no.rutebanken.tiamat.repository.ifopt.StopPlaceRepository;
import no.rutebanken.tiamat.repository.ifopt.TopographicPlaceRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.org.netex.netex.*;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TiamatTestApplication.class)
@WebIntegrationTest
@ActiveProfiles("geodb")
public class SiteFrameResourceIntegrationTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private GeometryFactory geometryFactory;

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

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
        topographicPlaceRepository.deleteAll();
    }

    @Test
    public void textXmlExportOfSiteFrame() throws Exception {

        CountryRef countryRef = new CountryRef();
        countryRef.setRef(IanaCountryTldEnumeration.NO);

        TopographicPlace county = new TopographicPlace();
        county.setName(new MultilingualString("Buskerud", "no", ""));
        county.setCountryRef(countryRef);

        topographicPlaceRepository.save(county);

        TopographicPlaceRefStructure countyReference = new TopographicPlaceRefStructure();
        countyReference.setRef(county.getId());

        TopographicPlace municipality = new TopographicPlace();
        municipality.setName(new MultilingualString("Nedre Eiker", "no", ""));
        municipality.setParentTopographicPlaceRef(countyReference);
        municipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.TOWN);
        municipality.setCountryRef(countryRef);

        topographicPlaceRepository.save(municipality);

        TopographicPlaceRefStructure topographicPlaceRefStructure = new TopographicPlaceRefStructure();
        topographicPlaceRefStructure.setRef(municipality.getId());

        StopPlace stopPlace = new StopPlace();
        String firstStopPlaceName = "first stop place name";
        stopPlace.setName(new MultilingualString(firstStopPlaceName, "en", ""));
        stopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(5, 60)))));
        stopPlace.setTopographicPlaceRef(topographicPlaceRefStructure);

        stopPlaceRepository.save(stopPlace);

        String xml = get("/jersey/site_frame")
                .then()
                .log()
                .body()
                .statusCode(200)
                .body(notNullValue())
                .assertThat()
                .body(hasXPath("/SiteFrame/stopPlaces/StopPlace/Name[text()='" + firstStopPlaceName + "']"))
                .extract()
                .body()
                .asString();

        stopPlaceRepository.delete(stopPlace);
        topographicPlaceRepository.delete(municipality);
        topographicPlaceRepository.delete(county);

        System.out.println("---------------------");
        System.out.println("About to post the xml");
        given().contentType(ContentType.XML)
                .content(xml)
                .when()
                .post("/jersey/site_frame")
                .then()
                .body(containsString("Saved"));
    }

}
package org.rutebanken.tiamat.rest.netex.siteframe;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.rutebanken.tiamat.TiamatTestApplication;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT, classes = TiamatTestApplication.class)
@ActiveProfiles("geodb")
public class SiteFrameResourceIntegrationTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private GeometryFactory geometryFactory;

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    private QuayRepository quayRepository;

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


    /**
     * Verify that the REST service can export and import a SiteFrame with stop place, quay and topographic place.
     */
    @Test
    public void netexExportImportSiteFrameWithStopPlaceAndTopographicalPlace() throws Exception {

        CountryRef countryRef = new CountryRef();
        countryRef.setRef(IanaCountryTldEnumeration.NO);

        TopographicPlace county = new TopographicPlace();
        county.setName(new EmbeddableMultilingualString("Buskerud", "no"));
        county.setCountryRef(countryRef);

        topographicPlaceRepository.save(county);

        TopographicPlaceRefStructure countyReference = new TopographicPlaceRefStructure();
        countyReference.setRef(String.valueOf(county.getId()));

        TopographicPlace municipality = new TopographicPlace();
        municipality.setName(new EmbeddableMultilingualString("Nedre Eiker", "no"));
        municipality.setParentTopographicPlaceRef(countyReference);
        municipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.TOWN);
        municipality.setCountryRef(countryRef);

        topographicPlaceRepository.save(municipality);

        TopographicPlaceRefStructure topographicPlaceRefStructure = new TopographicPlaceRefStructure();
        topographicPlaceRefStructure.setRef(String.valueOf(municipality.getId()));

        StopPlace stopPlace = new StopPlace();
        String firstStopPlaceName = "first stop place name";
        stopPlace.setName(new EmbeddableMultilingualString(firstStopPlaceName, "en"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(5, 60)));
        stopPlace.setTopographicPlaceRef(topographicPlaceRefStructure);


        Quay quay = new Quay();
        quay.setName(new EmbeddableMultilingualString("quay", "en"));
        quay.setCentroid(geometryFactory.createPoint(new Coordinate(6, 70)));

        quayRepository.save(quay);
        stopPlace.getQuays().add(quay);
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

        System.out.println(xml);

        System.out.println("---------------------");
        System.out.println("About to post the xml back");
        given().contentType(ContentType.XML)
                .content(xml)
                .when()
                .post("/jersey/site_frame")
                .then()
                .body(containsString("Imported"));
    }

}
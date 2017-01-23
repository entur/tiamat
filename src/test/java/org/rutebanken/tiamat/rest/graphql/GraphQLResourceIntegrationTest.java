package org.rutebanken.tiamat.rest.graphql;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rutebanken.tiamat.TiamatTestApplication;
import org.rutebanken.tiamat.dtoassembling.dto.QuayDto;
import org.rutebanken.tiamat.dtoassembling.dto.StopPlaceDto;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.HashSet;

import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT, classes = TiamatTestApplication.class)
@ActiveProfiles("geodb")
public class GraphQLResourceIntegrationTest {
    private static final String BASE_URI_GRAPHQL = "/jersey/graphql/";

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
    public void retrieveStopPlaceWithTwoQuays() throws Exception {
        Quay quay = new Quay();
        String firstQuayName = "first quay name";
        quay.setName(new EmbeddableMultilingualString(firstQuayName));

        quayRepository.save(quay);

        Quay secondQuay = new Quay();
        String secondQuayName = "second quay";
        secondQuay.setName(new EmbeddableMultilingualString(secondQuayName));

        quayRepository.save(secondQuay);

        String stopPlaceName = "StopPlace";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        stopPlace.setQuays(new HashSet<>());
        stopPlace.getQuays().add(quay);
        stopPlace.getQuays().add(secondQuay);

        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(5, 60)));
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = "{" +
                "\"query\":\"" +
                "{ stopPlace:" + GraphQLNames.FIND_STOPPPLACE_BY_ID + " (id:" + stopPlace.getId() + ") {" +
                "   id " +
                "   name { value } " +
                "   quays { " +
                "      id " +
                "      name  { value } " +
                "     }" +
                "}" +
                "}\",\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName))
                .body("data.stopPlace[0].quays.name.value", hasItems(firstQuayName, secondQuayName))
                .body("data.stopPlace[0].quays.id", hasItems(quay.getId().intValue(), secondQuay.getId().intValue()));
    }

    @Test
    public void searchForStopPlaceNoParams() throws Exception {
        String stopPlaceName = "Eselstua";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace: " + GraphQLNames.STOPPLACE_SEARCH +
                " { name { value } " +
                "}" +
                "}\",\"variables\":\"\"}";


        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName));
    }

    @Test
    public void searchForStopPlaceByNameContainsCaseInsensitive() throws Exception {
        String stopPlaceName = "Grytnes";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.533212, 59.678080)));
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = "{" +
                "\"query\":\"{" +
                "  stopPlace: " + GraphQLNames.STOPPLACE_SEARCH + " (query:\\\"ytNES\\\") { " +
                "    name {value} " +
                "  } " +
                "}\"," +
                "\"variables\":\"\"}";


        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName));
    }

    @Test
    public void searchForStopsWithDifferentStopPlaceTypeShouldHaveNoResult() {

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Fyrstekakeveien"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_TRAM);
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = "{" +
                "\"query\":\"{" +
                "  stopPlace: " + GraphQLNames.STOPPLACE_SEARCH +  " (stopPlaceType:" + StopTypeEnumeration.FERRY_STOP.value() + ") { " +
                "    name {value} " +
                "  } " +
                "}\"," +
                "\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace", Matchers.hasSize(0));
    }

    private ValidatableResponse executeGraphQL(String graphQlJsonQuery) {
        return given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(graphQlJsonQuery)
        .when()
                .post(BASE_URI_GRAPHQL)
                .then()
                .log().body()
                .statusCode(200)
                .assertThat();
    }


    @Test
    public void searchForTramStopWithMunicipalityAndCounty() {

        TopographicPlace hordaland = new TopographicPlace(new EmbeddableMultilingualString("Hordaland"));
        topographicPlaceRepository.save(hordaland);

        TopographicPlace kvinnherad = createMunicipalityWithCountyRef("Kvinnherad", hordaland);

        StopPlace stopPlace = createStopPlaceWithMunicipalityRef("Anda", kvinnherad, StopTypeEnumeration.TRAM_STATION);
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = "{" +
                "\"query\":\"{" +
                "  stopPlace:" + GraphQLNames.STOPPLACE_SEARCH +
                " (stopPlaceType:" + StopTypeEnumeration.TRAM_STATION.value() + " countyReference:" + hordaland.getId() + " municipalityReference:" + kvinnherad.getId() +") { " +
                "    name {value} " +
                "  } " +
                "}\"," +
                "\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace", hasSize(1))
                .body("data.stopPlace[0].name.value", equalTo(stopPlace.getName().getValue()));
    }

    @Test
    public void searchForStopsInMunicipalityThenExpectNoResult() {
        // Stop Place not related to municipality
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Nesbru"));
        stopPlaceRepository.save(stopPlace);

        TopographicPlace asker = new TopographicPlace(new EmbeddableMultilingualString("Asker"));
        topographicPlaceRepository.save(asker);

        String graphQlJsonQuery = "{" +
                "\"query\":\"{" +
                "  stopPlace:" + GraphQLNames.STOPPLACE_SEARCH +
                " (municipalityReference:" + asker.getId() +") { " +
                "    name {value} " +
                "  } " +
                "}\"," +
                "\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace", hasSize(0));
    }

    @Test
    public void searchForStopInMunicipalityOnly() {
        TopographicPlace akershus = topographicPlaceRepository.save(new TopographicPlace(new EmbeddableMultilingualString("Akershus")));
        TopographicPlace asker = createMunicipalityWithCountyRef("Asker", akershus);
        String stopPlaceName = "Nesbru";
        createStopPlaceWithMunicipalityRef(stopPlaceName, asker);

        String graphQlJsonQuery = "{" +
                "\"query\":\"{" +
                "  stopPlace:" + GraphQLNames.STOPPLACE_SEARCH +
                " (municipalityReference:" + asker.getId() +") { " +
                "    name {value} " +
                "  } " +
                "}\"," +
                "\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace", hasSize(1))
                .body("data.stopPlace[0].name.value",  equalTo(stopPlaceName));
    }

    @Test
    public void searchForStopsInTwoMunicipalities() {
        TopographicPlace asker = createMunicipalityWithCountyRef("Asker", null);
        TopographicPlace baerum = createMunicipalityWithCountyRef("Bærum", null);

        createStopPlaceWithMunicipalityRef("Nesbru", asker);
        createStopPlaceWithMunicipalityRef("Slependen", baerum);


        String graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace:" + GraphQLNames.STOPPLACE_SEARCH +
                " (municipalityReference:["+baerum.getId()+","+asker.getId()+"]) {" +
                "id " +
                "name { value } " +
                "quays " +
                "  { " +
                "   id " +
                "   name  { value } " +
                "  }  " +
                "}" +
                "}\",\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace.name.value", hasItems("Nesbru", "Slependen"));
    }

    @Test
    public void searchForStopsInTwoCountiesAndTwoMunicipalities() {
        TopographicPlace akershus = topographicPlaceRepository.save(new TopographicPlace(new EmbeddableMultilingualString("Akershus")));
        TopographicPlace buskerud = topographicPlaceRepository.save(new TopographicPlace(new EmbeddableMultilingualString("Buskerud")));

        TopographicPlace lier = createMunicipalityWithCountyRef("Lier", buskerud);
        TopographicPlace asker = createMunicipalityWithCountyRef("Asker", akershus);

        createStopPlaceWithMunicipalityRef("Nesbru", asker);
        createStopPlaceWithMunicipalityRef("Hennumkrysset", asker);

        String graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace:" + GraphQLNames.STOPPLACE_SEARCH +
                " (countyReference:["+akershus.getId()+","+buskerud.getId()+"] municipalityReference:["+lier.getId()+","+asker.getId()+"]) {" +
                "id " +
                "name { value } " +
                "}" +
                "}\",\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace.name.value", hasItems("Nesbru", "Hennumkrysset"));
    }

    @Test
    public void searchForStopsInDifferentMunicipalitiesButSameCounty() {
        TopographicPlace akershus = topographicPlaceRepository.save(new TopographicPlace(new EmbeddableMultilingualString("Akershus")));
        TopographicPlace asker = createMunicipalityWithCountyRef("Asker", akershus);
        TopographicPlace baerum = createMunicipalityWithCountyRef("Bærum", akershus);

        createStopPlaceWithMunicipalityRef("Trollstua", asker);
        createStopPlaceWithMunicipalityRef("Haslum", baerum);

        String graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace:" + GraphQLNames.STOPPLACE_SEARCH +
                " (countyReference:"+akershus.getId()+") {" +
                "id " +
                "name { value } " +
                "}" +
                "}\",\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace.name.value", hasItems("Trollstua", "Haslum"));
    }

    @Test
    public void searchForStopById() throws Exception {

        StopPlace stopPlace = createStopPlace("Espa");
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace:" + GraphQLNames.FIND_STOPPPLACE_BY_ID+
                " (id:"+stopPlace.getId()+") {" +
                "id " +
                "name { value } " +
                "}" +
                "}\",\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].name.value", equalTo(stopPlace.getName().getValue()));
    }

    /**
     * https://rutebanken.atlassian.net/browse/NRP-677
     */
    @Test
    @Ignore
    public void createStopPlaceShouldExposeQuayIds() {
        StopPlaceDto stopPlaceDto = new StopPlaceDto();
        stopPlaceDto.quays = new ArrayList<>(1);
        stopPlaceDto.quays.add(new QuayDto());


        given()
                .contentType(ContentType.JSON)
                .body(stopPlaceDto)
                .when()
                .post(BASE_URI_GRAPHQL)
                .then()
                .log().body()
                .assertThat()
                .body("quays[0].id", notNullValue());
    }

    /**
     * https://rutebanken.atlassian.net/browse/NRP-677
     */
    @Test
    @Ignore
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
                .post(BASE_URI_GRAPHQL)
                .as(StopPlaceDto.class);

        // Add new quay
        QuayDto anotherQuayDto = new QuayDto();
        anotherQuayDto.name = "quay 2";
        stopPlaceDto.quays.add(anotherQuayDto);

        // Update
        stopPlaceDto = given()
                .contentType(ContentType.JSON)
                .body(stopPlaceDto)
                .post(BASE_URI_GRAPHQL + stopPlaceDto.id)
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
            TopographicPlaceRefStructure municipalityRef = new TopographicPlaceRefStructure();
            municipalityRef.setRef(municipality.getId().toString());
            stopPlace.setTopographicPlaceRef(municipalityRef);
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
            TopographicPlaceRefStructure countyRef = new TopographicPlaceRefStructure();
            countyRef.setRef(county.getId().toString());
            municipality.setParentTopographicPlaceRef(countyRef);
        }
        topographicPlaceRepository.save(municipality);
        return municipality;
    }


}

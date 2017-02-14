package org.rutebanken.tiamat.rest.graphql;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.ValidatableResponse;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.rutebanken.tiamat.CommonSpringBootTest;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashSet;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class GraphQLResourceIntegrationTest extends CommonSpringBootTest {
    private static final String BASE_URI_GRAPHQL = "/jersey/graphql/";

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

    @Before
    public void clearRepositories() {
        stopPlaceRepository.deleteAll();
        topographicPlaceRepository.deleteAll();
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

        String graphQlJsonQuery = "{" +
                "\"query\":\"" +
                "{ stopPlace:" + GraphQLNames.FIND_STOPPLACE + " (id:\\\"" + getNetexId(stopPlace) + "\\\") {" +
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
                .body("data.stopPlace[0].quays.id", hasItems(getNetexId(quay), getNetexId(secondQuay)));
    }

    @Test
    public void searchForStopPlaceByOriginalId() throws Exception {
        String stopPlaceName = "Eselstua";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        stopPlaceRepository.save(stopPlace);

        String originalId = "RUT:Stop:1234";
        org.rutebanken.tiamat.model.Value value = new org.rutebanken.tiamat.model.Value(originalId);
        stopPlace.getKeyValues().put(NetexIdMapper.ORIGINAL_ID_KEY, value);
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace: " + GraphQLNames.FIND_STOPPLACE +
                " (" + GraphQLNames.IMPORTED_ID_QUERY + ":\\\"" + originalId + "\\\")" +
                " { " +
                "  id " +
                "  name { value } " +
                " }" +
                "}\",\"variables\":\"\"}";


        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].id", equalTo(getNetexId(stopPlace)))
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName));
    }

    private String getNetexId(EntityStructure entity) {
        return NetexIdMapper.getNetexId(entity, entity.getId());
    }

    @Test
    public void searchForStopPlaceNoParams() throws Exception {
        String stopPlaceName = "Eselstua";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace: " + GraphQLNames.FIND_STOPPLACE +
                " { " +
                "  name { value } " +
                " } " +
                "}\",\"variables\":\"\"}";


        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName));
    }

    @Test
    public void searchForStopPlaceAllEmptyParams() throws Exception {
        String stopPlaceName = "Eselstua";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace: " + GraphQLNames.FIND_STOPPLACE +
                "(id:\\\"\\\" countyReference:\\\"\\\" municipalityReference:\\\"\\\") " +
                " { " +
                "  name { value } " +
                " } " +
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
                "  stopPlace: " + GraphQLNames.FIND_STOPPLACE + " (query:\\\"ytNES\\\") { " +
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
                "  stopPlace: " + GraphQLNames.FIND_STOPPLACE +  " (stopPlaceType:" + StopTypeEnumeration.FERRY_STOP.value() + ") { " +
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
                "  stopPlace:" + GraphQLNames.FIND_STOPPLACE +
                " (stopPlaceType:" + StopTypeEnumeration.TRAM_STATION.value() + " countyReference:\\\"" + getNetexId(hordaland) + "\\\" municipalityReference:\\\"" + getNetexId(kvinnherad) +"\\\") { " +
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
                "  stopPlace:" + GraphQLNames.FIND_STOPPLACE +
                " (municipalityReference:\\\"" + getNetexId(asker) +"\\\") { " +
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
                "  stopPlace:" + GraphQLNames.FIND_STOPPLACE +
                " (municipalityReference:\\\"" + getNetexId(asker) +"\\\") { " +
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
                "\"query\":\"{stopPlace:" + GraphQLNames.FIND_STOPPLACE +
                " (municipalityReference:[\\\""+getNetexId(baerum)+"\\\",\\\""+getNetexId(asker)+"\\\"]) {" +
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
                "\"query\":\"{stopPlace:" + GraphQLNames.FIND_STOPPLACE +
                " (countyReference:[\\\""+getNetexId(akershus)+"\\\",\\\""+getNetexId(buskerud)+"\\\"] municipalityReference:[\\\""+getNetexId(lier)+"\\\",\\\""+getNetexId(asker)+"\\\"]) {" +
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
                "\"query\":\"{stopPlace:" + GraphQLNames.FIND_STOPPLACE +
                " (countyReference:\\\""+getNetexId(akershus)+"\\\") {" +
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
                "\"query\":\"{stopPlace:" + GraphQLNames.FIND_STOPPLACE+
                " (id:\\\""+ getNetexId(stopPlace) +"\\\") {" +
                "id " +
                "name { value } " +
                "}" +
                "}\",\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].name.value", equalTo(stopPlace.getName().getValue()));
    }


    @Test
    public void testSimpleMutationCreateStopPlace() throws Exception {

        String name = "Testing name";
        String shortName = "Testing shortname";
        String description = "Testing description";

        Float lon = new Float(10.11111);
        Float lat = new Float(59.11111);

        Boolean allAreasWheelchairAccessible = Boolean.TRUE;

        String graphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  stopPlace: " + GraphQLNames.MUTATE_STOPPLACE + " (StopPlace: {" +
                "          name: { value:\\\"" + name + "\\\" } " +
                "          shortName:{ value:\\\"" + shortName + "\\\" } " +
                "          description:{ value:\\\"" + description + "\\\" }" +
                "          stopPlaceType:" + StopTypeEnumeration.TRAM_STATION.value() +
                "          location: {" +
                "            longitude:" + lon +
                "            latitude:" + lat +
                "          }" +
                "          allAreasWheelchairAccessible:" + allAreasWheelchairAccessible +
                "       }) { " +
                "  id " +
                "  name { value } " +
                "  shortName { value } " +
                "  description { value } " +
                "  stopPlaceType " +
                "  allAreasWheelchairAccessible " +
                "  location { longitude latitude } " +
                "  } " +
                "}\",\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].id", notNullValue())
                .body("data.stopPlace[0].name.value", equalTo(name))
                .body("data.stopPlace[0].shortName.value", equalTo(shortName))
                .body("data.stopPlace[0].description.value", equalTo(description))
                .body("data.stopPlace[0].stopPlaceType", equalTo(StopTypeEnumeration.TRAM_STATION.value()))
                .body("data.stopPlace[0].location.longitude", comparesEqualTo(lon))
                .body("data.stopPlace[0].location.latitude", comparesEqualTo(lat))
                .body("data.stopPlace[0].allAreasWheelchairAccessible", equalTo(allAreasWheelchairAccessible));


    }

    @Test
    public void testSimpleMutationUpdateStopPlace() throws Exception {

        StopPlace stopPlace = createStopPlace("Espa");
        stopPlace.setShortName(new EmbeddableMultilingualString("E"));
        stopPlace.setDescription(new EmbeddableMultilingualString("E6s beste boller"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));
        stopPlace.setAllAreasWheelchairAccessible(false);

        stopPlaceRepository.save(stopPlace);

        String updatedName = "Testing name ";
        String updatedShortName = "Testing shortname ";
        String updatedDescription = "Testing description ";

        Float updatedLon = new Float(10.11111);
        Float updatedLat = new Float(59.11111);

        Boolean allAreasWheelchairAccessible = Boolean.TRUE;

        String graphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  stopPlace: " + GraphQLNames.MUTATE_STOPPLACE + " (StopPlace: {" +
                "          id:\\\"" + getNetexId(stopPlace) + "\\\"" +
                "          name: { value:\\\"" + updatedName + "\\\" } " +
                "          shortName:{ value:\\\"" + updatedShortName + "\\\" } " +
                "          description:{ value:\\\"" + updatedDescription + "\\\" }" +
                "          stopPlaceType:" + StopTypeEnumeration.TRAM_STATION.value() +
                "          location: {" +
                "            longitude:" + updatedLon +
                "            latitude:" + updatedLat +
                "          }" +
                "          allAreasWheelchairAccessible:" + allAreasWheelchairAccessible +
                "       }) { " +
                "  id " +
                "  name { value } " +
                "  shortName { value } " +
                "  description { value } " +
                "  stopPlaceType " +
                "  allAreasWheelchairAccessible " +
                "  location { longitude latitude } " +
                "  } " +
                "}\",\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].name.value", equalTo(updatedName))
                .body("data.stopPlace[0].shortName.value", equalTo(updatedShortName))
                .body("data.stopPlace[0].description.value", equalTo(updatedDescription))
                .body("data.stopPlace[0].stopPlaceType", equalTo(StopTypeEnumeration.TRAM_STATION.value()))
                .body("data.stopPlace[0].location.longitude", comparesEqualTo(updatedLon))
                .body("data.stopPlace[0].location.latitude", comparesEqualTo(updatedLat))
                .body("data.stopPlace[0].allAreasWheelchairAccessible", equalTo(allAreasWheelchairAccessible));
    }


    @Test
    public void testSimpleMutationCreateQuay() throws Exception {

        StopPlace stopPlace = createStopPlace("Espa");

        stopPlaceRepository.save(stopPlace);

        String name = "Testing name ";
        String shortName = "Testing shortname ";
        String description = "Testing description ";

        Float lon = new Float(10.11111);
        Float lat = new Float(59.11111);


        String graphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  stopPlace: " + GraphQLNames.MUTATE_STOPPLACE + " (StopPlace: {" +
                "          id:\\\"" + getNetexId(stopPlace) + "\\\"" +
                "          quays: [{ " +
                "            name: { value:\\\"" + name + "\\\" } " +
                "            shortName:{ value:\\\"" + shortName + "\\\" } " +
                "            description:{ value:\\\"" + description + "\\\" }" +
                "            location: {" +
                "              longitude:" + lon +
                "              latitude:" + lat +
                "             }" +
                "          }] " +
                "       }) { " +
                "  id " +
                "  name { value } " +
                "  quays {" +
                "    id " +
                "    name { value } " +
                "    shortName { value } " +
                "    description { value } " +
                "    location { longitude latitude } " +
                "  } " +
                "  } " +
                "}\",\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].id", notNullValue())
                .body("data.stopPlace[0].name.value", equalTo(stopPlace.getName().getValue()))
                .body("data.stopPlace[0].quays[0].id", notNullValue())
                .body("data.stopPlace[0].quays[0].name.value", equalTo(name))
                .body("data.stopPlace[0].quays[0].shortName.value", equalTo(shortName))
                .body("data.stopPlace[0].quays[0].description.value", equalTo(description))
                .body("data.stopPlace[0].quays[0].location.longitude", comparesEqualTo(lon))
                .body("data.stopPlace[0].quays[0].location.latitude", comparesEqualTo(lat));
    }

    @Test
    public void testSimpleMutationUpdateQuay() throws Exception {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Espa"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));

        Quay quay = new Quay();
        quay.setCompassBearing(new Float(90));
        quay.setCentroid(geometryFactory.createPoint(new Coordinate(11.2, 60.2)));
        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);

        String name = "Testing name ";
        String shortName = "Testing shortname ";
        String description = "Testing description ";

        Float lon = new Float(10.11111);
        Float lat = new Float(59.11111);

        Float compassBearing = new Float(180);

        String graphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  stopPlace: " + GraphQLNames.MUTATE_STOPPLACE + " (StopPlace: {" +
                "          id:\\\"" + getNetexId(stopPlace) + "\\\"" +
                "          quays: [{ " +
                "            id:\\\"" + getNetexId(quay) + "\\\" " +
                "            name: { value:\\\"" + name + "\\\" } " +
                "            shortName:{ value:\\\"" + shortName + "\\\" } " +
                "            description:{ value:\\\"" + description + "\\\" }" +
                "            location: {" +
                "              longitude:" + lon +
                "              latitude:" + lat +
                "             }" +
                "            compassBearing:" + compassBearing +
                "          }] " +
                "       }) { " +
                "  id " +
                "  name { value } " +
                "  quays {" +
                "    id " +
                "    name { value } " +
                "    shortName { value } " +
                "    description { value } " +
                "    location { longitude latitude } " +
                "    compassBearing " +
                "  } " +
                "  } " +
                "}\",\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].id", comparesEqualTo(getNetexId(stopPlace)))
                .body("data.stopPlace[0].name.value", equalTo(stopPlace.getName().getValue()))
                .body("data.stopPlace[0].quays[0].id", comparesEqualTo(getNetexId(quay)))
                .body("data.stopPlace[0].quays[0].name.value", equalTo(name))
                .body("data.stopPlace[0].quays[0].shortName.value", equalTo(shortName))
                .body("data.stopPlace[0].quays[0].description.value", equalTo(description))
                .body("data.stopPlace[0].quays[0].location.longitude", comparesEqualTo(lon))
                .body("data.stopPlace[0].quays[0].location.latitude", comparesEqualTo(lat))
                .body("data.stopPlace[0].quays[0].compassBearing", comparesEqualTo(compassBearing));
    }


    @Test
    public void testSimpleMutationAddSecondQuay() throws Exception {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Espa"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));

        Quay quay = new Quay();
        quay.setCompassBearing(new Float(90));
        Point point = geometryFactory.createPoint(new Coordinate(11.2, 60.2));
        quay.setCentroid(point);
        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);

        String name = "Testing name ";
        String shortName = "Testing shortname ";
        String description = "Testing description ";

        Float lon = new Float(10.11111);
        Float lat = new Float(59.11111);

        Float compassBearing = new Float(180);

        String graphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  stopPlace: " + GraphQLNames.MUTATE_STOPPLACE + " (StopPlace: {" +
                "          id:\\\"" + getNetexId(stopPlace) + "\\\"" +
                "          quays: [{ " +
                "            name: { value:\\\"" + name + "\\\" } " +
                "            shortName:{ value:\\\"" + shortName + "\\\" } " +
                "            description:{ value:\\\"" + description + "\\\" }" +
                "            location: {" +
                "              longitude:" + lon +
                "              latitude:" + lat +
                "             }" +
                "            compassBearing:" + compassBearing +
                "          }] " +
                "       }) { " +
                "  id " +
                "  name { value } " +
                "  quays {" +
                "    id " +
                "    name { value } " +
                "    shortName { value } " +
                "    description { value } " +
                "    location { longitude latitude } " +
                "    compassBearing " +
                "  } " +
                "  } " +
                "}}\",\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].id", comparesEqualTo(getNetexId(stopPlace)))
                .body("data.stopPlace[0].name.value", equalTo(stopPlace.getName().getValue()))
                .body("data.stopPlace[0].quays", Matchers.hasSize(2))
                // First Quay - added manually
                .body("data.stopPlace[0].quays[0].id", comparesEqualTo(getNetexId(quay)))
                .body("data.stopPlace[0].quays[0].name.", nullValue())
                .body("data.stopPlace[0].quays[0].shortName", nullValue())
                .body("data.stopPlace[0].quays[0].description", nullValue())
                .body("data.stopPlace[0].quays[0].location.longitude", comparesEqualTo(new Float(point.getX())))
                .body("data.stopPlace[0].quays[0].location.latitude", comparesEqualTo(new Float(point.getY())))
                .body("data.stopPlace[0].quays[0].compassBearing", comparesEqualTo(quay.getCompassBearing()))
                // Second Quay - added using GraphQL
                .body("data.stopPlace[0].quays[1].id", not(getNetexId(quay)))
                .body("data.stopPlace[0].quays[1].id", not(getNetexId(stopPlace)))
                .body("data.stopPlace[0].quays[1].name.value", equalTo(name))
                .body("data.stopPlace[0].quays[1].shortName.value", equalTo(shortName))
                .body("data.stopPlace[0].quays[1].description.value", equalTo(description))
                .body("data.stopPlace[0].quays[1].location.longitude", comparesEqualTo(lon))
                .body("data.stopPlace[0].quays[1].location.latitude", comparesEqualTo(lat))
                .body("data.stopPlace[0].quays[1].compassBearing", comparesEqualTo(compassBearing));
    }



    @Test
    public void testMutationUpdateStopPlaceCreateQuayAndUpdateQuay() throws Exception {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Espa"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));

        Quay quay = new Quay();
        quay.setCompassBearing(new Float(90));
        Point point = geometryFactory.createPoint(new Coordinate(11.2, 60.2));
        quay.setCentroid(point);
        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);

        String newStopName = "Shell - E6";
        String newQuaydName = "Testing name 1";
        String newQuayShortName = "Testing shortname 1";
        String newQuayDescription = "Testing description 1";

        String updatedName = "Testing name 2";
        String updatedShortName = "Testing shortname 2";
        String updatedDescription = "Testing description 2";

        Float lon = new Float(10.11111);
        Float lat = new Float(59.11111);

        Float compassBearing = new Float(180);

        String graphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  stopPlace: " + GraphQLNames.MUTATE_STOPPLACE + " (StopPlace: {" +
                "          id:\\\"" + getNetexId(stopPlace) + "\\\"" +
                "          name: { value:\\\"" + newStopName + "\\\" } " +
                "          quays: [{ " +
                "            name: { value:\\\"" + newQuaydName + "\\\" } " +
                "            shortName:{ value:\\\"" + newQuayShortName + "\\\" } " +
                "            description:{ value:\\\"" + newQuayDescription + "\\\" }" +
                "            location: {" +
                "              longitude:" + lon +
                "              latitude:" + lat +
                "             }" +
                "            compassBearing:" + compassBearing +
                "          } , {" +
                "            id:\\\"" + getNetexId(quay) + "\\\" " +
                "            name: { value:\\\"" + updatedName + "\\\" } " +
                "            shortName:{ value:\\\"" + updatedShortName + "\\\" } " +
                "            description:{ value:\\\"" + updatedDescription + "\\\" }" +
                "          }] " +
                "       }) { " +
                "  id " +
                "  name { value } " +
                "  quays {" +
                "    id " +
                "    name { value } " +
                "    shortName { value } " +
                "    description { value } " +
                "    location { longitude latitude } " +
                "    compassBearing " +
                "  } " +
                "  } " +
                "}}\",\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].id", comparesEqualTo(getNetexId(stopPlace)))
                .body("data.stopPlace[0].name.value", equalTo(newStopName))
                .body("data.stopPlace[0].quays", Matchers.hasSize(2))
                        // First Quay - added manually, then updated
                .body("data.stopPlace[0].quays[0].id", comparesEqualTo(getNetexId(quay)))
                .body("data.stopPlace[0].quays[0].name.value", equalTo(updatedName))
                .body("data.stopPlace[0].quays[0].shortName.value", equalTo(updatedShortName))
                .body("data.stopPlace[0].quays[0].description.value", equalTo(updatedDescription))
                .body("data.stopPlace[0].quays[0].location.longitude", comparesEqualTo(new Float(point.getX())))
                .body("data.stopPlace[0].quays[0].location.latitude", comparesEqualTo(new Float(point.getY())))
                .body("data.stopPlace[0].quays[0].compassBearing", comparesEqualTo(quay.getCompassBearing()))
                        // Second Quay - added using GraphQL
                .body("data.stopPlace[0].quays[1].id", not(getNetexId(quay)))
                .body("data.stopPlace[0].quays[1].id", not(getNetexId(stopPlace)))
                .body("data.stopPlace[0].quays[1].name.value", equalTo(newQuaydName))
                .body("data.stopPlace[0].quays[1].shortName.value", equalTo(newQuayShortName))
                .body("data.stopPlace[0].quays[1].description.value", equalTo(newQuayDescription))
                .body("data.stopPlace[0].quays[1].location.longitude", comparesEqualTo(lon))
                .body("data.stopPlace[0].quays[1].location.latitude", comparesEqualTo(lat))
                .body("data.stopPlace[0].quays[1].compassBearing", comparesEqualTo(compassBearing));
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

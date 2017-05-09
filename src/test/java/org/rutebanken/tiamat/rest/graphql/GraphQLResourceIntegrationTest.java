package org.rutebanken.tiamat.rest.graphql;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;

import java.math.BigInteger;
import java.util.HashSet;

import static org.hamcrest.Matchers.*;

public class GraphQLResourceIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

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
                "{ stopPlace:" + GraphQLNames.FIND_STOPPLACE + " (id:\\\"" + stopPlace.getNetexId() + "\\\") {" +
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
                .body("data.stopPlace[0].quays.id", hasItems(quay.getNetexId(), secondQuay.getNetexId()));
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
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName));
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
                " (stopPlaceType:" + StopTypeEnumeration.TRAM_STATION.value() + " countyReference:\\\"" + hordaland.getNetexId() + "\\\" municipalityReference:\\\"" + kvinnherad.getNetexId() +"\\\") { " +
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
                " (municipalityReference:\\\"" + asker.getNetexId() +"\\\") { " +
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
                " (municipalityReference:\\\"" + asker.getNetexId() +"\\\") { " +
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
                " (municipalityReference:[\\\""+baerum.getNetexId()+"\\\",\\\""+asker.getNetexId()+"\\\"]) {" +
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
                " (countyReference:[\\\""+akershus.getNetexId()+"\\\",\\\""+buskerud.getNetexId()+"\\\"] municipalityReference:[\\\""+lier.getNetexId()+"\\\",\\\""+asker.getNetexId()+"\\\"]) {" +
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
                " (countyReference:\\\""+akershus.getNetexId()+"\\\") {" +
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
                " (id:\\\""+ stopPlace.getNetexId() +"\\\") {" +
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

        String graphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  stopPlace: " + GraphQLNames.MUTATE_STOPPLACE + " (StopPlace: {" +
                "          name: { value:\\\"" + name + "\\\" } " +
                "          shortName:{ value:\\\"" + shortName + "\\\" } " +
                "          description:{ value:\\\"" + description + "\\\" }" +
                "          stopPlaceType:" + StopTypeEnumeration.TRAM_STATION.value() +
                "          geometry: {" +
                "            type: Point" +
                "            coordinates: [[" + lon + "," + lat + "]] " +
                "          }" +
                "       }) { " +
                "  id " +
                "  name { value } " +
                "  shortName { value } " +
                "  description { value } " +
                "  stopPlaceType " +
                "  geometry { type coordinates } " +
                "  } " +
                "}\",\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .root("data.stopPlace[0]")
                    .body("id", notNullValue())
                    .body("name.value", equalTo(name))
                    .body("shortName.value", equalTo(shortName))
                    .body("description.value", equalTo(description))
                    .body("stopPlaceType", equalTo(StopTypeEnumeration.TRAM_STATION.value()))
                    .body("geometry.type", equalTo("Point"))
                    .body("geometry.coordinates[0][0]", comparesEqualTo(lon))
                    .body("geometry.coordinates[0][1]", comparesEqualTo(lat));
    }

    @Test
    public void testSimpleMutationUpdateStopPlace() throws Exception {
        TopographicPlace parentTopographicPlace = new TopographicPlace(new EmbeddableMultilingualString("countyforinstance"));
        parentTopographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);

        topographicPlaceRepository.save(parentTopographicPlace);
        TopographicPlace topographicPlace = createMunicipalityWithCountyRef("somewhere in space", parentTopographicPlace);

        StopPlace stopPlace = createStopPlace("Espa");
        stopPlace.setShortName(new EmbeddableMultilingualString("E"));
        stopPlace.setDescription(new EmbeddableMultilingualString("E6s beste boller"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));
        stopPlace.setAllAreasWheelchairAccessible(false);
        stopPlace.setTopographicPlace(topographicPlace);
        stopPlace.setWeighting(InterchangeWeightingEnumeration.NO_INTERCHANGE);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String updatedName = "Testing name ";
        String updatedShortName = "Testing shortname ";
        String updatedDescription = "Testing description ";
//        String fromDate = "2012-04-23T18:25:43.511+0200";
//        String toDate = "2018-04-23T18:25:43.511+0200";

        Float updatedLon = new Float(10.11111);
        Float updatedLat = new Float(59.11111);

        String versionComment = "Stop place moved 100 meters";

        InterchangeWeightingEnumeration weighting = InterchangeWeightingEnumeration.INTERCHANGE_ALLOWED;

        String graphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  stopPlace: " + GraphQLNames.MUTATE_STOPPLACE + " (StopPlace: {" +
                "          id:\\\"" + stopPlace.getNetexId() + "\\\"" +
                "          name: { value:\\\"" + updatedName + "\\\" } " +
                "          shortName:{ value:\\\"" + updatedShortName + "\\\" } " +
                "          description:{ value:\\\"" + updatedDescription + "\\\" }" +
                "          stopPlaceType:" + StopTypeEnumeration.TRAM_STATION.value() +
                "          versionComment: \\\""+ versionComment + "\\\"" +
                "          geometry: {" +
                "            type: Point" +
                "            coordinates: [[" + updatedLon + "," + updatedLat + "]] " +
                "          }" +
                "          weighting:" + weighting.value() +
//                "          validBetweens: [{fromDate: \\\"" + fromDate + "\\\", toDate: \\\"" + toDate + "\\\"}]" +
                "       }) { " +
                "  id " +
                "  name { value } " +
                "  shortName { value } " +
                "  description { value } " +
                "  stopPlaceType " +
                "  versionComment " +
                "  topographicPlace { id topographicPlaceType parentTopographicPlace { id topographicPlaceType }} " +
                "  weighting " +
                "  geometry { type coordinates } " +
                "  validBetweens { fromDate toDate } " +
                "  } " +
                "}\",\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .root("data.stopPlace[0]")
                    .body("name.value", equalTo(updatedName))
                    .body("shortName.value", equalTo(updatedShortName))
                    .body("description.value", equalTo(updatedDescription))
                    .body("stopPlaceType", equalTo(StopTypeEnumeration.TRAM_STATION.value()))
                    .body("versionComment", equalTo(versionComment))
                    .body("geometry.type", equalTo("Point"))
                    .body("geometry.coordinates[0][0]", comparesEqualTo(updatedLon))
                    .body("geometry.coordinates[0][1]", comparesEqualTo(updatedLat))
                    .body("weighting", equalTo(weighting.value()))
                    .body("topographicPlace.id", notNullValue())
                    .body("topographicPlace.topographicPlaceType", equalTo(TopographicPlaceTypeEnumeration.TOWN.value()))
                    .body("topographicPlace.parentTopographicPlace", notNullValue())
                    .body("topographicPlace.parentTopographicPlace.id", notNullValue())
                    .body("topographicPlace.parentTopographicPlace.topographicPlaceType", equalTo(TopographicPlaceTypeEnumeration.COUNTY.value()));
//                    .body("validBetweens[0].fromDate", comparesEqualTo(fromDate))
//                    .body("validBetweens[0].toDate", comparesEqualTo(toDate));
    }


    @Test
    public void testSimpleMutationCreateQuay() throws Exception {

        StopPlace stopPlace = createStopPlace("Espa");

        stopPlaceRepository.save(stopPlace);

        String name = "Testing name ";
        String shortName = "Testing shortname ";
        String description = "Testing description ";
        String publicCode = "publicCode 2";

        Float lon = new Float(10.11111);
        Float lat = new Float(59.11111);


        String graphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  stopPlace: " + GraphQLNames.MUTATE_STOPPLACE + " (StopPlace: {" +
                "          id:\\\"" + stopPlace.getNetexId() + "\\\"" +
                "          quays: [{ " +
                "            name: { value:\\\"" + name + "\\\" } " +
                "            shortName:{ value:\\\"" + shortName + "\\\" } " +
                "            description:{ value:\\\"" + description + "\\\" }" +
                "            publicCode:\\\"" + publicCode + "\\\"" +
                "            geometry: {" +
                "              type: Point" +
                "              coordinates: [[" + lon + "," + lat + "]] " +
                "            }" +
                "            }] " +
                "       }) { " +
                "  id " +
                "  name { value } " +
                "  quays {" +
                "    id " +
                "    publicCode " +
                "    name { value } " +
                "    shortName { value } " +
                "    description { value } " +
                "    geometry { type coordinates } " +
                "  } " +
                "  } " +
                "}\",\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].id", notNullValue())
                .body("data.stopPlace[0].name.value", equalTo(stopPlace.getName().getValue()))
                .root("data.stopPlace[0].quays[0]")
                    .body("id", notNullValue())
                    .body("name.value", equalTo(name))
                    .body("shortName.value", equalTo(shortName))
                    .body("description.value", equalTo(description))
                    .body("publicCode", equalTo(publicCode))
                    .body("geometry.type", equalTo("Point"))
                    .body("geometry.coordinates[0][0]", comparesEqualTo(lon))
                    .body("geometry.coordinates[0][1]", comparesEqualTo(lat));
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
                "          id:\\\"" + stopPlace.getNetexId() + "\\\"" +
                "          quays: [{ " +
                "            id:\\\"" + quay.getNetexId() + "\\\" " +
                "            name: { value:\\\"" + name + "\\\" } " +
                "            shortName:{ value:\\\"" + shortName + "\\\" } " +
                "            description:{ value:\\\"" + description + "\\\" }" +
                "          geometry: {" +
                "            type: Point" +
                "            coordinates: [[" + lon + "," + lat + "]] " +
                "          }" +
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
                "    geometry { type coordinates } " +
                "    compassBearing " +
                "  } " +
                "  } " +
                "}\",\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].id", comparesEqualTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlace.getName().getValue()))
                .root("data.stopPlace[0].quays[0]")
                    .body("id", comparesEqualTo(quay.getNetexId()))
                    .body("name.value", equalTo(name))
                    .body("shortName.value", equalTo(shortName))
                    .body("description.value", equalTo(description))
                    .body("geometry.type", equalTo("Point"))
                    .body("geometry.coordinates[0][0]", comparesEqualTo(lon))
                    .body("geometry.coordinates[0][1]", comparesEqualTo(lat))
                    .body("compassBearing", comparesEqualTo(compassBearing));
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
                "          id:\\\"" + stopPlace.getNetexId() + "\\\"" +
                "          quays: [{ " +
                "            name: { value:\\\"" + name + "\\\" } " +
                "            shortName:{ value:\\\"" + shortName + "\\\" } " +
                "            description:{ value:\\\"" + description + "\\\" }" +
                "            geometry: {" +
                "              type: Point" +
                "              coordinates: [[" + lon + "," + lat + "]] " +
                "            }" +
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
                "    geometry { type coordinates } " +
                "    compassBearing " +
                "  } " +
                "  } " +
                "}}\",\"variables\":\"\"}";

        String manuallyAddedQuayId = quay.getNetexId();


        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].id", comparesEqualTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlace.getName().getValue()))
                .body("data.stopPlace[0].quays", hasSize(2))
                        // First Quay - added manually
                .root("data.stopPlace[0].quays.find { it.id == '" + manuallyAddedQuayId + "'}")
                    .body("name", nullValue())
                    .body("shortName", nullValue())
                    .body("description", nullValue())
                    .body("geometry.type", equalTo(point.getGeometryType()))
                    .body("geometry.coordinates[0][0]", comparesEqualTo(new Float(point.getX())))
                    .body("geometry.coordinates[0][1]", comparesEqualTo(new Float(point.getY())))
                    .body("compassBearing", comparesEqualTo(quay.getCompassBearing()))
                        // Second Quay - added using GraphQL
                .root("data.stopPlace[0].quays.find { it.id != '" + manuallyAddedQuayId + "'}")
                    .body("name.value", equalTo(name))
                    .body("shortName.value", equalTo(shortName))
                    .body("description.value", equalTo(description))
                    .body("geometry.type", equalTo("Point"))
                    .body("geometry.coordinates[0][0]", comparesEqualTo(lon))
                    .body("geometry.coordinates[0][1]", comparesEqualTo(lat))
                    .body("compassBearing", comparesEqualTo(compassBearing));
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
                "          id:\\\"" + stopPlace.getNetexId() + "\\\"" +
                "          name: { value:\\\"" + newStopName + "\\\" } " +
                "          quays: [{ " +
                "            name: { value:\\\"" + newQuaydName + "\\\" } " +
                "            shortName:{ value:\\\"" + newQuayShortName + "\\\" } " +
                "            description:{ value:\\\"" + newQuayDescription + "\\\" }" +
                "            geometry: {" +
                "              type: Point" +
                "              coordinates: [[" + lon + ","+ lat + "]]" +
                "             }" +
                "            compassBearing:" + compassBearing +
                "          } , {" +
                "            id:\\\"" + quay.getNetexId() + "\\\" " +
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
                "    geometry { type coordinates } " +
                "    compassBearing " +
                "  } " +
                "  } " +
                "}}\",\"variables\":\"\"}";

        String manuallyAddedQuayId = quay.getNetexId();


        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].id", comparesEqualTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(newStopName))
                .body("data.stopPlace[0].quays", hasSize(2))
                        // First Quay - added manually, then updated
                .root("data.stopPlace[0].quays.find { it.id == '" + manuallyAddedQuayId + "'}")
                    .body("name.value", equalTo(updatedName))
                    .body("shortName.value", equalTo(updatedShortName))
                    .body("description.value", equalTo(updatedDescription))
                    .body("geometry.type", equalTo(point.getGeometryType()))
                    .body("geometry.coordinates[0][0]", comparesEqualTo(new Float(point.getX())))
                    .body("geometry.coordinates[0][1]", comparesEqualTo(new Float(point.getY())))
                    .body("compassBearing", comparesEqualTo(quay.getCompassBearing()))

                        // Second Quay - added using GraphQL
                .root("data.stopPlace[0].quays.find { it.id != '" + manuallyAddedQuayId + "'}")
                    .body("id", not(stopPlace.getNetexId()))
                    .body("name.value", equalTo(newQuaydName))
                    .body("shortName.value", equalTo(newQuayShortName))
                    .body("description.value", equalTo(newQuayDescription))
                    .body("geometry.type", equalTo(point.getGeometryType()))
                    .body("geometry.coordinates[0][0]", comparesEqualTo(lon))
                    .body("geometry.coordinates[0][1]", comparesEqualTo(lat))
                    .body("compassBearing", comparesEqualTo(compassBearing));
    }

    /**
     * Test that reproduces NRP-1433
     *
     * @throws Exception
     */
    @Test
    public void testSimpleMutationUpdateStopPlaceKeepPlaceEquipmentsOnQuay() throws Exception {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Espa"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));
        stopPlace.setPlaceEquipments(createPlaceEquipments());

        Quay quay = new Quay();
        quay.setCompassBearing(new Float(90));
        quay.setCentroid(geometryFactory.createPoint(new Coordinate(11.2, 60.2)));
        quay.setPlaceEquipments(createPlaceEquipments());
        stopPlace.getQuays().add(quay);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String name = "Testing name ";
        String netexId = stopPlace.getNetexId();

        //Verify that placeEquipments have been set
        String graphQlStopPlaceQuery = "{" +
                "\"query\":\"{stopPlace:" + GraphQLNames.FIND_STOPPLACE + " (id:\\\"" + netexId + "\\\") { " +
                "    id" +
                "      placeEquipments {" +
                "        waitingRoomEquipment { id }" +
                "        sanitaryEquipment { id }" +
                "        ticketingEquipment { id }" +
                "        cycleStorageEquipment { id }" +
                "        shelterEquipment { id }" +
                "      }" +
                "    quays {" +
                "      id" +
                "      placeEquipments {" +
                "        waitingRoomEquipment { id }" +
                "        sanitaryEquipment { id }" +
                "        ticketingEquipment { id }" +
                "        cycleStorageEquipment { id }" +
                "        shelterEquipment { id }" +
                "      }" +
                "    }" +
                "  }" +
                "}}\",\"variables\":\"\"}";

        executeGraphQL(graphQlStopPlaceQuery)
                .root("data.stopPlace[0]")
                    .body("id", comparesEqualTo(netexId))
                    .body("placeEquipments", notNullValue())
                    .body("placeEquipments.waitingRoomEquipment", notNullValue())
                    .body("placeEquipments.sanitaryEquipment", notNullValue())
                    .body("placeEquipments.ticketingEquipment", notNullValue())
                    .body("placeEquipments.cycleStorageEquipment", notNullValue())
                    .body("placeEquipments.shelterEquipment", notNullValue())
                .root("data.stopPlace[0].quays[0]")
                    .body("id", notNullValue())
                    .body("placeEquipments", notNullValue())
                    .body("placeEquipments.waitingRoomEquipment", notNullValue())
                    .body("placeEquipments.sanitaryEquipment", notNullValue())
                    .body("placeEquipments.ticketingEquipment", notNullValue())
                    .body("placeEquipments.cycleStorageEquipment", notNullValue())
                    .body("placeEquipments.shelterEquipment", notNullValue())
        ;

        //Update StopPlace name
        String graphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  stopPlace: " + GraphQLNames.MUTATE_STOPPLACE + " (StopPlace: {" +
                "          id:\\\"" + stopPlace.getNetexId() + "\\\"" +
                "          name: { value:\\\"" + name + "\\\" } " +
                "       }) { " +
                "    id " +
                "    name { value } " +
                "      placeEquipments {" +
                "        waitingRoomEquipment { id }" +
                "        sanitaryEquipment { id }" +
                "        ticketingEquipment { id }" +
                "        cycleStorageEquipment { id }" +
                "        shelterEquipment { id }" +
                "      }" +
                "    quays {" +
                "      id" +
                "      placeEquipments {" +
                "        waitingRoomEquipment { id }" +
                "        sanitaryEquipment { id }" +
                "        ticketingEquipment { id }" +
                "        cycleStorageEquipment { id }" +
                "        shelterEquipment { id }" +
                "      }" +
                "    }" +
                "  }" +
                "}}\",\"variables\":\"\"}";

        executeGraphQL(graphQlJsonQuery)
                .root("data.stopPlace[0]")
                    .body("id", comparesEqualTo(netexId))
                    .body("name.value", comparesEqualTo(name))
                    .body("placeEquipments", notNullValue())
                    .body("placeEquipments.waitingRoomEquipment", notNullValue())
                    .body("placeEquipments.sanitaryEquipment", notNullValue())
                    .body("placeEquipments.ticketingEquipment", notNullValue())
                    .body("placeEquipments.cycleStorageEquipment", notNullValue())
                    .body("placeEquipments.shelterEquipment", notNullValue())
                .root("data.stopPlace[0].quays[0]")
                    .body("id", notNullValue())
                    .body("placeEquipments", notNullValue())
                    .body("placeEquipments.waitingRoomEquipment", notNullValue())
                    .body("placeEquipments.sanitaryEquipment", notNullValue())
                    .body("placeEquipments.ticketingEquipment", notNullValue())
                    .body("placeEquipments.cycleStorageEquipment", notNullValue())
                    .body("placeEquipments.shelterEquipment", notNullValue());

    }


    @Test
    public void testSimpleSaveAlternativeNames() throws Exception {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));

        AlternativeName altName = new AlternativeName();
        altName.setNameType(NameTypeEnumeration.ALIAS);
        altName.setName(new EmbeddableMultilingualString("Navn", "no"));

        AlternativeName altName2 = new AlternativeName();
        altName2.setNameType(NameTypeEnumeration.ALIAS);
        altName2.setName(new EmbeddableMultilingualString("Name", "en"));

        stopPlace.getAlternativeNames().add(altName);
        stopPlace.getAlternativeNames().add(altName2);

        Quay quay = new Quay();
        quay.setCompassBearing(new Float(90));
        quay.setCentroid(geometryFactory.createPoint(new Coordinate(11.2, 60.2)));
        quay.getAlternativeNames().add(altName);
        quay.getAlternativeNames().add(altName2);

        stopPlace.getQuays().add(quay);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String name = "Testing name ";
        String netexId = stopPlace.getNetexId();

        //Verify that placeEquipments have been set
        String graphQlStopPlaceQuery = "{" +
                "\"query\":\"{stopPlace:" + GraphQLNames.FIND_STOPPLACE + " (id:\\\"" + netexId + "\\\") { " +
                "    id" +
                "      alternativeNames {" +
                "        nameType" +
                "        name {" +
                "          value" +
                "          lang" +
                "        }" +
                "      }" +
                "    quays {" +
                "      id" +
                "      alternativeNames {" +
                "        nameType" +
                "        name {" +
                "          value" +
                "          lang" +
                "        }" +
                "      }" +
                "    }" +
                "  }" +
                "}}\",\"variables\":\"\"}";

        executeGraphQL(graphQlStopPlaceQuery)
                .root("data.stopPlace[0]")
                    .body("id", comparesEqualTo(netexId))
                    .body("alternativeNames", notNullValue())
                    .body("alternativeNames[0].nameType", notNullValue())
                    .body("alternativeNames[0].name.value", notNullValue())
                    .body("alternativeNames[0].name.lang", notNullValue())
                    .body("alternativeNames[1].nameType", notNullValue())
                    .body("alternativeNames[1].name.value", notNullValue())
                    .body("alternativeNames[1].name.lang", notNullValue())
                .root("data.stopPlace[0].quays[0]")
                    .body("id", comparesEqualTo(quay.getNetexId()))
                    .body("alternativeNames", notNullValue())
                    .body("alternativeNames[0].nameType", notNullValue())
                    .body("alternativeNames[0].name.value", notNullValue())
                    .body("alternativeNames[0].name.lang", notNullValue())
                    .body("alternativeNames[1].nameType", notNullValue())
                    .body("alternativeNames[1].name.value", notNullValue())
                    .body("alternativeNames[1].name.lang", notNullValue())
        ;
    }
    @Test
    public <T extends Comparable<T>> void testSimpleMutateAlternativeNames() throws Exception {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));

        AlternativeName altName = new AlternativeName();
        altName.setNameType(NameTypeEnumeration.ALIAS);
        altName.setName(new EmbeddableMultilingualString("Navn", "no"));

        stopPlace.getAlternativeNames().add(altName);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String netexId = stopPlace.getNetexId();

        String updatedAlternativeNameValue = "UPDATED ALIAS";
        String updatedAlternativeNameLang = "no";

        String graphQlStopPlaceQuery = "{" +
                "\"query\":\"mutation { " +
                "  stopPlace: " + GraphQLNames.MUTATE_STOPPLACE + " (StopPlace: {" +
                "      id:\\\"" + netexId + "\\\"" +
                "      alternativeNames: [" +
                "        {" +
                "          nameType: " + altName.getNameType().value() +
                "          name: {" +
                "            value: \\\"" + updatedAlternativeNameValue + "\\\"" +
                "            lang:\\\""+ updatedAlternativeNameLang +"\\\"" +
                "          }" +
                "        } " +
                "      ]" +
                "    }) " +
                "    {" +
                "      id" +
                "      alternativeNames {" +
                "        nameType" +
                "        name {" +
                "          value" +
                "          lang" +
                "        }" +
                "      }" +
                "    }" +
                "  }" +
                "\",\"variables\":\"\"}";

        executeGraphQL(graphQlStopPlaceQuery)
                .body("data.stopPlace[0].id", comparesEqualTo(netexId))
                .body("data.stopPlace[0].alternativeNames", notNullValue())
                .root("data.stopPlace[0].alternativeNames[0]")
//                .body("nameType", equalTo(altName.getNameType())) //RestAssured apparently does not like comparing response with enums...
                .body("name.value", comparesEqualTo(updatedAlternativeNameValue))
                .body("name.lang", comparesEqualTo(updatedAlternativeNameLang))
        ;
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
        municipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.TOWN);
        if(county != null) {
            municipality.setParentTopographicPlaceRef(new TopographicPlaceRefStructure(county));
        }
        topographicPlaceRepository.save(municipality);
        return municipality;
    }


    private PlaceEquipment createPlaceEquipments() {
        PlaceEquipment equipments = new PlaceEquipment();

        ShelterEquipment leskur = new ShelterEquipment();
        leskur.setEnclosed(false);
        leskur.setSeats(BigInteger.valueOf(2));

        WaitingRoomEquipment venterom = new WaitingRoomEquipment();
        venterom.setSeats(BigInteger.valueOf(25));

        TicketingEquipment billettAutomat = new TicketingEquipment();
        billettAutomat.setTicketMachines(true);
        billettAutomat.setNumberOfMachines(BigInteger.valueOf(2));

        SanitaryEquipment toalett = new SanitaryEquipment();
        toalett.setNumberOfToilets(BigInteger.valueOf(2));

        CycleStorageEquipment sykkelstativ = new CycleStorageEquipment();
        sykkelstativ.setCycleStorageType(CycleStorageEnumeration.RACKS);
        sykkelstativ.setNumberOfSpaces(BigInteger.TEN);

        equipments.getInstalledEquipment().add(venterom);
        equipments.getInstalledEquipment().add(billettAutomat);
        equipments.getInstalledEquipment().add(toalett);
        equipments.getInstalledEquipment().add(leskur);
        equipments.getInstalledEquipment().add(sykkelstativ);
        return equipments;
    }

}

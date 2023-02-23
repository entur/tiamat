package org.rutebanken.tiamat.rest.graphql;

import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.changelog.EntityChangedEvent;
import org.rutebanken.tiamat.changelog.EntityChangedJMSListener;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.service.stopplace.MultiModalStopPlaceEditor;
import org.rutebanken.tiamat.time.ExportTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.operations.MultiModalityOperationsBuilder.INPUT;
import static org.rutebanken.tiamat.rest.graphql.scalars.DateScalar.DATE_TIME_PATTERN;

public class GraphQLResourceStopPlaceIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphQLResourceStopPlaceIntegrationTest.class);

    @Autowired
    private EntityChangedJMSListener entityChangedJMSListener;

    @Autowired
    private ExportTimeZone exportTimeZone;

    private final Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    @Before
    public void cleanReceivedJMS() {
        entityChangedJMSListener.popEvents();
    }

    @Test
    public void retrieveStopPlaceWithTwoQuays() {
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


        String graphQlJsonQuery = """
                {
                    stopPlace:  %s (
                        %s:"%s",
                        allVersions:true
                    )
                    {
                        id
                        name { value }
                        ... on StopPlace {
                            quays {
                                id
                                name { value }
                            }
                        }
                    }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE, QUERY, stopPlace.getNetexId());

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName))
                .body("data.stopPlace[0].quays.name.value", hasItems(firstQuayName, secondQuayName))
                .body("data.stopPlace[0].quays.id", hasItems(quay.getNetexId(), secondQuay.getNetexId()));
    }

    @Test
    public void mutateStopPlaceWithPlaceEquipmentOnQuay() {

        var quay = new Quay();
        var firstQuayName = "quay to add place equipment on";
        quay.setName(new EmbeddableMultilingualString(firstQuayName));

        var stopPlaceName = "StopPlace";
        var stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        stopPlace.setQuays(new HashSet<>());
        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);

        var graphqlQuery = """
                mutation {
                  stopPlace: mutateStopPlace(StopPlace: {id: "%s", quays: [{id: "%s", placeEquipments: {shelterEquipment: [{seats: 3}]}}]}) {
                    id
                    quays {
                      id
                      placeEquipments {
                        shelterEquipment {
                          seats
                        }
                      }
                    }
                  }
                }
                """
                .formatted(stopPlace.getNetexId(), quay.getNetexId());

        executeGraphQLQueryOnly(graphqlQuery)
                .root("data.stopPlace[0].quays[0]")
                .body("placeEquipments", notNullValue())
                .root("data.stopPlace[0].quays[0].placeEquipments.shelterEquipment[0]")
                .body("seats", equalTo(3));
    }

    /**
     * Use explicit parameter for original ID search
     */
    @Test
    public void searchForStopPlaceByOriginalId() {
        String stopPlaceName = "Eselstua";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        stopPlaceRepository.save(stopPlace);

        String originalId = "RUT:Stop:1234";
        Value value = new Value(originalId);
        stopPlace.getKeyValues().put(NetexIdMapper.ORIGINAL_ID_KEY, value);
        stopPlaceRepository.save(stopPlace);


        String graphQlJsonQuery = """
                {
                stopPlace:  %s (%s:"%s", allVersions:true) {
                          id
                          name { value }
                      }
                  }
                  """
                .formatted(GraphQLNames.FIND_STOPPLACE, IMPORTED_ID_QUERY, originalId);

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName));
    }

    /**
     * Use query parameter for original ID search
     */
    @Test
    public void searchForStopPlaceByOriginalIdQuery() {
        String stopPlaceName = "Fleskeberget";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        stopPlaceRepository.save(stopPlace);

        String originalId = "BRA:StopPlace:666";
        Value value = new Value(originalId);
        stopPlace.getKeyValues().put(NetexIdMapper.ORIGINAL_ID_KEY, value);
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                {
                stopPlace: %s(query:"%s", allVersions:true) {
                          id
                          name { value }
                      }
                  }
                  """
                .formatted(GraphQLNames.FIND_STOPPLACE, originalId);

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName));
    }


    /**
     * Use query parameter for original ID search
     */
    @Test
    public void searchForStopPlaceWithoutCoordinates() {
        String basename = "koordinaten";
        String nameWithLocation = basename + " nr 1";
        StopPlace stopPlaceWithCoordinates = new StopPlace(new EmbeddableMultilingualString(nameWithLocation));
        stopPlaceWithCoordinates.setCentroid(geometryFactory.createPoint(new Coordinate(10.533212, 59.678080)));

        String nameWithoutLocation = basename + " nr 2";
        StopPlace stopPlaceWithoutCoordinates = new StopPlace(new EmbeddableMultilingualString(nameWithoutLocation));
        stopPlaceWithoutCoordinates.setCentroid(null);

        stopPlaceRepository.save(stopPlaceWithCoordinates);
        stopPlaceRepository.save(stopPlaceWithoutCoordinates);

        String graphQlJsonQuery = """
                {
                  stopPlace: %s(query:"%s", allVersions:true) {
                          id
                          name { value }
                          geometry {coordinates }
                      }
                }
                  """
                .formatted(GraphQLNames.FIND_STOPPLACE, basename);

        // Search for stopPlace should return both StopPlaces above
        executeGraphQLQueryOnly(graphQlJsonQuery)
                .root("data.stopPlace.find { it.id == '" + stopPlaceWithCoordinates.getNetexId() + "'}")
                .body("name.value", equalTo(nameWithLocation))
                .body("geometry", notNullValue())
                .body("geometry.coordinates", hasSize(1))
                .root("data.stopPlace.find { it.id == '" + stopPlaceWithoutCoordinates.getNetexId() + "'}")
                .body("name.value", equalTo(nameWithoutLocation))
                .body("geometry", nullValue());

        graphQlJsonQuery = """
                {
                   stopPlace: %s (
                      query:"%s",
                      allVersions:true,
                      withoutLocationOnly:true
                   )
                   {
                      id
                      name { value }
                      geometry {coordinates }
                   }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE, basename);

        // Filtering on withoutLocationsOnly stopPlace should only return one
        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", hasSize(1))
                .body("data.stopPlace[0].name.value", equalTo(nameWithoutLocation))
                .body("data.stopPlace[0].geometry", nullValue());
    }

    /**
     * Search for stop place by quay original ID
     */
    @Test
    public void searchForStopPlaceByQuayOriginalIdQuery() {
        String stopPlaceName = "Travbanen";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        String quayOriginalId = "BRA:Quay:187";
        Quay quay = new Quay();
        quay.getOriginalIds().add(quayOriginalId);

        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                {
                    stopPlace: %s (
                        query:"%s"
                        allVersions:true
                    )
                    {
                        id
                        name { value }
                    }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE, quayOriginalId);

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName));
    }

    @Test
    public void searchForStopPlaceNsrIdInQuery() {
        String stopPlaceName = "Jallafjellet";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                {
                    stopPlace: %s (
                        query:"%s",
                        allVersions:true
                    )
                    {
                        id
                        name {value}
                    }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE, stopPlace.getNetexId());

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName));
    }

    @Test
    public void lookupStopPlaceAllVersions() {

        String stopPlaceName = "TestPlace";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);
        StopPlace copy = versionCreator.createCopy(stopPlace, StopPlace.class);
        copy = stopPlaceVersionedSaverService.saveNewVersion(stopPlace, copy);

        assertThat(stopPlace.getVersion()).isEqualTo(1);
        assertThat(copy.getVersion()).isEqualTo(2);

        String graphQlJsonQuery = """
                {
                    stopPlace: %s (
                        %s:"%s",
                        allVersions:true
                    )
                    {
                        id
                        version
                    }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE, GraphQLNames.ID, stopPlace.getNetexId());

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", hasSize(2))
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .body("data.stopPlace[1].id", equalTo(stopPlace.getNetexId()));
    }

    @Test
    public void searchForQuayNsrIdInQuery() {
        String stopPlaceName = "Solkroken";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));

        Quay quay = new Quay();
        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                {
                    stopPlace: %s (
                        query:"%s",
                        allVersions:true
                    )
                    {
                        id
                        name {value}
                    }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE, quay.getNetexId());

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName));
    }

    @Test
    public void searchForStopPlaceNoParamsExpectAllVersions() {
        String stopPlaceName = "Eselstua";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                {
                    stopPlace: %s (
                        allVersions:true
                    )
                    {
                        name { value }
                    }
                }"""
                .formatted(GraphQLNames.FIND_STOPPLACE);

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName));
    }

    @Test
    public void searchForStopPlaceAllEmptyParams() {
        String stopPlaceName = "Eselstua";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                {
                    stopPlace: %s (
                         id:"",
                         countyReference:"",
                         municipalityReference:"",
                         allVersions:true
                    )
                    {
                         id
                         name {value}
                    }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE);

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName));
    }

    @Test
    public void searchForStopPlaceByNameContainsCaseInsensitive() {
        String stopPlaceName = "Grytnes";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.533212, 59.678080)));
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                {
                    stopPlace: %s(
                        query:"ytNES",
                        allVersions:true
                    )
                    {
                        name {value}
                    }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE);

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName));
    }

    @Test
    public void searchForStopPlaceByKeyValue() {
        String stopPlaceName = "KeyValueStop";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.533212, 59.678080)));
        String key = "testKey";
        String value = "testValue";
        stopPlace.getKeyValues().put(key, new Value(value));
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                {
                    stopPlace: %s(
                        key:"%s",
                        values:"%s"
                        allVersions:true
                    )
                    {
                        id
                        name { value }
                        keyValues { key values }
                    }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE, key, value);

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", hasSize(1))
                .root("data.stopPlace[0]")
                .body("name.value", equalTo(stopPlaceName))
                .body("keyValues[0].key", equalTo(key))
                .body("keyValues[0].values", hasSize(1))
                .body("keyValues[0].values[0]", equalTo(value));
    }

    @Test
    public void searchForStopsWithDifferentStopPlaceTypeShouldHaveNoResult() {

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Fyrstekakeveien"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_TRAM);
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                {
                    stopPlace: %s (
                        stopPlaceType: %s
                    )
                    {
                        name {
                            value
                        }
                    }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE, StopTypeEnumeration.FERRY_STOP.value());

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", Matchers.hasSize(0));
    }

    @Test
    public void searchForExpiredStopPlace() {

        String name = "Gamleveien";
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(name));

        Instant fromDate = now.minusSeconds(10000);
        Instant toDate = now.minusSeconds(1000);

        ValidBetween validBetween = new ValidBetween(fromDate, toDate);
        stopPlace.setValidBetween(validBetween);
        stopPlaceRepository.save(stopPlace);

        //Ensure that from- and toDate is before "now"
        assertThat(fromDate.isBefore(now));
        assertThat(toDate.isBefore(now));

        String graphQlJsonQuery = """
                {
                    stopPlace: %s (
                        query:"%s",
                        pointInTime:"%s"
                    )
                    {
                        name {value}
                    }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE, name, stopPlace.getValidBetween().getFromDate().plusSeconds(10));
        // Verify that pointInTime within validity-period returns expected StopPlace
        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", Matchers.hasSize(1));

        // Verify that pointInTime *after* validity-period returns null
        graphQlJsonQuery = """
                {
                    stopPlace: %s (
                        query: "%s",
                        pointInTime:"%s"
                    )
                    {
                        name {value}
                    }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE, name, stopPlace.getValidBetween().getToDate().plusSeconds(10));

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", Matchers.hasSize(0));

        // Verify that pointInTime *before* validity-period returns null
        graphQlJsonQuery = """
                {
                    stopPlace: %s (
                        query:"%s",
                        pointInTime:"%s"
                    )
                    {
                        name {value}
                    }
                }"""
                .formatted(GraphQLNames.FIND_STOPPLACE, name, stopPlace.getValidBetween().getFromDate().minusSeconds(100));

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", Matchers.hasSize(0));

        // PointInTime must be set. If not, max version is returned.
        graphQlJsonQuery = """
                {
                    stopPlace: %s (
                        query:"%s",
                        pointInTime:"%s"
                    )
                    {
                        name {value}
                    }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE, name, now.toString());

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", Matchers.hasSize(0));
    }

    @Test
    public void searchForStopsWithoutQuays() {

        String name = "fuscator";
        StopPlace stopPlaceWithoutQuays = new StopPlace(new EmbeddableMultilingualString(name));
        stopPlaceWithoutQuays.setValidBetween(new ValidBetween(now.minusMillis(10000)));
        stopPlaceRepository.save(stopPlaceWithoutQuays);

        StopPlace stopPlaceWithQuays = new StopPlace(new EmbeddableMultilingualString(name));
        stopPlaceWithQuays.setValidBetween(new ValidBetween(now.minusMillis(10000)));
        stopPlaceWithQuays.getQuays().add(new Quay());
        stopPlaceRepository.save(stopPlaceWithQuays);

        String graphQlJsonQuery = """
                {
                    stopPlace: %s (
                        query:"%s",
                        %s:true
                    )
                    {
                        id
                        name {value}
                    }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE, name, WITHOUT_QUAYS_ONLY);

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", Matchers.hasSize(1))
                .body("data.stopPlace[0].id", equalTo(stopPlaceWithoutQuays.getNetexId()));
    }

    @Test
    public void searchForTramStopWithMunicipalityAndCounty() {

        TopographicPlace hordaland = new TopographicPlace(new EmbeddableMultilingualString("Hordaland"));
        topographicPlaceRepository.save(hordaland);

        TopographicPlace kvinnherad = createMunicipalityWithCountyRef("Kvinnherad", hordaland);

        StopPlace stopPlace = createStopPlaceWithMunicipalityRef("Anda", kvinnherad, StopTypeEnumeration.TRAM_STATION);
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                {
                    stopPlace: %s (
                        stopPlaceType: %s,
                        countyReference:"%s",
                        municipalityReference:"%s",
                        allVersions:true
                    )
                    {
                        name {value}
                    }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE, StopTypeEnumeration.TRAM_STATION.value(),
                        hordaland.getNetexId(), kvinnherad.getNetexId());

        executeGraphQLQueryOnly(graphQlJsonQuery)
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

        String graphQlJsonQuery = """
                {
                    stopPlace: %s (
                        municipalityReference:"%s"
                    )
                    {
                        name {value}
                    }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE, asker.getNetexId());

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", hasSize(0));
    }

    @Test
    public void searchForStopInMunicipalityOnly() {
        TopographicPlace akershus = topographicPlaceRepository.save(new TopographicPlace(new EmbeddableMultilingualString("Akershus")));
        TopographicPlace asker = createMunicipalityWithCountyRef("Asker", akershus);
        String stopPlaceName = "Nesbru";
        createStopPlaceWithMunicipalityRef(stopPlaceName, asker);

        String graphQlJsonQuery = """
                {
                    stopPlace: %s (
                        municipalityReference: ["%s"],
                        allVersions:true
                    )
                    {
                        id
                        name {value}
                    }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE, asker.getNetexId());

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", hasSize(1))
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName));
    }

    @Test
    public void searchForStopsInTwoMunicipalities() {
        TopographicPlace asker = createMunicipalityWithCountyRef("Asker", null);
        TopographicPlace baerum = createMunicipalityWithCountyRef("Bærum", null);

        createStopPlaceWithMunicipalityRef("Nesbru", asker);
        createStopPlaceWithMunicipalityRef("Slependen", baerum);

        String graphQlJsonQuery = """
                {
                    stopPlace: %s (
                        municipalityReference: ["%s", "%s"],
                        allVersions:true
                    )
                    {
                        id
                        name { value }
                        ... on StopPlace {
                            quays {
                                id
                                name  { value }
                            }
                        }
                    }
                }"""
                .formatted(GraphQLNames.FIND_STOPPLACE, baerum.getNetexId(), asker.getNetexId());

        executeGraphQLQueryOnly(graphQlJsonQuery)
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

        var graphQlJsonQuery = """
                {
                    stopPlace: %s (
                        allVersions:true,
                        countyReference:["%s","%s"],
                        municipalityReference:["%s","%s"]
                    )
                    {
                        id
                        name { value }
                    }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE, akershus.getNetexId(), buskerud.getNetexId(), lier.getNetexId(), asker.getNetexId());

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace.name.value", hasItems("Nesbru", "Hennumkrysset"));
    }

    @Test
    public void searchForStopsInDifferentMunicipalitiesButSameCounty() {
        TopographicPlace akershus = topographicPlaceRepository.save(new TopographicPlace(new EmbeddableMultilingualString("Akershus")));
        TopographicPlace asker = createMunicipalityWithCountyRef("Asker", akershus);
        TopographicPlace baerum = createMunicipalityWithCountyRef("Bærum", akershus);

        createStopPlaceWithMunicipalityRef("Trollstua", asker);
        createStopPlaceWithMunicipalityRef("Haslum", baerum);

        var graphQlJsonQuery = """
                {
                    stopPlace: %s (
                        allVersions:true,
                        countyReference:["%s"]
                    )
                    {
                        id
                        name { value }
                    }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE, akershus.getNetexId());

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace.name.value", hasItems("Trollstua", "Haslum"));
    }

    @Test
    public void searchForStopById() {

        StopPlace stopPlace = createStopPlace("Espa");
        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                {
                    stopPlace: %s (
                         %s:"%s",
                        allVersions:true
                    )
                    {
                        id
                        name { value }
                    }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE, QUERY, stopPlace.getNetexId());

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].name.value", equalTo(stopPlace.getName().getValue()));
    }

    @Test
    public void getTariffZonesForStop() {

        StopPlace stopPlace = new StopPlace();

        TariffZone tariffZone = new TariffZone();
        tariffZone.setName(new EmbeddableMultilingualString("V02"));
        tariffZone.setVersion(1L);
        tariffZoneRepository.save(tariffZone);

        stopPlace.getTariffZones().add(new TariffZoneRef(tariffZone));

        stopPlaceRepository.save(stopPlace);

        String graphQlJsonQuery = """
                {
                    stopPlace:  %s (
                        id:"%s",
                        allVersions:true
                    )
                    {
                        id
                        tariffZones {
                            id
                            version
                            name {
                                value
                            }
                        }
                    }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE, stopPlace.getNetexId());

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .root("data.stopPlace[0]")
                .body("tariffZones[0].id", equalTo(tariffZone.getNetexId()))
                .body("tariffZones[0].name.value", equalTo(tariffZone.getName().getValue()));
    }

    @Test
    public void testSimpleMutationCreateStopPlace() {

        String name = "Testing name";
        String shortName = "Testing shortname";
        String description = "Testing description";

        Float lon = (float) 10.11111;
        Float lat = (float) 59.11111;

        String graphQlJsonQuery = """
                mutation {
                    stopPlace: %s (
                        StopPlace: {
                            name: {
                                value:"%s"
                            },
                            shortName:{
                                value:"%s"
                            },
                            description:{
                                value:"%s"
                            },
                            stopPlaceType: %s,
                            geometry: {
                                type: Point,
                                coordinates: [[%s, %s]]
                            }
                        }
                    )
                    {
                        id
                        weighting
                        name { value }
                        shortName { value }
                        description { value }
                        stopPlaceType
                        geometry { type coordinates }
                    }
                }
                """
                .formatted(GraphQLNames.MUTATE_STOPPLACE, name, shortName,
                        description, StopTypeEnumeration.TRAM_STATION.value(), lon, lat);

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .root("data.stopPlace[0]")
                .body("id", notNullValue())
                .body("name.value", equalTo(name))
                .body("shortName.value", equalTo(shortName))
                .body("description.value", equalTo(description))
                .body("stopPlaceType", equalTo(StopTypeEnumeration.TRAM_STATION.value()))
                .body("geometry.type", equalTo("Point"))
                .body("geometry.coordinates[0][0]", comparesEqualTo(lon))
                .body("geometry.coordinates[0][1]", comparesEqualTo(lat))
                .body("weighting", comparesEqualTo(InterchangeWeightingEnumeration.INTERCHANGE_ALLOWED.value()));

        assertThat(entityChangedJMSListener.hasReceivedEvent(null, 1L, EntityChangedEvent.CrudAction.CREATE, null)).isTrue();
    }

    @Test
    public void testMutateStopWithTariffZoneRef() {

        var tariffZone = new TariffZone();
        tariffZone.setName(new EmbeddableMultilingualString("tariff zone"));
        tariffZone.setNetexId("CRI:TariffZone:1");
        tariffZoneRepository.save(tariffZone);

        String graphqlQuery = """
                mutation {
                  stopPlace:mutateStopPlace(
                    StopPlace: {
                        name: {
                            value: "Name",
                            lang:"nor"
                        },
                        tariffZones: [{ref: "%s"}]
                    }
                )
                {
                    id
                    tariffZones {
                      id
                      name {
                        value
                      }
                    }
                  }
                }
                """
                .formatted(tariffZone.getNetexId());

        executeGraphQLQueryOnly(graphqlQuery)
                .root("data.stopPlace[0]")
                .body("tariffZones", is(not(empty())))
                .body("tariffZones[0].id", equalTo(tariffZone.getNetexId()))
                .body("tariffZones[0].name.value", equalTo(tariffZone.getName().getValue()));
    }

    /**
     * Test added for NRP-1851
     */
    @Test
    public void testSimpleMutationCreateStopPlaceImportedIdWithNewLine() {

        String name = "Testing name";
        String jsonFriendlyNewLineStr = "\\\\n";
        String shortName = "          ";
        String originalId = "   TEST:1234    ";

        String graphQlJsonQuery = """
                mutation {
                    stopPlace: %s (
                        StopPlace: {
                            name: {
                                value:"%s"
                            },
                            shortName: {
                                value:"%s"
                            },
                            keyValues: {
                                key:"%s",
                                values:"%s"
                            }
                        }
                    )
                    {
                        id
                        name { value }
                        shortName { value }
                        keyValues { key values }
                    }
                }
                """
                .formatted(GraphQLNames.MUTATE_STOPPLACE,
                        name,
                        shortName + jsonFriendlyNewLineStr,
                        GraphQLNames.IMPORTED_ID,
                        originalId + jsonFriendlyNewLineStr);

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .root("data.stopPlace[0]")
                .body("id", notNullValue())
                .body("name.value", equalTo(name))
                .body("shortName.value", equalTo(""))
                .body("keyValues[0].key", equalTo(GraphQLNames.IMPORTED_ID))
                .body("keyValues[0].values[0]", equalTo(originalId.trim()));

        assertThat(entityChangedJMSListener.hasReceivedEvent(null, 1L, EntityChangedEvent.CrudAction.CREATE, null)).isTrue();
    }

    @Test
    public void createParentStopPlace() {
        var bus = new StopPlace();
        bus.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));
        bus.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        stopPlaceVersionedSaverService.saveNewVersion(bus);

        var tram = new StopPlace();
        tram.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));
        tram.setStopPlaceType(StopTypeEnumeration.TRAM_STATION);
        stopPlaceVersionedSaverService.saveNewVersion(tram);

        var fromDate = now.plusSeconds(100000);

        var parentStopPlaceName = "Super stop place name";
        var versionComment = "VersionComment";

        var graphQlJsonQuery = """
                mutation {
                    stopPlace: %s (
                        %s: {
                            stopPlaceIds:["%s" ,"%s"]
                            name: { value: "%s" }
                            validBetween: { fromDate:"%s" }
                            versionComment:"%s"
                        }
                    )
                    {
                        id
                        name { value }
                        children {
                            id
                            name { value }
                            stopPlaceType
                            version
                        }
                        validBetween { fromDate toDate }
                        versionComment
                    }
                }
                 """
                .formatted(GraphQLNames.CREATE_MULTI_MODAL_STOPPLACE, INPUT, bus.getNetexId(),
                        tram.getNetexId(), parentStopPlaceName, fromDate, versionComment);

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace.name.value", equalTo(parentStopPlaceName))
                .body("data.stopPlace.stopPlaceType", nullValue())
                .body("data.stopPlace.versionComment", equalTo(versionComment))
                .root("data.stopPlace.children.find { it.id == '" + tram.getNetexId() + "'}")
                .body("version", equalTo(String.valueOf(tram.getVersion() + 1)))
                .body("stopPlaceType", equalTo(StopTypeEnumeration.TRAM_STATION.value()))
                .body("name", nullValue())
                .root("data.stopPlace.children.find { it.id == '" + bus.getNetexId() + "'}")
                .body("name", nullValue())
                .body("stopPlaceType", equalTo(StopTypeEnumeration.BUS_STATION.value()))
                .body("version", equalTo(String.valueOf(bus.getVersion() + 1)));
    }

    @Transactional
    protected StopPlace createParentInTransaction(StopPlace existingChild, StopPlace newChild, EmbeddableMultilingualString parentStopPlaceName) {

        existingChild = stopPlaceVersionedSaverService.saveNewVersion(existingChild);
        stopPlaceVersionedSaverService.saveNewVersion(newChild);

        return multiModalStopPlaceEditor.createMultiModalParentStopPlace(List.of(existingChild.getNetexId()), parentStopPlaceName);
    }

    @Autowired
    private MultiModalStopPlaceEditor multiModalStopPlaceEditor;

    @Test
    public void addChildToParentStopPlace() {
        var existingChild = new StopPlace();

        existingChild.setStopPlaceType(StopTypeEnumeration.HARBOUR_PORT);

        var newChild = new StopPlace(new EmbeddableMultilingualString("new child"));
        newChild.setVersion(10L);
        newChild.setStopPlaceType(StopTypeEnumeration.LIFT_STATION);

        LOGGER.debug("tariff zones new child: " + newChild.getTariffZones());

        var parentStopPlaceName = "parent stop place name";

        var parent = createParentInTransaction(existingChild, newChild, new EmbeddableMultilingualString(parentStopPlaceName));

        var versionComment = "VersionComment";

        // Make sure dates are after privous version of parent stop place
        var fromDate = parent.getValidBetween().getFromDate().plusSeconds(1000);
        var toDate = fromDate.plusSeconds(70000);

        var graphQlJsonQuery = """
                mutation {
                    stopPlace: %s (
                        %s: {
                            %s: "%s",
                            %s:["%s"],
                            validBetween: {
                                fromDate:"%s",
                                toDate:"%s"
                            },
                            versionComment:"%s"
                        }
                    )
                    {
                        id
                        name { value }
                        children {
                            id
                            name { value }
                            stopPlaceType
                            version
                        }
                        validBetween { fromDate toDate }
                        version
                        versionComment
                    }
                }
                """
                .formatted(ADD_TO_MULTIMODAL_STOPPLACE, INPUT, PARENT_SITE_REF,
                        parent.getNetexId(), STOP_PLACE_IDS, newChild.getNetexId(),
                        fromDate, toDate, versionComment);

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace.name.value", equalTo(parentStopPlaceName))
                .body("data.stopPlace.stopPlaceType", nullValue())
                .body("data.stopPlace.versionComment", equalTo(versionComment))
                .body("data.stopPlace.version", equalTo("2"))
                .root("data.stopPlace.children.find { it.id == '%s'}".formatted(existingChild.getNetexId()))
                .body("name.value", nullValue())
                // version 3 expected. 1: created, 2: added to parent stop, 3: new child added to parent stop
                .body("version", equalTo(String.valueOf(existingChild.getVersion() + 2)))
                .body("stopPlaceType", equalTo(existingChild.getStopPlaceType().value()))
                .root("data.stopPlace.children.find { it.id == '%s'}".formatted(newChild.getNetexId()))
                .body("name.value", equalTo(newChild.getName().getValue()))
                .body("version", equalTo(String.valueOf(newChild.getVersion() + 1)))
                .body("stopPlaceType", equalTo(newChild.getStopPlaceType().value()));
    }

    @Test
    public void testSimpleMutationUpdateStopPlace() {
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

        StopPlace adjacentStopPlace = createStopPlace("Adjacent Site");
        adjacentStopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.11111, 59.11111)));
        stopPlaceVersionedSaverService.saveNewVersion(adjacentStopPlace);

        String updatedName = "Testing name";
        String updatedShortName = "Testing shortname";
        String updatedDescription = "Testing description";

        float updatedLon = 10.11111F;
        float updatedLat = 59.11111F;

        String versionComment = "Stop place moved";

        InterchangeWeightingEnumeration weighting = InterchangeWeightingEnumeration.INTERCHANGE_ALLOWED;

        String graphQlJsonQuery = """
                mutation {
                    stopPlace: %s (
                        StopPlace: {
                            id: "%s"
                            name: { value: "%s" }
                            shortName: { value: "%s" }
                            description: { value:"%s" }
                            adjacentSites: [ {ref: "%s"} ]
                            stopPlaceType: %s
                            versionComment: "%s"
                            geometry: {
                              type: Point
                              coordinates: [[%s,%s]]
                            }
                            weighting: %s
                        }
                    )
                    {
                        id
                        name { value }
                        shortName { value }
                        description { value }
                        adjacentSites { ref }
                        stopPlaceType
                        versionComment
                        topographicPlace {
                            id
                            topographicPlaceType
                            parentTopographicPlace {
                                id
                                topographicPlaceType
                            }
                        }
                        weighting
                        geometry { type coordinates }
                        validBetween { fromDate toDate }
                    }
                }"""
                .formatted(GraphQLNames.MUTATE_STOPPLACE, stopPlace.getNetexId(), updatedName,
                        updatedShortName, updatedDescription, adjacentStopPlace.getNetexId(),
                        StopTypeEnumeration.TRAM_STATION.value(), versionComment, updatedLon,
                        updatedLat, weighting.value());

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .root("data.stopPlace[0]")
                .body("name.value", equalTo(updatedName))
                .body("shortName.value", equalTo(updatedShortName))
                .body("description.value", equalTo(updatedDescription))
                .body("adjacentSites[0].ref", equalTo(adjacentStopPlace.getNetexId()))
                .body("stopPlaceType", equalTo(StopTypeEnumeration.TRAM_STATION.value()))
                .body("versionComment", equalTo(versionComment))
                .body("geometry.type", equalTo("Point"))
                .body("geometry.coordinates[0][0]", comparesEqualTo(updatedLon))
                .body("geometry.coordinates[0][1]", comparesEqualTo(updatedLat))
                .body("weighting", equalTo(weighting.value()))
                .body("topographicPlace.id", notNullValue())
                .body("topographicPlace.topographicPlaceType", equalTo(TopographicPlaceTypeEnumeration.MUNICIPALITY.value()))
                .body("topographicPlace.parentTopographicPlace", notNullValue())
                .body("topographicPlace.parentTopographicPlace.id", notNullValue())
                .body("topographicPlace.parentTopographicPlace.topographicPlaceType", equalTo(TopographicPlaceTypeEnumeration.COUNTY.value()));
    }

    @Test
    public void testTerminateStopPlaceValidity() {
        StopPlace stopPlace = createStopPlace("Stop place soon to be invalidated");
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));
        stopPlace.setValidBetween(new ValidBetween(Instant.EPOCH));
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String versionComment = "Stop place not valid anymore";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

        // Mutate stop place. The new version should have valid from now.
        String fromDate = dateTimeFormatter.format(now.atZone(exportTimeZone.getDefaultTimeZoneId()));

        // The new version should be terminated in the future.
        String toDate = dateTimeFormatter.format(now.plusSeconds(2000).atZone(exportTimeZone.getDefaultTimeZoneId()));

        String graphQlJsonQuery = """
                mutation {
                    stopPlace: %s (
                        StopPlace: {
                            id: "%s",
                            versionComment: "%s",
                            validBetween: {
                                fromDate: "%s",
                                toDate: "%s"
                            }
                        }
                    )
                    {
                        validBetween {
                            fromDate
                            toDate
                        }
                        versionComment
                    }
                }
                """
                .formatted(GraphQLNames.MUTATE_STOPPLACE, stopPlace.getNetexId(), versionComment, fromDate, toDate);

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0]", notNullValue())
                .root("data.stopPlace[0]")
                .body("versionComment", equalTo(versionComment))
                .body("validBetween.fromDate", comparesEqualTo(fromDate))
                .body("validBetween.toDate", comparesEqualTo(toDate));
    }

    @Test
    public void testSimpleMutationUpdateKeyValuesStopPlace() {

        StopPlace stopPlace = createStopPlace("Espa");
        stopPlace.setShortName(new EmbeddableMultilingualString("E"));
        stopPlace.setDescription(new EmbeddableMultilingualString("E6s beste boller"));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));
        stopPlace.setAllAreasWheelchairAccessible(false);
        stopPlace.setWeighting(InterchangeWeightingEnumeration.NO_INTERCHANGE);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String graphQlJsonQuery = """
                mutation {
                    stopPlace:  %s (
                        StopPlace: {
                            id: "%s",
                            keyValues: [{
                                key: "jbvId",
                                values: ["1234"]
                            }]
                        }
                    )
                    {
                        id
                        keyValues {
                            key
                            values
                        }
                    }
                }
                """
                .formatted(GraphQLNames.MUTATE_STOPPLACE, stopPlace.getNetexId());

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .root("data.stopPlace[0]")
                .body("id", equalTo(stopPlace.getNetexId()))
                .body("keyValues[0].key", equalTo("jbvId"))
                .body("keyValues[0].values[0]", equalTo("1234"));
    }

    @Test
    public void testSimpleMutationUpdateTransportModeStopPlace() {

        StopPlace stopPlace = createStopPlace("Bussen");
        stopPlace.setTransportMode(VehicleModeEnumeration.BUS);
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlace.setBusSubmode(BusSubmodeEnumeration.LOCAL_BUS);
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)));

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String newTransportMode = VehicleModeEnumeration.TRAM.value();
        String newSubmode = TramSubmodeEnumeration.LOCAL_TRAM.value();
        String graphQlJsonQuery = """
                mutation {
                    stopPlace: %s (
                        StopPlace: {
                            id:"%s",
                            transportMode: %s,
                            submode: %s
                        }
                    )
                    {
                        id
                        transportMode
                        submode
                    }
                }
                """
                .formatted(GraphQLNames.MUTATE_STOPPLACE, stopPlace.getNetexId(), newTransportMode, newSubmode);

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .root("data.stopPlace[0]")
                .body("id", equalTo(stopPlace.getNetexId()))
                .body("transportMode", equalTo(newTransportMode))
                .body("submode", equalTo(newSubmode));

        var stopPlaces = stopPlaceRepository.findAll();
        for (StopPlace stopPlaceVersion : stopPlaces) {
            if (stopPlaceVersion.getVersion() == 1) {
                assertThat(stopPlaceVersion.getBusSubmode()).as("version 1").isNotNull().isEqualTo(BusSubmodeEnumeration.LOCAL_BUS);
                assertThat(stopPlaceVersion.getTramSubmode()).as("version 1").isNull();
            } else if (stopPlaceVersion.getVersion() == 2) {
                assertThat(stopPlaceVersion.getBusSubmode()).as("version 2").isNull();
                assertThat(stopPlaceVersion.getTramSubmode()).as("version 2").isNotNull().isEqualTo(TramSubmodeEnumeration.LOCAL_TRAM);
            }
        }
    }

    @Test
    public void testGetValidTransportModes() {

        String graphQlJsonQuery = """
                {
                   validTransportModes {
                       transportMode
                       submode
                   }
                }
                """;

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.validTransportModes", notNullValue())
                .body("data.validTransportModes[0].transportMode", notNullValue())
                .body("data.validTransportModes[0].submode", notNullValue());
    }

    @Test
    public void testSimpleMutationCreateQuay() {

        StopPlace stopPlace = createStopPlace("Espa");

        stopPlaceRepository.save(stopPlace);

        String name = "Testing name";
        String shortName = "Testing shortname";
        String description = "Testing description";
        String publicCode = "publicCode 2";

        String privateCodeValue = "PB03";
        String privateCodeType = "Type";

        float lon = 10.11111F;
        float lat = 59.11111F;

        String graphQlJsonQuery = """
                mutation {
                    stopPlace: %s (
                        StopPlace: {
                            id:"%s",
                            quays: [{
                                name: {
                                    value:"%s"
                                },
                                shortName: {
                                    value:"%s"
                                },
                                description: {
                                    value:"%s"
                                },
                                publicCode:"%s",
                                privateCode: {
                                    value:"%s",
                                    type:"%s"
                                },
                                geometry: {
                                    type: Point,
                                    coordinates: [[%s, %s]]
                                }
                            }]
                        }
                    )
                    {
                        id
                        name { value }
                        quays {
                            id
                            publicCode
                            privateCode { value type }
                            name { value }
                            shortName { value }
                            description { value }
                            geometry { type coordinates }
                        }
                    }
                }
                """
                .formatted(GraphQLNames.MUTATE_STOPPLACE, stopPlace.getNetexId(), name,
                shortName, description, publicCode, privateCodeValue, privateCodeType, lon, lat);

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].id", notNullValue())
                .body("data.stopPlace[0].name.value", equalTo(stopPlace.getName().getValue()))
                .root("data.stopPlace[0].quays[0]")
                .body("id", notNullValue())
                .body("name.value", equalTo(name))
                .body("shortName.value", equalTo(shortName))
                .body("description.value", equalTo(description))
                .body("publicCode", equalTo(publicCode))
                .body("privateCode.value", equalTo(privateCodeValue))
                .body("privateCode.type", equalTo(privateCodeType))
                .body("geometry.type", equalTo("Point"))
                .body("geometry.coordinates[0][0]", comparesEqualTo(lon))
                .body("geometry.coordinates[0][1]", comparesEqualTo(lat));
    }

    @Test
    public void testSimpleMutationUpdateQuay() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Espa"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));

        Quay quay = new Quay();
        quay.setCompassBearing(90F);
        quay.setCentroid(geometryFactory.createPoint(new Coordinate(11.2, 60.2)));
        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);

        String name = "Testing name";
        String shortName = "Testing shortname";
        String description = "Testing description";

        float lon = 10.11111F;
        float lat = 59.11111F;

        float compassBearing = 180F;

        String graphQlJsonQuery = """
                mutation {
                    stopPlace: %s (
                        StopPlace: {
                            id:"%s",
                            quays: [{
                                id:"%s",
                                name: { value:"%s" }
                                shortName: {
                                    value:"%s"
                                },
                                description:{
                                    value:"%s"
                                },
                                geometry: {
                                    type: Point,
                                    coordinates: [[%s, %s]]
                                },
                                compassBearing:%s
                            }]
                        }
                    )
                    {
                        id
                        name { value }
                        quays {
                            id
                            name { value }
                            shortName { value }
                            description { value }
                            geometry { type coordinates }
                            compassBearing
                        }
                    }
                }
                """.formatted(GraphQLNames.MUTATE_STOPPLACE, stopPlace.getNetexId(), quay.getNetexId(),
                name, shortName, description, lon, lat, compassBearing);

        executeGraphQLQueryOnly(graphQlJsonQuery)
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
    public void testMoveQuayToNewStop() {

        StopPlace stopPlace = new StopPlace();

        Quay quay = new Quay();
        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);

        String versionComment = "moving quays";

        String graphQlJsonQuery = """
                mutation {
                    stopPlace:
                        %s (
                            %s:"%s",
                            %s:"%s"
                        )
                        {
                            id
                            ...on StopPlace {
                                quays { id }
                            }
                            versionComment
                        }
                }
                """
                .formatted(MOVE_QUAYS_TO_STOP, QUAY_IDS, quay.getNetexId(), TO_VERSION_COMMENT, versionComment);

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace.id", not(comparesEqualTo(stopPlace.getNetexId())))
                .body("data.stopPlace.versionComment", equalTo(versionComment))
                .root("data.stopPlace.quays[0]")
                .body("id", comparesEqualTo(quay.getNetexId()));
    }

    @Test
    public void testSimpleMutationAddSecondQuay() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        stopPlace.setVersion(1L);
        stopPlace.setName(new EmbeddableMultilingualString("Espa"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));

        Quay quay = new Quay();
        quay.setCompassBearing(90F);
        Point point = geometryFactory.createPoint(new Coordinate(11.2, 60.2));
        quay.setCentroid(point);
        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);

        String name = "Testing name";
        String shortName = "Testing shortname";
        String description = "Testing description";

        Float lon = 10.11111F;
        Float lat = 59.11111F;

        float compassBearing = 180F;

        String graphQlJsonQuery = """
                mutation {
                    stopPlace: %s (
                        StopPlace: {
                            id:"%s",
                            quays: [{
                                name: {
                                    value:"%s"
                                },
                                shortName: {
                                    value:"%s"
                                },
                                description:{
                                    value:"%s"
                                },
                                geometry: {
                                    type: Point
                                    coordinates: [[%s,%s]]
                                },
                                compassBearing: %s
                            }]
                        }
                    )
                    {
                        id
                        name { value }
                        quays {
                            id
                            name { value }
                            shortName { value }
                            description { value }
                            geometry { type coordinates }
                            compassBearing
                        }
                    }
                }
                """
                .formatted(GraphQLNames.MUTATE_STOPPLACE, stopPlace.getNetexId(),
                        name, shortName, description, lon, lat, compassBearing);

        String manuallyAddedQuayId = quay.getNetexId();

        ValidatableResponse validatableResponse = executeGraphQLQueryOnly(graphQlJsonQuery);

        System.out.println("validatableResponse" + validatableResponse);

        validatableResponse
                .body("data.stopPlace[0].id", comparesEqualTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlace.getName().getValue()))
                .body("data.stopPlace[0].quays", hasSize(2))
                // First Quay - added manually
                .root("data.stopPlace[0].quays.find { it.id == '" + manuallyAddedQuayId + "'}")
                    .body("name", nullValue())
                    .body("shortName", nullValue())
                    .body("description", nullValue())
                    .body("geometry.type", equalTo(point.getGeometryType()))
                    .body("geometry.coordinates[0][0]", comparesEqualTo(point.getX()))
                    .body("geometry.coordinates[0][1]", comparesEqualTo(point.getY()))
                    .body("compassBearing", comparesEqualTo(quay.getCompassBearing()))
                // Second Quay - added using GraphQL
                .root("data.stopPlace[0].quays.find { it.id != '" + manuallyAddedQuayId + "'}")
                    .body("name.value", equalTo(name))
                    .body("shortName.value", equalTo(shortName))
                    .body("description.value", equalTo(description))
                    .body("geometry.type", equalTo("Point"))
                    .body("geometry.coordinates[0][0]", comparesEqualTo(String.valueOf(lon)))
                    .body("geometry.coordinates[0][1]", comparesEqualTo(String.valueOf(lat)))
                    .body("compassBearing", comparesEqualTo(compassBearing));

        assertThat(entityChangedJMSListener.hasReceivedEvent(
                stopPlace.getNetexId(),
                stopPlace.getVersion() + 1,
                EntityChangedEvent.CrudAction.UPDATE,
                stopPlace.getChanged().toEpochMilli())
        ).isTrue();
    }

    @Test
    public void testMutationUpdateStopPlaceCreateQuayAndUpdateQuay() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        stopPlace.setName(new EmbeddableMultilingualString("Espa"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));

        Quay quay = new Quay();
        quay.setCompassBearing(90F);
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

        float lon = 10.11111F;
        float lat = 59.11111F;

        float compassBearing = 180F;

        String graphQlJsonQuery = """
                mutation {
                     stopPlace: %s (
                         StopPlace: {
                             id:"%s",
                             name: {
                                 value:"%s"
                             },
                             quays: [{
                                 name: { value:"%s" },
                                 shortName:{ value:"%s" },
                                 description:{ value:"%s" },
                                 geometry: {
                                     type: Point,
                                     coordinates: [[%s, %s]]
                                 },
                                 compassBearing: %s
                             },
                             {
                                 id: "%s",
                                 name: {
                                     value:"%s"
                                 },
                                 shortName:{
                                     value:"%s"
                                 },
                                 description:{
                                     value:"%s"
                                 }
                             }]
                         }
                     )
                     {
                         id
                         name { value }
                         quays {
                             id
                             name { value }
                             shortName { value }
                             description { value }
                             geometry { type coordinates }
                             compassBearing
                         }
                     }
                 }
                """
                .formatted(GraphQLNames.MUTATE_STOPPLACE, stopPlace.getNetexId(),
                        newStopName, newQuaydName, newQuayShortName, newQuayDescription,
                        lon, lat, compassBearing, quay.getNetexId(),
                        updatedName, updatedShortName, updatedDescription);

        String manuallyAddedQuayId = quay.getNetexId();

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace[0].id", comparesEqualTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(newStopName))
                .body("data.stopPlace[0].quays", hasSize(2))
                // First Quay - added manually, then updated
                .root("data.stopPlace[0].quays.find { it.id == '" + manuallyAddedQuayId + "'}")
                    .body("name.value", equalTo(updatedName))
                    .body("shortName.value", equalTo(updatedShortName))
                    .body("description.value", equalTo(updatedDescription))
                    .body("geometry.type", equalTo(point.getGeometryType()))
                    .body("geometry.coordinates[0][0]", equalTo(point.getX()))
                    .body("geometry.coordinates[0][1]", equalTo(point.getY()))
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

        assertThat(
                entityChangedJMSListener.hasReceivedEvent(
                        stopPlace.getNetexId(),
                        stopPlace.getVersion() + 1,
                        EntityChangedEvent.CrudAction.UPDATE,
                        stopPlace.getChanged().toEpochMilli()
                )).isTrue();
    }

    @Test
    public void testTicketMachineTicketOfficeTrueFalse() {

        var stopPlace = new StopPlace();
        TicketingEquipment ticketingEquipment = new TicketingEquipment();
        ticketingEquipment.setTicketMachines(null);
        ticketingEquipment.setTicketOffice(null);
        ticketingEquipment.setNumberOfMachines(BigInteger.valueOf(7));

        PlaceEquipment placeEquipment = new PlaceEquipment();

        placeEquipment.getInstalledEquipment().add(ticketingEquipment);

        stopPlace.setPlaceEquipments(placeEquipment);
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        var query = """
                {
                    stopPlace(
                        query:"%s"
                    )
                    {
                        placeEquipments {
                            ticketingEquipment {
                                ticketMachines
                                numberOfMachines
                                ticketOffice
                            }
                        }
                    }
                }
                """.formatted(stopPlace.getNetexId());

        executeGraphQLQueryOnly(query)
                .body("data.stopPlace[0].placeEquipments.ticketingEquipment[0].ticketMachines", is(true))
                .body("data.stopPlace[0].placeEquipments.ticketingEquipment[0].ticketOffice", Matchers.is(false));
    }

    /**
     * Test that reproduces NRP-1433
     */
    @Test
    public void testSimpleMutationUpdateStopPlaceKeepPlaceEquipmentsOnQuay() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Espa"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));
        stopPlace.setPlaceEquipments(createPlaceEquipments());

        Quay quay = new Quay();
        quay.setCompassBearing(90F);
        quay.setCentroid(geometryFactory.createPoint(new Coordinate(11.2, 60.2)));
        quay.setPlaceEquipments(createPlaceEquipments());
        stopPlace.getQuays().add(quay);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String name = "Testing name";
        String netexId = stopPlace.getNetexId();

        //Verify that placeEquipments have been set
        String graphQlStopPlaceQuery = """
                {
                    stopPlace: %s (
                        id:"%s"
                    )
                    {
                        id
                        placeEquipments {
                            waitingRoomEquipment { id }
                            sanitaryEquipment { id }
                            ticketingEquipment { id }
                            cycleStorageEquipment { id }
                            shelterEquipment { id }
                            generalSign { id }
                        }
                        ... on StopPlace {
                            quays {
                                id
                                placeEquipments {
                                    waitingRoomEquipment { id }
                                    sanitaryEquipment { id }
                                    ticketingEquipment { id }
                                    cycleStorageEquipment { id }
                                    shelterEquipment { id }
                                    generalSign { id }
                                }
                            }
                        }
                    }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE, netexId);

        executeGraphQLQueryOnly(graphQlStopPlaceQuery)
                .root("data.stopPlace[0]")
                .body("id", comparesEqualTo(netexId))
                .body("placeEquipments", notNullValue())
                .body("placeEquipments.waitingRoomEquipment[0]", notNullValue())
                .body("placeEquipments.sanitaryEquipment[0]", notNullValue())
                .body("placeEquipments.ticketingEquipment[0]", notNullValue())
                .body("placeEquipments.cycleStorageEquipment[0]", notNullValue())
                .body("placeEquipments.shelterEquipment[0]", notNullValue())
                .body("placeEquipments.generalSign[0]", notNullValue())
                .root("data.stopPlace[0].quays[0]")
                .body("id", notNullValue())
                .body("placeEquipments", notNullValue())
                .body("placeEquipments.waitingRoomEquipment[0]", notNullValue())
                .body("placeEquipments.sanitaryEquipment[0]", notNullValue())
                .body("placeEquipments.ticketingEquipment[0]", notNullValue())
                .body("placeEquipments.cycleStorageEquipment[0]", notNullValue())
                .body("placeEquipments.shelterEquipment[0]", notNullValue())
                .body("placeEquipments.generalSign[0]", notNullValue());

        //Update StopPlace name
        String graphQlJsonQuery = """
                mutation {
                    stopPlace: %s (
                        StopPlace: {
                            id:"%s",
                            name: { value:"%s" }
                        }
                    )
                    {
                    id
                    name { value }
                    placeEquipments {
                        waitingRoomEquipment { id }
                        sanitaryEquipment { id }
                        ticketingEquipment { id }
                        cycleStorageEquipment { id }
                        shelterEquipment { id }
                        generalSign { id }
                    }
                    quays {
                        id
                        placeEquipments {
                            waitingRoomEquipment { id }
                            sanitaryEquipment { id }
                            ticketingEquipment { id }
                            cycleStorageEquipment { id }
                            shelterEquipment { id }
                            generalSign { id }
                        }
                    }
                }
                """
                .formatted(GraphQLNames.MUTATE_STOPPLACE, stopPlace.getNetexId(), name);

        executeGraphQLQueryOnly(graphQlJsonQuery)
                .root("data.stopPlace[0]")
                .body("id", comparesEqualTo(netexId))
                .body("name.value", comparesEqualTo(name))
                .body("placeEquipments", notNullValue())
                .body("placeEquipments.waitingRoomEquipment", notNullValue())
                .body("placeEquipments.sanitaryEquipment", notNullValue())
                .body("placeEquipments.ticketingEquipment", notNullValue())
                .body("placeEquipments.cycleStorageEquipment", notNullValue())
                .body("placeEquipments.shelterEquipment", notNullValue())
                .body("placeEquipments.generalSign", notNullValue())
                .root("data.stopPlace[0].quays[0]")
                .body("id", notNullValue())
                .body("placeEquipments", notNullValue())
                .body("placeEquipments.waitingRoomEquipment", notNullValue())
                .body("placeEquipments.sanitaryEquipment", notNullValue())
                .body("placeEquipments.ticketingEquipment", notNullValue())
                .body("placeEquipments.cycleStorageEquipment", notNullValue())
                .body("placeEquipments.shelterEquipment", notNullValue())
                .body("placeEquipments.generalSign", notNullValue());
    }

    @Test
    public void testSimpleSaveAlternativeNames() {

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
        quay.setCompassBearing(90F);
        quay.setCentroid(geometryFactory.createPoint(new Coordinate(11.2, 60.2)));
        quay.getAlternativeNames().add(altName);
        quay.getAlternativeNames().add(altName2);

        stopPlace.getQuays().add(quay);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String netexId = stopPlace.getNetexId();

        //Verify that placeEquipments have been set
        String graphQlStopPlaceQuery = """
                {
                    stopPlace: %s (
                        id:"%s"
                    )
                    {
                        id
                        alternativeNames {
                            nameType
                            name {
                                value
                                lang
                            }
                        }
                        ... on StopPlace {
                            quays {
                                id
                                alternativeNames {
                                    nameType
                                    name {
                                        value
                                        lang
                                    }
                                }
                            }
                        }
                    }
                }
                """
                .formatted(GraphQLNames.FIND_STOPPLACE, netexId);

        executeGraphQLQueryOnly(graphQlStopPlaceQuery)
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
                .body("alternativeNames[1].name.lang", notNullValue());
    }

    @Test
    public <T extends Comparable<T>> void testSimpleMutateAlternativeNames() {

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

        String graphQlStopPlaceQuery = """
                mutation {
                    stopPlace: %s (
                        StopPlace: {
                            id:"%s"
                            alternativeNames: [{
                                nameType: %s
                                name: {
                                    value: "%s"
                                    lang:"%s"
                                }
                            }]
                        }
                    )
                    {
                        id
                        alternativeNames {
                            nameType
                            name {
                                value
                                lang
                            }
                        }
                    }
                }
                """
                .formatted(GraphQLNames.MUTATE_STOPPLACE, netexId, altName.getNameType().value(),
                        updatedAlternativeNameValue, updatedAlternativeNameLang);

        executeGraphQLQueryOnly(graphQlStopPlaceQuery)
                .body("data.stopPlace[0].id", comparesEqualTo(netexId))
                .body("data.stopPlace[0].alternativeNames", notNullValue())
                .root("data.stopPlace[0].alternativeNames[0]")
                .body("name.value", comparesEqualTo(updatedAlternativeNameValue))
                .body("name.lang", comparesEqualTo(updatedAlternativeNameLang));
    }

    @Test
    public void testSimpleMutatePlaceEquipmentSignPrivateCode() {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("Name"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)));

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        String netexId = stopPlace.getNetexId();

        String type = "StopPoint";
        String value = "512";
        String graphQlStopPlaceQuery = """
                mutation {
                    stopPlace: %s (
                        StopPlace: {
                            id:"%s",
                            placeEquipments: {
                                generalSign:[{
                                    signContentType: TransportModePoint,
                                    privateCode: {
                                        value: "%s",
                                        type:"%s"
                                    }
                                }]
                            }
                        }
                    )
                    {
                        id
                        placeEquipments {
                            generalSign {
                                privateCode {
                                    value
                                    type
                                }
                                signContentType
                            }
                        }
                    }
                }
                """
                .formatted(GraphQLNames.MUTATE_STOPPLACE, netexId, value, type
                );

        executeGraphQLQueryOnly(graphQlStopPlaceQuery)
                .body("data.stopPlace[0].id", comparesEqualTo(netexId))
                .body("data.stopPlace[0].placeEquipments", notNullValue())
                .root("data.stopPlace[0].placeEquipments")
                .body("generalSign[0]", notNullValue())
                .body("generalSign[0].privateCode.type", comparesEqualTo(type))
                .body("generalSign[0].privateCode.value", comparesEqualTo(value));
    }

    private StopPlace createStopPlaceWithMunicipalityRef(String name, TopographicPlace municipality, StopTypeEnumeration type) {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(name));
        stopPlace.setStopPlaceType(type);
        if (municipality != null) {
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
        municipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        if (county != null) {
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

        GeneralSign skilt = new GeneralSign();
        skilt.setSignContentType(SignContentEnumeration.TRANSPORT_MODE);
        PrivateCodeStructure privCode = new PrivateCodeStructure();
        privCode.setValue("512");
        skilt.setPrivateCode(privCode);

        equipments.getInstalledEquipment().add(venterom);
        equipments.getInstalledEquipment().add(billettAutomat);
        equipments.getInstalledEquipment().add(toalett);
        equipments.getInstalledEquipment().add(leskur);
        equipments.getInstalledEquipment().add(sykkelstativ);
        equipments.getInstalledEquipment().add(skilt);
        return equipments;
    }
}
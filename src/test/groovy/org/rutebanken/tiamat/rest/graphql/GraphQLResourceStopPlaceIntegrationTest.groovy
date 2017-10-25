/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.rest.graphql

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.Point
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.rutebanken.tiamat.changelog.EntityChangedEvent
import org.rutebanken.tiamat.changelog.EntityChangedJMSListener
import org.rutebanken.tiamat.model.*
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper
import org.rutebanken.tiamat.service.stopplace.MultiModalStopPlaceEditor
import org.rutebanken.tiamat.time.ExportTimeZone
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import java.time.Instant
import java.time.format.DateTimeFormatter

import static org.assertj.core.api.Assertions.assertThat
import static org.hamcrest.Matchers.*
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*
import static org.rutebanken.tiamat.rest.graphql.operations.MultiModalityOperationsBuilder.INPUT
import static org.rutebanken.tiamat.rest.graphql.scalars.DateScalar.DATE_TIME_PATTERN

def class GraphQLResourceStopPlaceIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

    @Autowired
    private EntityChangedJMSListener entityChangedJMSListener

    @Autowired
    private ExportTimeZone exportTimeZone

    @Before
    void cleanReceivedJMS(){
        entityChangedJMSListener.popEvents()
    }

    @Test
    void retrieveStopPlaceWithTwoQuays() throws Exception {
        Quay quay = new Quay()
        String firstQuayName = "first quay name"
        quay.setName(new EmbeddableMultilingualString(firstQuayName))

        Quay secondQuay = new Quay()
        String secondQuayName = "second quay"
        secondQuay.setName(new EmbeddableMultilingualString(secondQuayName))


        String stopPlaceName = "StopPlace"
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName))

        stopPlace.setQuays(new HashSet<>())
        stopPlace.getQuays().add(quay)
        stopPlace.getQuays().add(secondQuay)

        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(5, 60)))
        stopPlaceRepository.save(stopPlace)

        String graphQlJsonQuery = "{" +
                "\"query\":\"" +
                "{ stopPlace:" + GraphQLNames.FIND_STOPPLACE + " (id:\\\"" + stopPlace.getNetexId() + "\\\") {" +
                "   id " +
                "   name { value } " +
                "  ... on StopPlace {" +
                "   quays { " +
                "      id " +
                "      name  { value } " +
                "     }" +
                "  }" +
                "}" +
                "}\",\"variables\":\"\"}"

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName))
                .body("data.stopPlace[0].quays.name.value", hasItems(firstQuayName, secondQuayName))
                .body("data.stopPlace[0].quays.id", hasItems(quay.getNetexId(), secondQuay.getNetexId()))
    }

    /**
     * Use explicit parameter for original ID search
     */
    @Test
    void searchForStopPlaceByOriginalId() throws Exception {
        String stopPlaceName = "Eselstua"
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName))

        stopPlaceRepository.save(stopPlace)

        String originalId = "RUT:Stop:1234"
        Value value = new Value(originalId)
        stopPlace.getKeyValues().put(NetexIdMapper.ORIGINAL_ID_KEY, value)
        stopPlaceRepository.save(stopPlace)

        String graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace: " + GraphQLNames.FIND_STOPPLACE +
                " (" + GraphQLNames.IMPORTED_ID_QUERY + ":\\\"" + originalId + "\\\")" +
                " { " +
                "  id " +
                "  name { value } " +
                " }" +
                "}\",\"variables\":\"\"}"


        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName))
    }

    /**
     * Use query parameter for original ID search
     */
    @Test
    void searchForStopPlaceByOriginalIdQuery() throws Exception {
        String stopPlaceName = "Fleskeberget"
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName))

        stopPlaceRepository.save(stopPlace)

        String originalId = "BRA:StopPlace:666"
        Value value = new Value(originalId)
        stopPlace.getKeyValues().put(NetexIdMapper.ORIGINAL_ID_KEY, value)
        stopPlaceRepository.save(stopPlace)

        String graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace: " + GraphQLNames.FIND_STOPPLACE +
                " (" + GraphQLNames.QUERY + ":\\\"" + originalId + "\\\")" +
                " { " +
                "  id " +
                "  name { value } " +
                " }" +
                "}\",\"variables\":\"\"}"


        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName))
    }


    /**
     * Use query parameter for original ID search
     */
    @Test
    void searchForStopPlaceWithoutCoordinates() throws Exception {
        String basename = "koordinaten"
        String nameWithLocation = basename + " nr 1"
        StopPlace stopPlaceWithCoordinates = new StopPlace(new EmbeddableMultilingualString(nameWithLocation))
        stopPlaceWithCoordinates.setCentroid(geometryFactory.createPoint(new Coordinate(10.533212, 59.678080)))

        String nameWithoutLocation = basename + " nr 2"
        StopPlace stopPlaceWithoutCoordinates = new StopPlace(new EmbeddableMultilingualString(nameWithoutLocation))
        stopPlaceWithoutCoordinates.setCentroid(null)

        stopPlaceRepository.save(stopPlaceWithCoordinates)
        stopPlaceRepository.save(stopPlaceWithoutCoordinates)

        String graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace: " + GraphQLNames.FIND_STOPPLACE +
                " (" + GraphQLNames.QUERY + ":\\\"" + basename + "\\\")" +
                " { " +
                "  id " +
                "  name { value } " +
                "  geometry {coordinates } " +
                " }" +
                "}\",\"variables\":\"\"}"

        // Search for stopPlace should return both StopPlaces above
        executeGraphQL(graphQlJsonQuery)
                .root("data.stopPlace.find { it.id == '" + stopPlaceWithCoordinates.getNetexId() + "'}")
                    .body("name.value", equalTo(nameWithLocation))
                    .body("geometry", notNullValue())
                    .body("geometry.coordinates", hasSize(1))
                .root("data.stopPlace.find { it.id == '" + stopPlaceWithoutCoordinates.getNetexId() + "'}")
                    .body("name.value", equalTo(nameWithoutLocation))
                    .body("geometry", nullValue())


        graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace: " + GraphQLNames.FIND_STOPPLACE +
                " (" + GraphQLNames.QUERY + ":\\\"" + basename + "\\\" withoutLocationOnly:true)" +
                " { " +
                "  id " +
                "  name { value } " +
                "  geometry {coordinates } " +
                " }" +
                "}\",\"variables\":\"\"}"

        // Filtering on withoutLocationsOnly stopPlace should only return one
        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace", hasSize(1))
                .body("data.stopPlace[0].name.value", equalTo(nameWithoutLocation))
                .body("data.stopPlace[0].geometry", nullValue())

    }

    /**
     * Search for stop place by quay original ID
     */
    @Test
    void searchForStopPlaceByQuayOriginalIdQuery() throws Exception {
//        String stopPlaceName = "Hesteløpsbanen";
        String stopPlaceName = "Travbanen"
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName))


        String quayOriginalId = "BRA:Quay:187"
        Quay quay = new Quay()
        quay.getOriginalIds().add(quayOriginalId)

        stopPlace.getQuays().add(quay)

        stopPlaceRepository.save(stopPlace)

        String graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace: " + GraphQLNames.FIND_STOPPLACE +
                " (" + GraphQLNames.QUERY + ":\\\"" + quayOriginalId + "\\\")" +
                " { " +
                "  id " +
                "  name { value } " +
                " }" +
                "}\",\"variables\":\"\"}"


        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName))
    }

    @Test
    void searchForStopPlaceNsrIdInQuery() throws Exception {
        String stopPlaceName = "Jallafjellet"
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName))

        stopPlaceRepository.save(stopPlace)

        String graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace: " + GraphQLNames.FIND_STOPPLACE +
                " (" + GraphQLNames.QUERY + ":\\\"" + stopPlace.getNetexId() + "\\\")" +
                " { " +
                "  id " +
                "  name { value } " +
                " }" +
                "}\",\"variables\":\"\"}"


        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName))
    }

    @Test
    void lookupStopPlaceAllVersions() throws Exception {

        String stopPlaceName = "TestPlace"
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName))

        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace)
        StopPlace copy = stopPlaceVersionedSaverService.createCopy(stopPlace, StopPlace.class)
        copy = stopPlaceVersionedSaverService.saveNewVersion(stopPlace, copy)

        assertThat(stopPlace.getVersion()).isEqualTo(1)
        assertThat(copy.getVersion()).isEqualTo(2)

        String graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace: " + GraphQLNames.FIND_STOPPLACE +
                " (" + GraphQLNames.ID + ":\\\"" + stopPlace.getNetexId() + "\\\" allVersions:true)" +
                " { " +
                "  id " +
                "  version " +
                " }" +
                "}\",\"variables\":\"\"}"


        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace", hasSize(2))
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .body("data.stopPlace[1].id", equalTo(stopPlace.getNetexId()))
    }

    @Test
    void searchForQuayNsrIdInQuery() throws Exception {
        String stopPlaceName = "Solkroken"
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName))

        Quay quay = new Quay()
        stopPlace.getQuays().add(quay)

        stopPlaceRepository.save(stopPlace)

        String graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace: " + GraphQLNames.FIND_STOPPLACE +
                " (" + GraphQLNames.QUERY + ":\\\"" + quay.getNetexId() + "\\\")" +
                " { " +
                "  id " +
                "  name { value } " +
                " }" +
                "}\",\"variables\":\"\"}"


        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].id", equalTo(stopPlace.getNetexId()))
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName))
    }

    @Test
    void searchForStopPlaceNoParams() throws Exception {
        String stopPlaceName = "Eselstua"
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName))
        stopPlaceRepository.save(stopPlace)

        String graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace: " + GraphQLNames.FIND_STOPPLACE +
                " { " +
                "  name { value } " +
                " } " +
                "}\",\"variables\":\"\"}"


        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName))
    }

    @Test
    void searchForStopPlaceAllEmptyParams() throws Exception {
        String stopPlaceName = "Eselstua"
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName))
        stopPlaceRepository.save(stopPlace)

        String graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace: " + GraphQLNames.FIND_STOPPLACE +
                "(id:\\\"\\\" countyReference:\\\"\\\" municipalityReference:\\\"\\\") " +
                " { " +
                "  name { value } " +
                " } " +
                "}\",\"variables\":\"\"}"


        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName))
    }

    @Test
    void searchForStopPlaceByNameContainsCaseInsensitive() throws Exception {
        String stopPlaceName = "Grytnes"
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName))
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.533212, 59.678080)))
        stopPlaceRepository.save(stopPlace)

        String graphQlJsonQuery = "{" +
                "\"query\":\"{" +
                "  stopPlace: " + GraphQLNames.FIND_STOPPLACE + " (query:\\\"ytNES\\\") { " +
                "    name {value} " +
                "  } " +
                "}\"," +
                "\"variables\":\"\"}"


        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].name.value", equalTo(stopPlaceName))
    }


    @Test
    void searchForStopPlaceByKeyValue() throws Exception {
        String stopPlaceName = "KeyValueStop"
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(stopPlaceName))
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.533212, 59.678080)))
        String key = "testKey"
        String value = "testValue"
        stopPlace.getKeyValues().put(key, new Value(value))
        stopPlaceRepository.save(stopPlace)

        String graphQlJsonQuery = "{" +
                "\"query\":\"{" +
                "  stopPlace: " + GraphQLNames.FIND_STOPPLACE + " (key:\\\"" + key + "\\\", values:\\\"" + value + "\\\") { " +
                "    name {value} " +
                "    keyValues {key values} " +
                "  } " +
                "}\"," +
                "\"variables\":\"\"}"


        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace", hasSize(1))
                .root("data.stopPlace[0]")
                    .body("name.value", equalTo(stopPlaceName))
                    .body("keyValues[0].key", equalTo(key))
                    .body("keyValues[0].values",  hasSize(1))
                    .body("keyValues[0].values[0]", equalTo(value))
    }

    @Test
    void searchForStopsWithDifferentStopPlaceTypeShouldHaveNoResult() {

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Fyrstekakeveien"))
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_TRAM)
        stopPlaceRepository.save(stopPlace)

        String graphQlJsonQuery = "{" +
                "\"query\":\"{" +
                "  stopPlace: " + GraphQLNames.FIND_STOPPLACE +  " (stopPlaceType:" + StopTypeEnumeration.FERRY_STOP.value() + ") { " +
                "    name {value} " +
                "  } " +
                "}\"," +
                "\"variables\":\"\"}"

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace", Matchers.hasSize(0))
    }


    @Test
    void searchForExpiredStopPlace() {

        String name = "Gamleveien"
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(name))

        Instant now = Instant.now()

        Instant fromDate = now.minusSeconds(10000)
        Instant toDate = now.minusSeconds(1000)

        ValidBetween validBetween = new ValidBetween(fromDate, toDate)
        stopPlace.setValidBetween(validBetween)
        stopPlaceRepository.save(stopPlace)

        //Ensure that from- and toDate is before "now"
        assertThat(fromDate.isBefore(now))
        assertThat(toDate.isBefore(now))

        String graphQlJsonQuery = """{
                  stopPlace:  ${GraphQLNames.FIND_STOPPLACE} (query:"${name}", pointInTime:"${stopPlace.getValidBetween().getFromDate().plusSeconds(10)}") {
                            name {value}
                        }
                    }"""
        // Verify that pointInTime within validity-period returns expected StopPlace
        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", Matchers.hasSize(1))

        // Verify that pointInTime *after* validity-period returns null
        graphQlJsonQuery = """{
                stopPlace: ${GraphQLNames.FIND_STOPPLACE} (query: "${name}", pointInTime:"${stopPlace.getValidBetween().getToDate().plusSeconds(10).toString()}") {
                    name {value}
                    }
                }"""

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", Matchers.hasSize(0))

        // Verify that pointInTime *before* validity-period returns null
        graphQlJsonQuery = """{
                stopPlace: ${GraphQLNames.FIND_STOPPLACE} (query:"${name}", pointInTime:"${stopPlace.getValidBetween().getFromDate().minusSeconds(100).toString()}") { "
                    name {value}
                  }
                }"""

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", Matchers.hasSize(0))

        // PointInTime must be set. If not, max version is returned.
        graphQlJsonQuery = """{
                  stopPlace: ${GraphQLNames.FIND_STOPPLACE} (query:"${name}", pointInTime:"${now.toString()}") {
                    name {value}
                  }
                }"""

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", Matchers.hasSize(0))
    }

    @Test
    void searchForStopsWithoutQuays() {

        String name = "fuscator"
        StopPlace stopPlaceWithoutQuays = new StopPlace(new EmbeddableMultilingualString(name))
        stopPlaceWithoutQuays.setValidBetween(new ValidBetween(Instant.now().minusMillis(10000)))
        stopPlaceRepository.save(stopPlaceWithoutQuays)

        StopPlace stopPlaceWithQuays = new StopPlace(new EmbeddableMultilingualString(name))
        stopPlaceWithQuays.setValidBetween(new ValidBetween(Instant.now().minusMillis(10000)))
        stopPlaceWithQuays.getQuays().add(new Quay())
        stopPlaceRepository.save(stopPlaceWithQuays)

        String graphQlJsonQuery = """{
                  stopPlace:  ${GraphQLNames.FIND_STOPPLACE} (query:"${name}", ${WITHOUT_QUAYS_ONLY}:true) {
                            id
                            name {value}
                        }
                    }"""
        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace", Matchers.hasSize(1))
                .body("data.stopPlace[0].id", equalTo(stopPlaceWithoutQuays.getNetexId()))
    }


    @Test
    void searchForTramStopWithMunicipalityAndCounty() {

        TopographicPlace hordaland = new TopographicPlace(new EmbeddableMultilingualString("Hordaland"))
        topographicPlaceRepository.save(hordaland)

        TopographicPlace kvinnherad = createMunicipalityWithCountyRef("Kvinnherad", hordaland)

        StopPlace stopPlace = createStopPlaceWithMunicipalityRef("Anda", kvinnherad, StopTypeEnumeration.TRAM_STATION)
        stopPlaceRepository.save(stopPlace)

        String graphQlJsonQuery = "{" +
                "\"query\":\"{" +
                "  stopPlace:" + GraphQLNames.FIND_STOPPLACE +
                " (stopPlaceType:" + StopTypeEnumeration.TRAM_STATION.value() + " countyReference:\\\"" + hordaland.getNetexId() + "\\\" municipalityReference:\\\"" + kvinnherad.getNetexId() +"\\\") { " +
                "    name {value} " +
                "  } " +
                "}\"," +
                "\"variables\":\"\"}"

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace", hasSize(1))
                .body("data.stopPlace[0].name.value", equalTo(stopPlace.getName().getValue()))
    }

    @Test
    void searchForStopsInMunicipalityThenExpectNoResult() {
        // Stop Place not related to municipality
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("Nesbru"))
        stopPlaceRepository.save(stopPlace)

        TopographicPlace asker = new TopographicPlace(new EmbeddableMultilingualString("Asker"))
        topographicPlaceRepository.save(asker)

        String graphQlJsonQuery = "{" +
                "\"query\":\"{" +
                "  stopPlace:" + GraphQLNames.FIND_STOPPLACE +
                " (municipalityReference:\\\"" + asker.getNetexId() +"\\\") { " +
                "    name {value} " +
                "  } " +
                "}\"," +
                "\"variables\":\"\"}"

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace", hasSize(0))
    }

    @Test
    void searchForStopInMunicipalityOnly() {
        TopographicPlace akershus = topographicPlaceRepository.save(new TopographicPlace(new EmbeddableMultilingualString("Akershus")))
        TopographicPlace asker = createMunicipalityWithCountyRef("Asker", akershus)
        String stopPlaceName = "Nesbru"
        createStopPlaceWithMunicipalityRef(stopPlaceName, asker)

        String graphQlJsonQuery = "{" +
                "\"query\":\"{" +
                "  stopPlace:" + GraphQLNames.FIND_STOPPLACE +
                " (municipalityReference:\\\"" + asker.getNetexId() +"\\\") { " +
                "    name {value} " +
                "  } " +
                "}\"," +
                "\"variables\":\"\"}"

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace", hasSize(1))
                .body("data.stopPlace[0].name.value",  equalTo(stopPlaceName))
    }

    @Test
    void searchForStopsInTwoMunicipalities() {
        TopographicPlace asker = createMunicipalityWithCountyRef("Asker", null)
        TopographicPlace baerum = createMunicipalityWithCountyRef("Bærum", null)

        createStopPlaceWithMunicipalityRef("Nesbru", asker)
        createStopPlaceWithMunicipalityRef("Slependen", baerum)


        String graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace:" + GraphQLNames.FIND_STOPPLACE +
                " (municipalityReference:[\\\""+baerum.getNetexId()+"\\\",\\\""+asker.getNetexId()+"\\\"]) {" +
                "id " +
                "name { value } " +
                "  ... on StopPlace {" +
                "quays " +
                "  { " +
                "   id " +
                "   name  { value } " +
                "  }  " +
                "}" +
                "}" +
                "}\",\"variables\":\"\"}"

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace.name.value", hasItems("Nesbru", "Slependen"))
    }

    @Test
    void searchForStopsInTwoCountiesAndTwoMunicipalities() {
        TopographicPlace akershus = topographicPlaceRepository.save(new TopographicPlace(new EmbeddableMultilingualString("Akershus")))
        TopographicPlace buskerud = topographicPlaceRepository.save(new TopographicPlace(new EmbeddableMultilingualString("Buskerud")))

        TopographicPlace lier = createMunicipalityWithCountyRef("Lier", buskerud)
        TopographicPlace asker = createMunicipalityWithCountyRef("Asker", akershus)

        createStopPlaceWithMunicipalityRef("Nesbru", asker)
        createStopPlaceWithMunicipalityRef("Hennumkrysset", asker)

        String graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace:" + GraphQLNames.FIND_STOPPLACE +
                " (countyReference:[\\\""+akershus.getNetexId()+"\\\",\\\""+buskerud.getNetexId()+"\\\"] municipalityReference:[\\\""+lier.getNetexId()+"\\\",\\\""+asker.getNetexId()+"\\\"]) {" +
                "id " +
                "name { value } " +
                "}" +
                "}\",\"variables\":\"\"}"

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace.name.value", hasItems("Nesbru", "Hennumkrysset"))
    }

    @Test
    void searchForStopsInDifferentMunicipalitiesButSameCounty() {
        TopographicPlace akershus = topographicPlaceRepository.save(new TopographicPlace(new EmbeddableMultilingualString("Akershus")))
        TopographicPlace asker = createMunicipalityWithCountyRef("Asker", akershus)
        TopographicPlace baerum = createMunicipalityWithCountyRef("Bærum", akershus)

        createStopPlaceWithMunicipalityRef("Trollstua", asker)
        createStopPlaceWithMunicipalityRef("Haslum", baerum)

        String graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace:" + GraphQLNames.FIND_STOPPLACE +
                " (countyReference:\\\""+akershus.getNetexId()+"\\\") {" +
                "id " +
                "name { value } " +
                "}" +
                "}\",\"variables\":\"\"}"

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace.name.value", hasItems("Trollstua", "Haslum"))
    }

    @Test
    void searchForStopById() throws Exception {

        StopPlace stopPlace = createStopPlace("Espa")
        stopPlaceRepository.save(stopPlace)

        String graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace:" + GraphQLNames.FIND_STOPPLACE+
                " (id:\\\""+ stopPlace.getNetexId() +"\\\") {" +
                "id " +
                "name { value } " +
                "}" +
                "}\",\"variables\":\"\"}"

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0].name.value", equalTo(stopPlace.getName().getValue()))
    }

    @Test
    void getTariffZonesForStop() throws Exception {

        StopPlace stopPlace = new StopPlace()

        TariffZone tariffZone = new TariffZone()
        tariffZone.setName(new EmbeddableMultilingualString("V02"))
        tariffZone.setVersion(1L)
        tariffZoneRepository.save(tariffZone)

        stopPlace.getTariffZones().add(new TariffZoneRef(tariffZone))

        stopPlaceRepository.save(stopPlace)

        String graphQlJsonQuery = "{" +
                "\"query\":\"{stopPlace:" + GraphQLNames.FIND_STOPPLACE+
                " (id:\\\""+ stopPlace.getNetexId() +"\\\") {" +
                "id " +
                "tariffZones { id version name { value }} " +
                "}" +
                "}\",\"variables\":\"\"}"

        executeGraphQL(graphQlJsonQuery)
                .root("data.stopPlace[0]")
                    .body("tariffZones[0].id", equalTo(tariffZone.getNetexId()))
                    .body("tariffZones[0].name.value", equalTo(tariffZone.getName().getValue()))
    }

    @Test
    void testSimpleMutationCreateStopPlace() throws Exception {

        String name = "Testing name"
        String shortName = "Testing shortname"
        String description = "Testing description"

        Float lon = new Float(10.11111)
        Float lat = new Float(59.11111)

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
                "}\",\"variables\":\"\"}"

        executeGraphQL(graphQlJsonQuery)
                .root("data.stopPlace[0]")
                    .body("id", notNullValue())
                    .body("name.value", equalTo(name))
                    .body("shortName.value", equalTo(shortName))
                    .body("description.value", equalTo(description))
                    .body("stopPlaceType", equalTo(StopTypeEnumeration.TRAM_STATION.value()))
                    .body("geometry.type", equalTo("Point"))
                    .body("geometry.coordinates[0][0]", comparesEqualTo(lon))
                    .body("geometry.coordinates[0][1]", comparesEqualTo(lat))

        assertThat(entityChangedJMSListener.hasReceivedEvent(null, 1l, EntityChangedEvent.CrudAction.CREATE)).isTrue()
    }


    /**
     * Test added for NRP-1851
     * @throws Exception
     */
    @Test
    void testSimpleMutationCreateStopPlaceImportedIdWithNewLine() throws Exception {

        String name = "Testing name"
        String jsonFriendlyNewLineStr = "\\\\n"
        String shortName = "          "
        String originalId = "   TEST:1234    "

        String graphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  stopPlace: " + GraphQLNames.MUTATE_STOPPLACE + " (StopPlace: {" +
                "          name: { value:\\\"" + name + "\\\" } " +
                "          shortName: { value:\\\"" + shortName + jsonFriendlyNewLineStr + "\\\" } " +
                "          keyValues:{ key:\\\"" + GraphQLNames.IMPORTED_ID +"\\\" values:\\\"" + originalId + jsonFriendlyNewLineStr + "\\\" }" +
                "       }) { " +
                "  id " +
                "  name { value } " +
                "  shortName { value } " +
                "  keyValues { key values } " +
                "  } " +
                "}\",\"variables\":\"\"}"

        executeGraphQL(graphQlJsonQuery)
                .root("data.stopPlace[0]")
                .body("id", notNullValue())
                .body("name.value", equalTo(name))
                .body("shortName.value", equalTo(""))
                .body("keyValues[0].key", equalTo(GraphQLNames.IMPORTED_ID))
                .body("keyValues[0].values[0]", equalTo(originalId.trim()))


        assertThat(entityChangedJMSListener.hasReceivedEvent(null, 1l, EntityChangedEvent.CrudAction.CREATE)).isTrue()
    }

    @Test
    void "Create parent stop place"() {
        def bus = new StopPlace()
        bus.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)))
        bus.setStopPlaceType(StopTypeEnumeration.BUS_STATION)
        stopPlaceVersionedSaverService.saveNewVersion(bus)

        def tram = new StopPlace()
        tram.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)))
        tram.setStopPlaceType(StopTypeEnumeration.TRAM_STATION)
        stopPlaceVersionedSaverService.saveNewVersion(tram)

        def fromDate = Instant.now().plusSeconds(100000)

        def parentStopPlaceName = "Super stop place name"
        def versionComment = "VersionComment"

        def graphQlJsonQuery = """mutation {
                 stopPlace: ${GraphQLNames.CREATE_MULTI_MODAL_STOPPLACE} (${INPUT}: {
                          stopPlaceIds:["${bus.getNetexId()}" ,"${tram.getNetexId()}"]
                          name: { value: "${parentStopPlaceName}" }
                          validBetween: { fromDate:"${fromDate}" }
                          versionComment:"${versionComment}"
                       }) {
                          id
                          name { value }
                          children {
                           id name { value } stopPlaceType version
                          }
                          validBetween { fromDate toDate }
                          versionComment
                       }
                  } """

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace.name.value", equalTo(parentStopPlaceName))
                .body("data.stopPlace.stopPlaceType", nullValue())
                .body("data.stopPlace.versionComment", equalTo(versionComment))
                .root("data.stopPlace.children.find { it.id == '" + tram.getNetexId() + "'}")
                .body("version", equalTo(String.valueOf(tram.getVersion()+1)))
                .body("stopPlaceType", equalTo(StopTypeEnumeration.TRAM_STATION.value()))
                .body("name", nullValue())
                .root("data.stopPlace.children.find { it.id == '" + bus.getNetexId() + "'}")
                .body("name", nullValue())
                .body("stopPlaceType", equalTo(StopTypeEnumeration.BUS_STATION.value()))
                .body("version", equalTo(String.valueOf(bus.getVersion()+1)))
    }


    @Transactional
    def createParentInTransaction(def existingChild, def newChild, EmbeddableMultilingualString parentStopPlaceName) {

        existingChild = stopPlaceVersionedSaverService.saveNewVersion(existingChild)
        newChild = stopPlaceVersionedSaverService.saveNewVersion(newChild)

        return multiModalStopPlaceEditor.createMultiModalParentStopPlace([existingChild.getNetexId()], parentStopPlaceName)
    }

    @Autowired
    private MultiModalStopPlaceEditor multiModalStopPlaceEditor

    @Test
    void "Add child to parent stop place"() {
        def existingChild = new StopPlace()

        existingChild.setStopPlaceType(StopTypeEnumeration.HARBOUR_PORT)

        def newChild = new StopPlace(new EmbeddableMultilingualString("new child"))
        newChild.setVersion(10L)
        newChild.setStopPlaceType(StopTypeEnumeration.LIFT_STATION)

        println "tariff zones new child: ${newChild.tariffZones}"

        def parentStopPlaceName = "parent stop place name"

        def parent = createParentInTransaction(existingChild, newChild, new EmbeddableMultilingualString(parentStopPlaceName))

        def versionComment = "VersionComment"

        // Make sure dates are after privous version of parent stop place
        def fromDate = parent.getValidBetween().getFromDate().plusSeconds(1000)
        def toDate = fromDate.plusSeconds(70000)

        def graphQlJsonQuery = """mutation {
                 stopPlace: ${ADD_TO_MULTIMODAL_STOPPLACE} (${INPUT}: {
                          ${PARENT_SITE_REF}: "${parent.getNetexId()}"
                          ${STOP_PLACE_IDS}:["${newChild.getNetexId()}"]
                          validBetween: { fromDate:"${fromDate}", toDate:"${toDate}" }
                          versionComment:"${versionComment}"
                       }) {
                          id
                          name { value }
                          children {
                           id name { value } stopPlaceType version
                          }
                          validBetween { fromDate toDate }
                          version
                          versionComment
                       }
                  } """

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace.name.value", equalTo(parentStopPlaceName))
                .body("data.stopPlace.stopPlaceType", nullValue())
                .body("data.stopPlace.versionComment", equalTo(versionComment))
                .body("data.stopPlace.version", equalTo("2"))

                .root("data.stopPlace.children.find { it.id == '${existingChild.getNetexId()}'}")

                    .body("name.value", nullValue())
                    // version 3 expected. 1: created, 2: added to parent stop, 3: new child added to parent stop
                    .body("version", equalTo("${existingChild.getVersion()+2}".toString()))
                    .body("stopPlaceType", equalTo(existingChild.getStopPlaceType().value()))

                .root("data.stopPlace.children.find { it.id == '${newChild.getNetexId()}'}".toString())
                    .body("name.value", equalTo(newChild.getName().getValue()))
                    .body("version", equalTo("${newChild.getVersion()+1}".toString()))
                    .body("stopPlaceType", equalTo(newChild.getStopPlaceType().value()))

    }

    @Test
    void testSimpleMutationUpdateStopPlace() throws Exception {
        TopographicPlace parentTopographicPlace = new TopographicPlace(new EmbeddableMultilingualString("countyforinstance"))
        parentTopographicPlace.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY)

        topographicPlaceRepository.save(parentTopographicPlace)
        TopographicPlace topographicPlace = createMunicipalityWithCountyRef("somewhere in space", parentTopographicPlace)

        StopPlace stopPlace = createStopPlace("Espa")
        stopPlace.setShortName(new EmbeddableMultilingualString("E"))
        stopPlace.setDescription(new EmbeddableMultilingualString("E6s beste boller"))
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS)
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)))
        stopPlace.setAllAreasWheelchairAccessible(false)
        stopPlace.setTopographicPlace(topographicPlace)
        stopPlace.setWeighting(InterchangeWeightingEnumeration.NO_INTERCHANGE)

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace)

        String updatedName = "Testing name"
        String updatedShortName = "Testing shortname"
        String updatedDescription = "Testing description"
//        String fromDate = "2012-04-23T18:25:43.511+0200";
//        String toDate = "2018-04-23T18:25:43.511+0200";

        Float updatedLon = new Float(10.11111)
        Float updatedLat = new Float(59.11111)

        String versionComment = "Stop place moved 100 meters"

        InterchangeWeightingEnumeration weighting = InterchangeWeightingEnumeration.INTERCHANGE_ALLOWED

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
                "  validBetween { fromDate toDate } " +
                "  } " +
                "}\",\"variables\":\"\"}"

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
                    .body("topographicPlace.parentTopographicPlace.topographicPlaceType", equalTo(TopographicPlaceTypeEnumeration.COUNTY.value()))
    }

    @Test
    void testTerminateStopPlaceValidity() throws Exception {
        StopPlace stopPlace = createStopPlace("Stop place soon to be invalidated")
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS)
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)))
        stopPlace.setValidBetween(new ValidBetween(Instant.EPOCH))
        stopPlaceVersionedSaverService.saveNewVersion(stopPlace)

        String versionComment = "Stop place not valid anymore"
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)

        Instant now = Instant.now()

        // Mutate stop place. The new version should have valid from now.
        String fromDate = dateTimeFormatter.format(now.atZone(exportTimeZone.getDefaultTimeZone()))

        // The new version should be terminated in the future.
        String toDate = dateTimeFormatter.format(now.plusSeconds(2000).atZone(exportTimeZone.getDefaultTimeZone()))

        String graphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  stopPlace: " + GraphQLNames.MUTATE_STOPPLACE + " (StopPlace: {" +
                "          id:\\\"" + stopPlace.getNetexId() + "\\\"" +
                "          versionComment: \\\""+ versionComment + "\\\"" +
                "          validBetween: {fromDate: \\\"" + fromDate + "\\\", toDate: \\\"" + toDate + "\\\"}" +
                "       }) { " +
                "  validBetween { fromDate toDate } " +
                "  versionComment " +
                "  } " +
                "}\",\"variables\":\"\"}"

        executeGraphQL(graphQlJsonQuery)
                .body("data.stopPlace[0]", notNullValue())
                .root("data.stopPlace[0]")
                    .body("versionComment", equalTo(versionComment))
                    .body("validBetween.fromDate", comparesEqualTo(fromDate))
                    .body("validBetween.toDate", comparesEqualTo(toDate))
    }


    @Test
    void testSimpleMutationUpdateKeyValuesStopPlace() throws Exception {

        StopPlace stopPlace = createStopPlace("Espa")
        stopPlace.setShortName(new EmbeddableMultilingualString("E"))
        stopPlace.setDescription(new EmbeddableMultilingualString("E6s beste boller"))
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS)
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)))
        stopPlace.setAllAreasWheelchairAccessible(false)
        stopPlace.setWeighting(InterchangeWeightingEnumeration.NO_INTERCHANGE)

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace)

        String graphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  stopPlace: " + GraphQLNames.MUTATE_STOPPLACE + " (StopPlace: {" +
                "          id:\\\"" + stopPlace.getNetexId() + "\\\"" +
                "          keyValues: [{" +
                "            key: \\\"jbvId\\\"" +
                "            values: [\\\"1234\\\", ]" +
                "          }]" +
                "       }) { " +
                "  id " +
                "  keyValues { key values } " +
                "  } " +
                "}\",\"variables\":\"\"}"

        executeGraphQL(graphQlJsonQuery)
                .root("data.stopPlace[0]")
                    .body("id", equalTo(stopPlace.getNetexId()))
                    .body("keyValues[0].key", equalTo("jbvId"))
                    .body("keyValues[0].values[0]", equalTo("1234"))
    }

    @Test
    void testSimpleMutationUpdateTransportModeStopPlace() throws Exception {

        StopPlace stopPlace = createStopPlace("Bussen")
        stopPlace.setTransportMode(VehicleModeEnumeration.BUS)
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS)
        stopPlace.setBusSubmode(BusSubmodeEnumeration.LOCAL_BUS)
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10, 59)))

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace)

        String newTransportMode = VehicleModeEnumeration.TRAM.value()
        String newSubmode = TramSubmodeEnumeration.LOCAL_TRAM.value()
        String graphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  stopPlace: " + GraphQLNames.MUTATE_STOPPLACE + " (StopPlace: {" +
                "          id:\\\"" + stopPlace.getNetexId() + "\\\"" +
                "          transportMode: " + newTransportMode +
                "          submode: " + newSubmode +
                "       }) { " +
                "  id " +
                "  transportMode" +
                "  submode " +
                "  } " +
                "}\",\"variables\":\"\"}"

        executeGraphQL(graphQlJsonQuery)
                .root("data.stopPlace[0]")
                .body("id", equalTo(stopPlace.getNetexId()))
                .body("transportMode", equalTo(newTransportMode))
                .body("submode", equalTo(newSubmode))
    }

    @Test
    void testGetValidTransportModes() throws Exception {

        String graphQlJsonQuery = "{" +
                "\"query\":\"{" +
                "  validTransportModes {" +
                "    transportMode" +
                "    submode" +
                "  }" +
                "}\",\"variables\":\"\"}"

        executeGraphQL(graphQlJsonQuery)
                .body("data.validTransportModes", notNullValue())
                .body("data.validTransportModes[0].transportMode", notNullValue())
                .body("data.validTransportModes[0].submode", notNullValue())
    }


    @Test
    void testSimpleMutationCreateQuay() throws Exception {

        StopPlace stopPlace = createStopPlace("Espa")

        stopPlaceRepository.save(stopPlace)

        String name = "Testing name"
        String shortName = "Testing shortname"
        String description = "Testing description"
        String publicCode = "publicCode 2"

        String privateCodeValue = "PB03"
        String privateCodeType = "Type"

        Float lon = new Float(10.11111)
        Float lat = new Float(59.11111)


        String graphQlJsonQuery = "{" +
                "\"query\":\"mutation { " +
                "  stopPlace: " + GraphQLNames.MUTATE_STOPPLACE + " (StopPlace: {" +
                "          id:\\\"" + stopPlace.getNetexId() + "\\\"" +
                "          quays: [{ " +
                "            name: { value:\\\"" + name + "\\\" } " +
                "            shortName:{ value:\\\"" + shortName + "\\\" } " +
                "            description:{ value:\\\"" + description + "\\\" }" +
                "            publicCode:\\\"" + publicCode + "\\\"" +
                "            privateCode:{ value:\\\"" + privateCodeValue + "\\\", type:\\\"" + privateCodeType + "\\\" }" +
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
                "    privateCode { value type }" +
                "    name { value } " +
                "    shortName { value } " +
                "    description { value } " +
                "    geometry { type coordinates } " +
                "  } " +
                "  } " +
                "}\",\"variables\":\"\"}"

        executeGraphQL(graphQlJsonQuery)
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
                    .body("geometry.coordinates[0][1]", comparesEqualTo(lat))
    }

    @Test
    void testSimpleMutationUpdateQuay() throws Exception {

        StopPlace stopPlace = new StopPlace()
        stopPlace.setName(new EmbeddableMultilingualString("Espa"))
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)))

        Quay quay = new Quay()
        quay.setCompassBearing(new Float(90))
        quay.setCentroid(geometryFactory.createPoint(new Coordinate(11.2, 60.2)))
        stopPlace.getQuays().add(quay)

        stopPlaceRepository.save(stopPlace)

        String name = "Testing name"
        String shortName = "Testing shortname"
        String description = "Testing description"

        Float lon = new Float(10.11111)
        Float lat = new Float(59.11111)

        Float compassBearing = new Float(180)

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
                "}\",\"variables\":\"\"}"

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
                    .body("compassBearing", comparesEqualTo(compassBearing))
    }


    @Test
    void testMoveQuayToNewStop() throws Exception {

        StopPlace stopPlace = new StopPlace()

        Quay quay = new Quay()
        stopPlace.getQuays().add(quay)

        stopPlaceRepository.save(stopPlace)

        String versionComment = "moving quays"

        String graphQlJsonQuery = """mutation {
                    stopPlace: ${MOVE_QUAYS_TO_STOP} (${QUAY_IDS}: "${quay.getNetexId()}", ${TO_VERSION_COMMENT}: "${versionComment}") {
                        id
                        ...on StopPlace {
                            quays {
                                id
                            }
                        }
                        versionComment
                    }
                }
              """

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.stopPlace.id", not(comparesEqualTo(stopPlace.getNetexId())))
                .body("data.stopPlace.versionComment", equalTo(versionComment))
                .root("data.stopPlace.quays[0]")
                    .body("id", comparesEqualTo(quay.getNetexId()))
    }


    @Test
    void testSimpleMutationAddSecondQuay() throws Exception {

        StopPlace stopPlace = new StopPlace()
        stopPlace.setVersion(1l)
        stopPlace.setName(new EmbeddableMultilingualString("Espa"))
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)))

        Quay quay = new Quay()
        quay.setCompassBearing(new Float(90))
        Point point = geometryFactory.createPoint(new Coordinate(11.2, 60.2))
        quay.setCentroid(point)
        stopPlace.getQuays().add(quay)

        stopPlaceRepository.save(stopPlace)

        String name = "Testing name"
        String shortName = "Testing shortname"
        String description = "Testing description"

        Float lon = new Float(10.11111)
        Float lat = new Float(59.11111)

        Float compassBearing = new Float(180)

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
                "}\",\"variables\":\"\"}"

        String manuallyAddedQuayId = quay.getNetexId()


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
                    .body("compassBearing", comparesEqualTo(compassBearing))

        assertThat(entityChangedJMSListener.hasReceivedEvent(stopPlace.getNetexId(), stopPlace.getVersion() + 1, EntityChangedEvent.CrudAction.UPDATE)).isTrue()
    }



    @Test
    void testMutationUpdateStopPlaceCreateQuayAndUpdateQuay() throws Exception {

        StopPlace stopPlace = new StopPlace()
        stopPlace.setVersion(1l)
        stopPlace.setName(new EmbeddableMultilingualString("Espa"))
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)))

        Quay quay = new Quay()
        quay.setCompassBearing(new Float(90))
        Point point = geometryFactory.createPoint(new Coordinate(11.2, 60.2))
        quay.setCentroid(point)
        stopPlace.getQuays().add(quay)

        stopPlaceRepository.save(stopPlace)

        String newStopName = "Shell - E6"
        String newQuaydName = "Testing name 1"
        String newQuayShortName = "Testing shortname 1"
        String newQuayDescription = "Testing description 1"

        String updatedName = "Testing name 2"
        String updatedShortName = "Testing shortname 2"
        String updatedDescription = "Testing description 2"

        Float lon = new Float(10.11111)
        Float lat = new Float(59.11111)

        Float compassBearing = new Float(180)

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
                "}\",\"variables\":\"\"}"

        String manuallyAddedQuayId = quay.getNetexId()


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
                    .body("compassBearing", comparesEqualTo(compassBearing))

        assertThat(entityChangedJMSListener.hasReceivedEvent(stopPlace.getNetexId(), stopPlace.getVersion() + 1, EntityChangedEvent.CrudAction.UPDATE)).isTrue()
    }

    /**
     * Test that reproduces NRP-1433
     *
     * @throws Exception
     */
    @Test
    void testSimpleMutationUpdateStopPlaceKeepPlaceEquipmentsOnQuay() throws Exception {

        StopPlace stopPlace = new StopPlace()
        stopPlace.setName(new EmbeddableMultilingualString("Espa"))
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)))
        stopPlace.setPlaceEquipments(createPlaceEquipments())

        Quay quay = new Quay()
        quay.setCompassBearing(new Float(90))
        quay.setCentroid(geometryFactory.createPoint(new Coordinate(11.2, 60.2)))
        quay.setPlaceEquipments(createPlaceEquipments())
        stopPlace.getQuays().add(quay)

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace)

        String name = "Testing name"
        String netexId = stopPlace.getNetexId()

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
                "        generalSign { id }" +
                "      }" +
                "    ... on StopPlace {" +
                "       quays {" +
                "       id" +
                "        placeEquipments {" +
                "         waitingRoomEquipment { id }" +
                "         sanitaryEquipment { id }" +
                "         ticketingEquipment { id }" +
                "         cycleStorageEquipment { id }" +
                "         shelterEquipment { id }" +
                "         generalSign { id }" +
                "        }" +
                "       }" +
                "   }" +
                "  }" +
                "}\",\"variables\":\"\"}"

        executeGraphQL(graphQlStopPlaceQuery)
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
                    .body("placeEquipments.generalSign[0]", notNullValue())

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
                "        generalSign { id }" +
                "      }" +
                "    quays {" +
                "      id" +
                "      placeEquipments {" +
                "        waitingRoomEquipment { id }" +
                "        sanitaryEquipment { id }" +
                "        ticketingEquipment { id }" +
                "        cycleStorageEquipment { id }" +
                "        shelterEquipment { id }" +
                "        generalSign { id }" +
                "      }" +
                "    }" +
                "  }" +
                "}\",\"variables\":\"\"}"

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
                    .body("placeEquipments.generalSign", notNullValue())
                .root("data.stopPlace[0].quays[0]")
                    .body("id", notNullValue())
                    .body("placeEquipments", notNullValue())
                    .body("placeEquipments.waitingRoomEquipment", notNullValue())
                    .body("placeEquipments.sanitaryEquipment", notNullValue())
                    .body("placeEquipments.ticketingEquipment", notNullValue())
                    .body("placeEquipments.cycleStorageEquipment", notNullValue())
                    .body("placeEquipments.shelterEquipment", notNullValue())
                    .body("placeEquipments.generalSign", notNullValue())

    }


    @Test
    void testSimpleSaveAlternativeNames() throws Exception {

        StopPlace stopPlace = new StopPlace()
        stopPlace.setName(new EmbeddableMultilingualString("Name"))
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)))

        AlternativeName altName = new AlternativeName()
        altName.setNameType(NameTypeEnumeration.ALIAS)
        altName.setName(new EmbeddableMultilingualString("Navn", "no"))

        AlternativeName altName2 = new AlternativeName()
        altName2.setNameType(NameTypeEnumeration.ALIAS)
        altName2.setName(new EmbeddableMultilingualString("Name", "en"))

        stopPlace.getAlternativeNames().add(altName)
        stopPlace.getAlternativeNames().add(altName2)

        Quay quay = new Quay()
        quay.setCompassBearing(new Float(90))
        quay.setCentroid(geometryFactory.createPoint(new Coordinate(11.2, 60.2)))
        quay.getAlternativeNames().add(altName)
        quay.getAlternativeNames().add(altName2)

        stopPlace.getQuays().add(quay)

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace)

        String name = "Testing name"
        String netexId = stopPlace.getNetexId()

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
                "  ... on StopPlace {" +
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
                "   }" +
                "  }" +
                "}\",\"variables\":\"\"}"

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

    }
    @Test
    <T extends Comparable<T>> void testSimpleMutateAlternativeNames() throws Exception {

        StopPlace stopPlace = new StopPlace()
        stopPlace.setName(new EmbeddableMultilingualString("Name"))
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)))

        AlternativeName altName = new AlternativeName()
        altName.setNameType(NameTypeEnumeration.ALIAS)
        altName.setName(new EmbeddableMultilingualString("Navn", "no"))

        stopPlace.getAlternativeNames().add(altName)

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace)

        String netexId = stopPlace.getNetexId()

        String updatedAlternativeNameValue = "UPDATED ALIAS"
        String updatedAlternativeNameLang = "no"

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
                "\",\"variables\":\"\"}"

        executeGraphQL(graphQlStopPlaceQuery)
                .body("data.stopPlace[0].id", comparesEqualTo(netexId))
                .body("data.stopPlace[0].alternativeNames", notNullValue())
                .root("data.stopPlace[0].alternativeNames[0]")
//                .body("nameType", equalTo(altName.getNameType())) //RestAssured apparently does not like comparing response with enums...
                .body("name.value", comparesEqualTo(updatedAlternativeNameValue))
                .body("name.lang", comparesEqualTo(updatedAlternativeNameLang))

    }


    @Test
    void testSimpleMutatePlaceEquipmentSignPrivateCode() throws Exception {

        StopPlace stopPlace = new StopPlace()
        stopPlace.setName(new EmbeddableMultilingualString("Name"))
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(11.1, 60.1)))

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace)

        String netexId = stopPlace.getNetexId()

        String type = "StopPoint"
        String value = "512"
        String graphQlStopPlaceQuery = "{" +
                "\"query\":\"mutation { " +
                "  stopPlace: " + GraphQLNames.MUTATE_STOPPLACE + " (StopPlace: {" +
                "      id:\\\"" + netexId + "\\\"" +
                "      placeEquipments: {" +
                "        generalSign:  [{" +
                "          signContentType: TransportModePoint" +
                "          privateCode: {" +
                "            value: \\\"" + value + "\\\"" +
                "            type:\\\"" + type + "\\\"" +
                "          }" +
                "        }]" +
                "      }" +
                "    }) " +
                "    {" +
                "      id" +
                "      placeEquipments {" +
                "        generalSign {" +
                "          privateCode { value, type } " +
                "          signContentType " +
                "        }" +
                "      }" +
                "    }" +
                "  }" +
                "\",\"variables\":\"\"}"

        executeGraphQL(graphQlStopPlaceQuery)
                .body("data.stopPlace[0].id", comparesEqualTo(netexId))
                .body("data.stopPlace[0].placeEquipments", notNullValue())
                .root("data.stopPlace[0].placeEquipments")
//                .body("nameType", equalTo(altName.getNameType())) //RestAssured apparently does not like comparing response with enums...
                .body("generalSign[0]", notNullValue())
                .body("generalSign[0].privateCode.type", comparesEqualTo(type))
                .body("generalSign[0].privateCode.value", comparesEqualTo(value))

    }


    private StopPlace createStopPlaceWithMunicipalityRef(String name, TopographicPlace municipality, StopTypeEnumeration type) {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(name))
        stopPlace.setStopPlaceType(type)
        if(municipality != null) {
            stopPlace.setTopographicPlace(municipality)
        }
        stopPlaceRepository.save(stopPlace)
        return stopPlace
    }

    private StopPlace createStopPlace(String name) {
        return createStopPlaceWithMunicipalityRef(name, null)
    }

    private StopPlace createStopPlaceWithMunicipalityRef(String name, TopographicPlace municipality) {
        return createStopPlaceWithMunicipalityRef(name, municipality, null)
    }

    private TopographicPlace createMunicipalityWithCountyRef(String name, TopographicPlace county) {
        TopographicPlace municipality = new TopographicPlace(new EmbeddableMultilingualString(name))
        municipality.setTopographicPlaceType(TopographicPlaceTypeEnumeration.TOWN)
        if(county != null) {
            municipality.setParentTopographicPlaceRef(new TopographicPlaceRefStructure(county))
        }
        topographicPlaceRepository.save(municipality)
        return municipality
    }


    private PlaceEquipment createPlaceEquipments() {
        PlaceEquipment equipments = new PlaceEquipment()

        ShelterEquipment leskur = new ShelterEquipment()
        leskur.setEnclosed(false)
        leskur.setSeats(BigInteger.valueOf(2))

        WaitingRoomEquipment venterom = new WaitingRoomEquipment()
        venterom.setSeats(BigInteger.valueOf(25))

        TicketingEquipment billettAutomat = new TicketingEquipment()
        billettAutomat.setTicketMachines(true)
        billettAutomat.setNumberOfMachines(BigInteger.valueOf(2))

        SanitaryEquipment toalett = new SanitaryEquipment()
        toalett.setNumberOfToilets(BigInteger.valueOf(2))

        CycleStorageEquipment sykkelstativ = new CycleStorageEquipment()
        sykkelstativ.setCycleStorageType(CycleStorageEnumeration.RACKS)
        sykkelstativ.setNumberOfSpaces(BigInteger.TEN)

        GeneralSign skilt = new GeneralSign()
        skilt.setSignContentType(SignContentEnumeration.TRANSPORT_MODE)
        PrivateCodeStructure privCode = new PrivateCodeStructure()
        privCode.setValue("512")
        skilt.setPrivateCode(privCode)

        equipments.getInstalledEquipment().add(venterom)
        equipments.getInstalledEquipment().add(billettAutomat)
        equipments.getInstalledEquipment().add(toalett)
        equipments.getInstalledEquipment().add(leskur)
        equipments.getInstalledEquipment().add(sykkelstativ)
        equipments.getInstalledEquipment().add(skilt)
        return equipments
    }

}

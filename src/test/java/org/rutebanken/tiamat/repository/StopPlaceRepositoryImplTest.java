package org.rutebanken.tiamat.repository;

import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.rutebanken.tiamat.CommonSpringBootTest;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Transactional
public class StopPlaceRepositoryImplTest extends CommonSpringBootTest {

    @Transactional(propagation = Propagation.NEVER)
    @Test
    public void scrollableResult() throws InterruptedException {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("new stop place to be savced and scrolled back"));
        stopPlace.setNetexId("NSR:StopPlace:123");
        stopPlace.getKeyValues().put("key", new Value("value"));

        Quay quay = new Quay(new EmbeddableMultilingualString("Quay"));
        stopPlace.getQuays().add(quay);

        stopPlace = stopPlaceRepository.save(stopPlace);
        stopPlaceRepository.flush();

        Iterator<StopPlace> iterator = stopPlaceRepository.scrollStopPlaces();
        assertThat(iterator.hasNext()).isTrue();
        StopPlace actual = iterator.next();
        assertThat(actual.getNetexId()).isEqualTo(stopPlace.getNetexId());
        assertThat(iterator.hasNext()).isFalse();

    }

    @Test
    public void findStopPlaceFromKeyValue() {
        StopPlace stopPlace = new StopPlace();

        stopPlace.getKeyValues().put("key", new Value("value"));
        stopPlaceRepository.save(stopPlace);

        String netexId = stopPlaceRepository.findByKeyValue("key", Sets.newHashSet("value"));
        StopPlace actual = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(netexId);
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getKeyValues()).containsKey("key");

        Assertions.assertThat(actual.getKeyValues().get("key").getItems()).contains("value");
    }

    @Test
    public void keyValuesForAddressablePlaceNoMixup() {
        Quay quay = new Quay();
        quay.getOrCreateValues("key").add("value");

        quayRepository.save(quay);

        StopPlace stopPlace = new StopPlace();
        stopPlace.getOrCreateValues("key").add("value");
        stopPlaceRepository.save(stopPlace);


        String netexId = stopPlaceRepository.findByKeyValue("key", Sets.newHashSet("value"));
        assertThat(netexId).isEqualTo(stopPlace.getNetexId());
        StopPlace actual = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(netexId);
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getKeyValues()).containsKey("key");

        Assertions.assertThat(actual.getKeyValues().get("key").getItems()).contains("value");
    }


    @Test
    public void noStopPlaceFromKeyValue() {
        StopPlace firstStopPlace = new StopPlace();
        firstStopPlace.getKeyValues().put("key", new Value("value"));
        stopPlaceRepository.save(firstStopPlace);

        String id = stopPlaceRepository.findByKeyValue("key", Sets.newHashSet("anotherValue"));
        assertThat(id).isNull();
    }

    @Test
    public void findCorrectStopPlaceFromKeyValue() {
        StopPlace anotherStopPlaceWithAnotherValue = new StopPlace();
        anotherStopPlaceWithAnotherValue.getKeyValues().put("key", new Value("anotherValue"));
        anotherStopPlaceWithAnotherValue.setNetexId("y");
        stopPlaceRepository.save(anotherStopPlaceWithAnotherValue);

        StopPlace matchingStopPlace = new StopPlace();
        matchingStopPlace.setNetexId("x");
        matchingStopPlace.getKeyValues().put("key", new Value("value"));
        stopPlaceRepository.save(matchingStopPlace);

        String id = stopPlaceRepository.findByKeyValue("key", Sets.newHashSet("value"));

        assertThat(id).isEqualTo(matchingStopPlace.getNetexId());

        StopPlace actual = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(id);
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getKeyValues()).containsKey("key");
        Assertions.assertThat(actual.getKeyValues().get("key").getItems()).contains("value");
    }

    @Test
    public void findCorrectStopPlaceFromValues() {
        StopPlace stopPlaceWithSomeValues = new StopPlace();
        stopPlaceWithSomeValues.getKeyValues().put("key", new Value("One value", "Second value", "Third value"));
        stopPlaceRepository.save(stopPlaceWithSomeValues);

        String id = stopPlaceRepository.findByKeyValue("key", Sets.newHashSet("Third value"));

        assertThat(id).isEqualTo(stopPlaceWithSomeValues.getNetexId());

        StopPlace actual = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(id);
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getKeyValues()).containsKey("key");
        Assertions.assertThat(actual.getKeyValues().get("key").getItems()).contains("Third value");
    }

    @Test
    public void findStopPlacesWithin() throws Exception {

        double southEastLatitude = 59.875649;
        double southEastLongitude = 10.500340;

        double northWestLatitude = 59.875924;
        double northWestLongitude = 10.500699;

        StopPlace stopPlace = createStopPlace(59.875679, 10.500430);
        stopPlaceRepository.save(stopPlace);

        Pageable pageable = new PageRequest(0, 10);

        Page<StopPlace> result = stopPlaceRepository.findStopPlacesWithin(southEastLongitude, southEastLatitude, northWestLongitude, northWestLatitude, null, pageable);
        assertThat(result.getContent()).extracting(EntityStructure::getNetexId).contains(stopPlace.getNetexId());
    }

    @Test
    public void findStopPlacesWithinMaxVersion() throws Exception {

        double southEastLatitude = 59.875649;
        double southEastLongitude = 10.500340;

        double northWestLatitude = 59.875924;
        double northWestLongitude = 10.500699;

        StopPlace version1 = createStopPlace(59.875679, 10.500430);
        version1.setVersion(1L);
        version1.setNetexId("NSR:StopPlace:977777");
        StopPlace version2 = createStopPlace(59.875679, 10.500430);
        version2.setVersion(2L);
        version2.setNetexId(version1.getNetexId());
        stopPlaceRepository.save(version1);
        stopPlaceRepository.save(version2);

        Pageable pageable = new PageRequest(0, 10);

        Page<StopPlace> result = stopPlaceRepository.findStopPlacesWithin(southEastLongitude, southEastLatitude, northWestLongitude, northWestLatitude, null, pageable);
        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getVersion()).isEqualTo(version2.getVersion());

    }

    @Test
    public void findStopPlaceWithinNoStopsInBoundingBox() throws Exception {
        double southEastLatitude = 59.875649;
        double southEastLongitude = 10.500340;

        double northWestLatitude = 59.875924;
        double northWestLongitude = 10.500699;

        // Outside boundingBox
        StopPlace stopPlace = createStopPlace(60.00, 11);
        Pageable pageable = new PageRequest(0, 10);

        stopPlaceRepository.save(stopPlace);

        Page<StopPlace> result = stopPlaceRepository.findStopPlacesWithin(southEastLongitude, southEastLatitude, northWestLongitude, northWestLatitude, null, pageable);

        assertThat(result.getContent()).extracting(IdentifiedEntity::getNetexId).doesNotContain(stopPlace.getNetexId());
    }

    @Test
    public void findStopPlaceWithinIgnoringStopPlace() throws Exception {
        double southEastLatitude = 59;
        double southEastLongitude = 10;

        double northWestLatitude = 60;
        double northWestLongitude = 11;

        StopPlace stopPlace = createStopPlace(59.5, 10.5);
        Pageable pageable = new PageRequest(0, 10);

        stopPlaceRepository.save(stopPlace);

        Page<StopPlace> result = stopPlaceRepository.findStopPlacesWithin(southEastLongitude, southEastLatitude, northWestLongitude, northWestLatitude, stopPlace.getNetexId(), pageable);

        assertThat(result.getContent())
                .extracting(IdentifiedEntity::getNetexId)
                .as("Ignored stop place shall not be part of the result")
                .doesNotContain(stopPlace.getNetexId());
    }

    @Test
    public void findStopPlacesWithinIgnoringStopPlaceButOtherShouldMatch() throws Exception {

        double southEastLatitude = 59;
        double southEastLongitude = 10;

        double northWestLatitude = 60;
        double northWestLongitude = 11;

        StopPlace ignoredStopPlace = createStopPlace(59.5, 10.5);
        stopPlaceRepository.save(ignoredStopPlace);

        StopPlace otherStopPlace = createStopPlace(59.5, 10.5);
        stopPlaceRepository.save(otherStopPlace);

        Pageable pageable = new PageRequest(0, 10);

        Page<StopPlace> result = stopPlaceRepository.findStopPlacesWithin(southEastLongitude, southEastLatitude, northWestLongitude, northWestLatitude, ignoredStopPlace.getNetexId(), pageable);

        assertThat(result.getContent())
                .extracting(IdentifiedEntity::getNetexId)
                .as("Ignored stop place shall not be part of the result")
                .doesNotContain(ignoredStopPlace.getNetexId())
                .contains(otherStopPlace.getNetexId());
    }


    @Test
    public void findNearbyStopPlace() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("name", ""));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);

        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(10.500430, 59.875679)));
        stopPlaceRepository.save(stopPlace);

        Envelope envelope = new Envelope(10.500340, 59.875649, 10.500699, 59.875924);

        String result = stopPlaceRepository.findNearbyStopPlace(envelope, stopPlace.getName().getValue(), StopTypeEnumeration.ONSTREET_BUS);
        assertThat(result).isNotNull();
        StopPlace actual = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(result);
        assertThat(actual.getName().getValue()).isEqualTo(stopPlace.getName().getValue());
    }

    @Test
    public void noNearbyStopPlace() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("stop place", ""));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(15, 60)));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlaceRepository.save(stopPlace);

        Envelope envelope = new Envelope(10.500340, 59.875649, 10.500699, 59.875924);

        String result = stopPlaceRepository.findNearbyStopPlace(envelope, stopPlace.getName().getValue(), StopTypeEnumeration.ONSTREET_BUS);
        assertThat(result).isNull();
    }

    @Test
    public void noNearbyStopPlaceIfNameIsDifferent() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("This name is different", ""));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(15, 60)));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        stopPlaceRepository.save(stopPlace);

        // Stop place coordinates within envelope
        Envelope envelope = new Envelope(14, 16, 50, 70);

        String result = stopPlaceRepository.findNearbyStopPlace(envelope, "Another stop place which does not exist", StopTypeEnumeration.ONSTREET_BUS);
        assertThat(result).isNull();
    }

    @Test
    public void multipleNearbyStopPlaces() throws Exception {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("name"));
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(15, 60)));
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        StopPlace stopPlace2 = new StopPlace(new EmbeddableMultilingualString("name"));
        stopPlace2.setCentroid(geometryFactory.createPoint(new Coordinate(15.0001, 60.0002)));

        stopPlaceRepository.save(stopPlace);
        stopPlaceRepository.save(stopPlace2);

        // Stop place coordinates within envelope
        Envelope envelope = new Envelope(14, 16, 50, 70);

        String result = stopPlaceRepository.findNearbyStopPlace(envelope, "name", StopTypeEnumeration.ONSTREET_BUS);
        assertThat(result).isNotNull();
    }

    @Test
    public void findStopPlaceByMunicipalityAndTypeBusThenExpectNoResult() {
        String stopPlaceName = "Falsens plass";
        String municipalityName = "Gjøvik";
        TopographicPlace municipality = createMunicipality(municipalityName, null);
        StopPlace stopPlace = createStopPlaceWithMunicipality(stopPlaceName, municipality);
        stopPlace.setStopPlaceType(StopTypeEnumeration.AIRPORT);
        stopPlaceRepository.save(stopPlace);
        Pageable pageable = new PageRequest(0, 10);

        List<StopTypeEnumeration> stopTypeEnumerations = Arrays.asList(StopTypeEnumeration.BUS_STATION);

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(new StopPlaceSearch.Builder()
                .setQuery(stopPlaceName)
                .setMunicipalityIds(Arrays.asList(stopPlace.getTopographicPlace().getNetexId().toString()))
                .setStopTypeEnumerations(stopTypeEnumerations)
                .setPageable(pageable)
                .build());
        assertThat(result).isEmpty();
    }

    @Test
    public void findStopPlaceByMunicipalityAndName() throws Exception {
        String stopPlaceName = "Nesbru";
        String municipalityName = "Asker";
        TopographicPlace municipality = createMunicipality(municipalityName, null);
        StopPlace stopPlace = createStopPlaceWithMunicipality(stopPlaceName, municipality);
        stopPlaceRepository.save(stopPlace);
        Pageable pageable = new PageRequest(0, 10);

        StopPlaceSearch search = new StopPlaceSearch.Builder()
                .setQuery(stopPlaceName)
                .setMunicipalityIds(Arrays.asList(stopPlace.getTopographicPlace().getNetexId().toString()))
                .setPageable(pageable).build();
        Page<StopPlace> result = stopPlaceRepository.findStopPlace(search);
        assertThat(result).isNotEmpty();
        System.out.println(result.getContent().get(0));
    }

    @Test
    public void searchingForThreeLettersMustOnlyReturnNamesStartingWithLetters() throws Exception {
        StopPlace nesbru = createStopPlaceWithMunicipality("Nesbru", null);
        StopPlace bru = createStopPlaceWithMunicipality("Bru", null);

        stopPlaceRepository.save(nesbru);
        stopPlaceRepository.save(bru);
        StopPlaceSearch search = new StopPlaceSearch.Builder()
                .setQuery("bru").build();

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(search);
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).extracting(stop -> stop.getName().getValue())
                .contains(bru.getName().getValue())
                .doesNotContain(nesbru.getName().getValue());
    }

    @Test
    public void searchingForMoreThanThreeLettersMustReturnNamesContainingLetters() throws Exception {

        StopPlace nesset = createStopPlaceWithMunicipality("Nesset", null);
        StopPlace brunesset = createStopPlaceWithMunicipality("Brunesset", null);

        stopPlaceRepository.save(nesset);
        stopPlaceRepository.save(brunesset);

        StopPlaceSearch search = new StopPlaceSearch.Builder()
                .setQuery("nesset").build();
        Page<StopPlace> result = stopPlaceRepository.findStopPlace(search);
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).extracting(stop -> stop.getName().getValue())
                .contains(brunesset.getName().getValue())
                .contains(nesset.getName().getValue());
    }

    @Test
    public void findStopPlaceByMunicipalityCountyAndName() throws Exception {
        String stopPlaceName = "Bergerveien";
        String municipalityName = "Asker";
        String countyName = "Akershus";

        TopographicPlace county = createCounty(countyName);
        TopographicPlace municipality = createMunicipality(municipalityName, county);
        createStopPlaceWithMunicipality(stopPlaceName, municipality);

        Pageable pageable = new PageRequest(0, 10);

        StopPlaceSearch search = new StopPlaceSearch.Builder()
                .setQuery(stopPlaceName)
                .setMunicipalityIds(Arrays.asList(municipality.getNetexId().toString()))
                .setCountyIds(Arrays.asList(county.getNetexId().toString()))
                .setPageable(pageable)
                .build();
        Page<StopPlace> result = stopPlaceRepository.findStopPlace(search);
        assertThat(result).isNotEmpty();
        System.out.println(result.getContent().get(0));
    }

    @Test
    public void findStopPlaceByCountyAndName() throws Exception {
        String stopPlaceName = "IKEA Slependen";
        String municipalityName = "Asker";
        String countyName = "Akershus";

        TopographicPlace county = createCounty(countyName);
        TopographicPlace municipality = createMunicipality(municipalityName, county);
        createStopPlaceWithMunicipality(stopPlaceName, municipality);

        StopPlaceSearch search = new StopPlaceSearch.Builder()
                .setQuery(stopPlaceName)
                .setCountyIds(Arrays.asList(county.getNetexId().toString()))
                .build();

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(search);
        assertThat(result).isNotEmpty();
        System.out.println(result.getContent().get(0));
    }

    @Test
    public void findStopPlaceByCounties() throws Exception {
        String stopPlaceName = "Slependen";
        String municipalityName = "Bærum";
        String countyName = "Akershus";

        TopographicPlace akershus = createCounty(countyName);
        TopographicPlace municipality = createMunicipality(municipalityName, akershus);
        StopPlace stopPlace = createStopPlaceWithMunicipality(stopPlaceName, municipality);
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        stopPlaceRepository.save(stopPlace);

        TopographicPlace buskerud = createCounty("Buskerud");

        List<String> countyRefs = Arrays.asList(buskerud.getNetexId().toString());
        List<String> municipalityRefs = Arrays.asList(municipality.getNetexId().toString());

        StopPlaceSearch search = new StopPlaceSearch.Builder()
                .setQuery(stopPlaceName)
                .setMunicipalityIds(municipalityRefs)
                .setCountyIds(countyRefs)
                .setStopTypeEnumerations(Arrays.asList(StopTypeEnumeration.BUS_STATION))
                .build();

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(search);
        assertThat(result).isNotEmpty();
        assertThat(result).extracting(actual -> actual.getNetexId()).contains(stopPlace.getNetexId());
    }

    @Test
    public void findStopPlacNameContainsIgnoreCase() throws Exception {
        String stopPlaceName = "IKEA Slependen";

        createStopPlaceWithMunicipality(stopPlaceName, null);

        StopPlaceSearch search = new StopPlaceSearch.Builder()
                .setQuery("lEpEnden")
                .build();
        Page<StopPlace> result = stopPlaceRepository.findStopPlace(search);
        assertThat(result).isNotEmpty();
        System.out.println(result.getContent().get(0));
    }

    /**
     * Expect no result beacuse stop type is not matching.
     * https://test.rutebanken.org/api/tiamat/1.0/stop_place/?stopPlaceType=onstreetBus&countyReference=33&municipalityReference=2&
     */
    @Test
    public void findStopPlaceByCountyAndMunicipalityAndStopPlaceType() throws Exception {
        String municipalityName = "Asker";
        String countyName = "Akershus";

        TopographicPlace county = createCounty(countyName);
        TopographicPlace municipality = createMunicipality(municipalityName, county);
        createStopPlaceWithMunicipality("Does not matter", municipality);

        StopPlaceSearch search = new StopPlaceSearch.Builder()
                .setMunicipalityIds(Arrays.asList(municipality.getNetexId().toString()))
                .setCountyIds(Arrays.asList(county.getNetexId().toString()))
                .setStopTypeEnumerations(Arrays.asList(StopTypeEnumeration.COACH_STATION))
                .build();

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(search);

        assertThat(result).isEmpty();
    }

    /**
     * Expect no result because name should be anded with other parts of query
     * https://test.rutebanken.org/api/tiamat/1.0/stop_place/?q=lomsdalen&municipalityReference=2&countyReference=33
     */
    @Test
    public void findStopPlaceByCountyAndMunicipalityAndNameExpectNoResult() throws Exception {
        String municipalityName = "Asker";
        String countyName = "Akershus";

        TopographicPlace county = createCounty(countyName);
        TopographicPlace municipality = createMunicipality(municipalityName, county);
        createStopPlaceWithMunicipality("XYZ", municipality);


        StopPlaceSearch search = new StopPlaceSearch.Builder()
                .setQuery("Name")
                .setMunicipalityIds(Arrays.asList(municipality.getNetexId().toString()))
                .setCountyIds(Arrays.asList(county.getNetexId().toString()))
                .build();

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(search);

        System.out.println(result);
        assertThat(result).isEmpty();
    }

    @Test
    public void findStopPlaceByCountyAndNameThenExpectEmptyResult() throws Exception {
        String municipalityName = "Asker";
        String countyName = "Akershus";

        TopographicPlace county = createCounty(countyName);
        TopographicPlace municipality = createMunicipality(municipalityName, county);
        createStopPlaceWithMunicipality("No matching stop name", municipality);

        StopPlaceSearch search = new StopPlaceSearch.Builder()
                .setQuery("Somewhere else")
                .setCountyIds(Arrays.asList(county.getNetexId().toString()))
                .build();
        Page<StopPlace> result = stopPlaceRepository.findStopPlace(search);
        assertThat(result).isEmpty();
    }

    @Test
    public void findStopPlaceByMunicipalityAndNameAndExpectEmptyResult() throws Exception {
        TopographicPlace municipality = createMunicipality("Asker", createCounty("Akershus"));
        createStopPlaceWithMunicipality("No matching stop name", municipality);

        StopPlaceSearch search = new StopPlaceSearch.Builder()
                .setQuery("Somewhere else")
                .setMunicipalityIds(Arrays.asList(municipality.getNetexId().toString()))
                .build();
        Page<StopPlace> result = stopPlaceRepository.findStopPlace(search);
        assertThat(result).isEmpty();
    }

    @Test
    public void findStopPlacesByListOfIds() throws Exception {

        StopPlace stopPlace1 = new StopPlace();
        stopPlace1.setNetexId("1");
        stopPlaceRepository.save(stopPlace1);

        StopPlace stopPlace2 = new StopPlace();
        stopPlace2.setNetexId("2");
        stopPlaceRepository.save(stopPlace2);

        StopPlace stopPlaceThatShouldNotBeReturned = new StopPlace();
        stopPlaceRepository.save(stopPlaceThatShouldNotBeReturned);

        List<String> stopPlaceIds = Arrays.asList(stopPlace1.getNetexId(), stopPlace2.getNetexId());

        StopPlaceSearch stopPlaceSearch = new StopPlaceSearch.Builder().setNetexIdList(stopPlaceIds).build();

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(stopPlaceSearch);
        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(StopPlace::getNetexId)
                .contains(stopPlace1.getNetexId(), stopPlace2.getNetexId())
                .doesNotContain(stopPlaceThatShouldNotBeReturned.getNetexId());
    }

    @Test
    public void emptyIdListShouldReturnStops() throws Exception {

        StopPlace stopPlace1 = new StopPlace();
        stopPlaceRepository.save(stopPlace1);

        StopPlace stopPlace2 = new StopPlace();
        stopPlaceRepository.save(stopPlace2);

        List<String> stopPlaceIds = new ArrayList<>();

        StopPlaceSearch stopPlaceSearch = new StopPlaceSearch.Builder().setNetexIdList(stopPlaceIds).build();

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(stopPlaceSearch);
        assertThat(result).isNotEmpty();
    }


    @Test
    public void searchingForIdListShouldNotUseQueryMunicipalityOrCounty() throws Exception {

        TopographicPlace county = createCounty("Hedmark");
        TopographicPlace municipality = createMunicipality("Hamar", county);
        createStopPlaceWithMunicipality("FromMunicipality", municipality);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString("OnlyThis"));
        stopPlaceRepository.save(stopPlace);

        List<String> stopPlaceIds = new ArrayList<>();
        stopPlaceIds.add(stopPlace.getNetexId());

        StopPlaceSearch stopPlaceSearch = new StopPlaceSearch.Builder()
                .setQuery("FromMu")
                .setNetexIdList(stopPlaceIds)
                .setMunicipalityIds(Arrays.asList(municipality.getNetexId().toString()))
                .setCountyIds(Arrays.asList(county.getNetexId().toString()))
                .build();

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(stopPlaceSearch);
        assertThat(result).extracting(StopPlace::getName).extracting(EmbeddableMultilingualString::getValue)
                .contains("OnlyThis")
                .doesNotContain("FromMunicipality");
    }

    @Test
    public void findStopPlaceByTypeAirport() {
        StopPlace stopPlace = new StopPlace();

        stopPlace.setStopPlaceType(StopTypeEnumeration.AIRPORT);

        stopPlaceRepository.save(stopPlace);

        StopPlaceSearch search = new StopPlaceSearch.Builder()
                .setStopTypeEnumerations(Arrays.asList(StopTypeEnumeration.AIRPORT))
                .build();
        Page<StopPlace> result = stopPlaceRepository.findStopPlace(search);

        Assertions.assertThat(result).isNotEmpty();
    }

    @Test
    public void findOnlyMaxVersion() {
        StopPlace versionOne = new StopPlace();
        versionOne.setVersion(1L);
        versionOne.setNetexId("NSR:StopPlace:999");
        StopPlace versionTwo = new StopPlace();
        versionTwo.setVersion(2L);
        versionTwo.setNetexId(versionOne.getNetexId());

        versionOne = stopPlaceRepository.save(versionOne);
        versionTwo = stopPlaceRepository.save(versionTwo);

        StopPlaceSearch stopPlaceSearch = new StopPlaceSearch.Builder()
                .setNetexIdList(Arrays.asList(versionOne.getNetexId()))
                .build();

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(stopPlaceSearch);

        assertThat(result)
                .describedAs("Expecting only one stop place in return. Because only the highest version should be returned.")
                .hasSize(1);

    }

    private TopographicPlace createMunicipality(String municipalityName, TopographicPlace parentCounty) {
        TopographicPlace municipality = new TopographicPlace();
        municipality.setName(new EmbeddableMultilingualString(municipalityName, ""));

        if (parentCounty != null) {
            municipality.setParentTopographicPlace(parentCounty);
        }

        topographicPlaceRepository.save(municipality);
        return municipality;
    }

    private TopographicPlace createCounty(String countyName) {

        TopographicPlace county = new TopographicPlace();
        county.setName(new EmbeddableMultilingualString(countyName, ""));
        topographicPlaceRepository.save(county);

        return county;
    }

    private StopPlace createStopPlaceWithMunicipality(String name, TopographicPlace municipality) {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new EmbeddableMultilingualString(name, ""));

        if (municipality != null) {
            stopPlace.setTopographicPlace(municipality);
        }

        stopPlaceRepository.save(stopPlace);

        return stopPlace;
    }

    private StopPlace createStopPlace(double latitude, double longitude) {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setCentroid(geometryFactory.createPoint(new Coordinate(longitude, latitude)));
        return stopPlace;
    }
}

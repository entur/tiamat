package org.rutebanken.tiamat.repository;

import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.junit.Before;
import org.rutebanken.tiamat.TiamatApplication;
import org.rutebanken.tiamat.model.*;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.swing.plaf.multi.MultiButtonUI;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
@ActiveProfiles("geodb")
public class StopPlaceRepositoryImplTest {

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private GeometryFactory geometryFactory;

    @Before
    public void before() {
        stopPlaceRepository.deleteAll();
        topographicPlaceRepository.deleteAll();
    }

    @Test
    public void findStopPlaceFromKeyValue() {
        StopPlace stopPlace = new StopPlace();

        stopPlace.getKeyValues().put("key", new Value("value"));
        stopPlaceRepository.save(stopPlace);

        Long id = stopPlaceRepository.findByKeyValue("key", Sets.newHashSet("value"));
        StopPlace actual = stopPlaceRepository.findOne(id);
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getKeyValues()).containsKey("key");

        Assertions.assertThat(actual.getKeyValues().get("key").getItems()).contains("value");
    }

    @Test
    public void noStopPlaceFromKeyValue() {
        StopPlace firstStopPlace = new StopPlace();
        firstStopPlace.getKeyValues().put("key", new Value("value"));
        stopPlaceRepository.save(firstStopPlace);

        Long id = stopPlaceRepository.findByKeyValue("key", Sets.newHashSet("anotherValue"));
        assertThat(id).isNull();
    }

    @Test
    public void findCorrectStopPlaceFromKeyValue() {
        StopPlace anotherStopPlaceWithAnotherValue = new StopPlace();
        anotherStopPlaceWithAnotherValue.getKeyValues().put("key", new Value("anotherValue"));
        stopPlaceRepository.save(anotherStopPlaceWithAnotherValue);

        StopPlace matchingStopPlace = new StopPlace();
        matchingStopPlace.getKeyValues().put("key", new Value("value"));
        stopPlaceRepository.save(matchingStopPlace);

        Long id = stopPlaceRepository.findByKeyValue("key", Sets.newHashSet("value"));

        assertThat(id).isEqualTo(matchingStopPlace.getId());

        StopPlace actual = stopPlaceRepository.findOne(id);
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getKeyValues()).containsKey("key");
        Assertions.assertThat(actual.getKeyValues().get("key").getItems()).contains("value");
    }

    @Test
    public void findCorrectStopPlaceFromValues() {
        StopPlace stopPlaceWithSomeValues = new StopPlace();
        stopPlaceWithSomeValues.getKeyValues().put("key", new Value("One value", "Second value", "Third value"));
        stopPlaceRepository.save(stopPlaceWithSomeValues);

        Long id = stopPlaceRepository.findByKeyValue("key", Sets.newHashSet("Third value"));

        assertThat(id).isEqualTo(stopPlaceWithSomeValues.getId());

        StopPlace actual = stopPlaceRepository.findOne(id);
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
        assertThat(result.getContent()).extracting(EntityStructure::getId).contains(stopPlace.getId());
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

        assertThat(result.getContent()).extracting(EntityStructure::getId).doesNotContain(stopPlace.getId());
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

        Page<StopPlace> result = stopPlaceRepository.findStopPlacesWithin(southEastLongitude, southEastLatitude, northWestLongitude, northWestLatitude, stopPlace.getId(), pageable);

        assertThat(result.getContent())
                .extracting(EntityStructure::getId)
                .as("Ignored stop place shall not be part of the result")
                .doesNotContain(stopPlace.getId());
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

        Page<StopPlace> result = stopPlaceRepository.findStopPlacesWithin(southEastLongitude, southEastLatitude, northWestLongitude, northWestLatitude, ignoredStopPlace.getId(), pageable);

        assertThat(result.getContent())
                .extracting(EntityStructure::getId)
                .as("Ignored stop place shall not be part of the result")
                .doesNotContain(ignoredStopPlace.getId())
                .contains(otherStopPlace.getId());
    }


    @Test
    public void findNearbyStopPlace() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new MultilingualString("name", "", ""));
        SimplePoint centroid = new SimplePoint();

        centroid.setLocation(new LocationStructure(geometryFactory.createPoint(new Coordinate(10.500430, 59.875679))));

        stopPlace.setCentroid(centroid);
        stopPlaceRepository.save(stopPlace);

        Envelope envelope = new Envelope(10.500340, 59.875649, 10.500699, 59.875924);

        Long result = stopPlaceRepository.findNearbyStopPlace(envelope, stopPlace.getName().getValue());
        assertThat(result).isNotNull();
        StopPlace actual = stopPlaceRepository.findOne(result);
        assertThat(actual.getName().getValue()).isEqualTo(stopPlace.getName().getValue());
    }

    @Test
    public void noNearbyStopPlace() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new MultilingualString("stop place", "", ""));
        SimplePoint centroid = new SimplePoint();

        centroid.setLocation(new LocationStructure(geometryFactory.createPoint(new Coordinate(15, 60))));

        stopPlace.setCentroid(centroid);
        stopPlaceRepository.save(stopPlace);

        Envelope envelope = new Envelope(10.500340, 59.875649, 10.500699, 59.875924);

        Long result = stopPlaceRepository.findNearbyStopPlace(envelope, stopPlace.getName().getValue());
        assertThat(result).isNull();
    }

    @Test
    public void noNearbyStopPlaceIfNameIsDifferent() throws Exception {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new MultilingualString("This name is different", "", ""));
        SimplePoint centroid = new SimplePoint();
        centroid.setLocation(new LocationStructure(geometryFactory.createPoint(new Coordinate(15, 60))));

        stopPlace.setCentroid(centroid);
        stopPlaceRepository.save(stopPlace);

        // Stop place coordinates within envelope
        Envelope envelope = new Envelope(14, 16, 50, 70);

        Long result = stopPlaceRepository.findNearbyStopPlace(envelope, "Another stop place which does not exist");
        assertThat(result).isNull();
    }

    @Test
    public void multipleNearbyStopPlaces() throws Exception {
        StopPlace stopPlace = new StopPlace(new MultilingualString("name"));
        stopPlace.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(15, 60)))));

        StopPlace stopPlace2 = new StopPlace(new MultilingualString("name"));
        stopPlace2.setCentroid(new SimplePoint(new LocationStructure(geometryFactory.createPoint(new Coordinate(15.0001, 60.0002)))));

        stopPlaceRepository.save(stopPlace);
        stopPlaceRepository.save(stopPlace2);

        // Stop place coordinates within envelope
        Envelope envelope = new Envelope(14, 16, 50, 70);

        Long result = stopPlaceRepository.findNearbyStopPlace(envelope, "name");
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

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(stopPlaceName, Arrays.asList(stopPlace.getTopographicPlaceRef().getRef()), null, stopTypeEnumerations, pageable);
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

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(stopPlaceName, Arrays.asList(stopPlace.getTopographicPlaceRef().getRef()), null, null, pageable);
        assertThat(result).isNotEmpty();
        System.out.println(result.getContent().get(0));
    }

    @Test
    public void searchingForThreeLettersMustOnlyReturnNamesStartingWithLetters() throws Exception {
        StopPlace nesbru = createStopPlaceWithMunicipality("Nesbru", null);
        StopPlace bru = createStopPlaceWithMunicipality("Bru", null);

        stopPlaceRepository.save(nesbru);
        stopPlaceRepository.save(bru);

        Page<StopPlace> result = stopPlaceRepository.findStopPlace("bru", null, null, null, new PageRequest(0, 10));
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

        Page<StopPlace> result = stopPlaceRepository.findStopPlace("nesset", null, null, null, new PageRequest(0, 10));
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

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(stopPlaceName, Arrays.asList(municipality.getId().toString()), Arrays.asList(county.getId().toString()), null, pageable);
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

        Pageable pageable = new PageRequest(0, 10);

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(stopPlaceName, null, Arrays.asList(county.getId().toString()), null, pageable);
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

        List<String> countyRefs = Arrays.asList(buskerud.getId().toString());
        List<String> municipalityRefs = Arrays.asList(municipality.getId().toString());

        Pageable pageable = new PageRequest(0, 10);

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(stopPlaceName, municipalityRefs, countyRefs, Arrays.asList(StopTypeEnumeration.BUS_STATION), pageable);
        assertThat(result).isNotEmpty();
        assertThat(result).extracting(actual -> actual.getId()).contains(stopPlace.getId());
    }

    @Test
    public void findStopPlacNameContainsIgnoreCase() throws Exception {
        String stopPlaceName = "IKEA Slependen";

        Pageable pageable = new PageRequest(0, 10);

        createStopPlaceWithMunicipality(stopPlaceName, null);
        Page<StopPlace> result = stopPlaceRepository.findStopPlace("lEpEnden", null, null, null, pageable);
        assertThat(result).isNotEmpty();
        System.out.println(result.getContent().get(0));
    }

    /**
     * Expect no result beacuse stop type is not matching.
     * https://test.rutebanken.org/apiman-gateway/rutebanken/tiamat/1.0/stop_place/?stopPlaceType=onstreetBus&countyReference=33&municipalityReference=2&
     */
    @Test
    public void findStopPlaceByCountyAndMunicipalityAndStopPlaceType() throws Exception {
        String municipalityName = "Asker";
        String countyName = "Akershus";

        TopographicPlace county = createCounty(countyName);
        TopographicPlace municipality = createMunicipality(municipalityName, county);
        createStopPlaceWithMunicipality("Does not matter", municipality);

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(null,
                Arrays.asList(municipality.getId().toString()),
                Arrays.asList(county.getId().toString()),
                Arrays.asList(StopTypeEnumeration.COACH_STATION),
                new PageRequest(0, 1));

        assertThat(result).isEmpty();
    }

    /**
     * Expect no result because name should be anded with other parts of query
     * https://test.rutebanken.org/apiman-gateway/rutebanken/tiamat/1.0/stop_place/?q=lomsdalen&municipalityReference=2&countyReference=33
     */
    @Test
    public void findStopPlaceByCountyAndMunicipalityAndNameExpectNoResult() throws Exception {
        String municipalityName = "Asker";
        String countyName = "Akershus";

        TopographicPlace county = createCounty(countyName);
        TopographicPlace municipality = createMunicipality(municipalityName, county);
        createStopPlaceWithMunicipality("XYZ", municipality);

        Page<StopPlace> result = stopPlaceRepository.findStopPlace("Name",
                Arrays.asList(municipality.getId().toString()),
                Arrays.asList(county.getId().toString()),
                null,
                new PageRequest(0, 1));

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

        Pageable pageable = new PageRequest(0, 10);

        Page<StopPlace> result = stopPlaceRepository.findStopPlace("Somewhere else", null, Arrays.asList(county.getId().toString()), null, pageable);
        assertThat(result).isEmpty();
    }

    @Test
    public void findStopPlaceByMunicipalityAndNameAndExpectEmptyResult() throws Exception {
        TopographicPlace municipality = createMunicipality("Asker", createCounty("Akershus"));
        createStopPlaceWithMunicipality("No matching stop name", municipality);

        Pageable pageable = new PageRequest(0, 10);

        Page<StopPlace> result = stopPlaceRepository.findStopPlace("Somewhere else", Arrays.asList(municipality.getId().toString()), null, null, pageable);
        assertThat(result).isEmpty();
    }

    @Test
    public void findStopPlaceByTypeAirport() {
        StopPlace stopPlace = new StopPlace();

        stopPlace.setStopPlaceType(StopTypeEnumeration.AIRPORT);

        stopPlaceRepository.save(stopPlace);
        Page<StopPlace> actual = stopPlaceRepository.findStopPlace(null, null, null, Arrays.asList(StopTypeEnumeration.AIRPORT), new PageRequest(0,1));
        Assertions.assertThat(actual).isNotEmpty();
    }

    private TopographicPlace createMunicipality(String municipalityName, TopographicPlace parentCounty) {
        TopographicPlace municipality = new TopographicPlace();
        municipality.setName(new MultilingualString(municipalityName, "", ""));

        if(parentCounty != null) {
            TopographicPlaceRefStructure countyRef = new TopographicPlaceRefStructure();
            countyRef.setRef(parentCounty.getId().toString());
            municipality.setParentTopographicPlaceRef(countyRef);
        }

        topographicPlaceRepository.save(municipality);
        return municipality;
    }

    private TopographicPlace createCounty(String countyName) {

        TopographicPlace county = new TopographicPlace();
        county.setName(new MultilingualString(countyName, "", ""));
        topographicPlaceRepository.save(county);

        return county;
    }

    private StopPlace createStopPlaceWithMunicipality(String name, TopographicPlace municipality) {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new MultilingualString(name, "", ""));

        if(municipality != null) {
            TopographicPlaceRefStructure municipalityRef = new TopographicPlaceRefStructure();
            municipalityRef.setRef(municipality.getId().toString());
            stopPlace.setTopographicPlaceRef(municipalityRef);
        }

        stopPlaceRepository.save(stopPlace);

        return stopPlace;
    }



    private StopPlace createStopPlace(double latitude, double longitude) {
        StopPlace stopPlace = new StopPlace();
        SimplePoint centroid = new SimplePoint();
        centroid.setLocation(new LocationStructure(geometryFactory.createPoint(new Coordinate(longitude, latitude))));
        stopPlace.setCentroid(centroid);
        return stopPlace;
    }
}
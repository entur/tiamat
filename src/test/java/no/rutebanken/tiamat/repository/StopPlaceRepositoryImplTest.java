package no.rutebanken.tiamat.repository;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import no.rutebanken.tiamat.TiamatApplication;
import no.rutebanken.tiamat.model.*;
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

    @Test
    public void findStopPlaceFromKeyList() {
        StopPlace stopPlace = new StopPlace();

        KeyListStructure keyListStructure = new KeyListStructure();
        keyListStructure.getKeyValue().add(new KeyValueStructure("key", "value"));
        stopPlace.setKeyList(keyListStructure);

        stopPlaceRepository.save(stopPlace);

        Long id = stopPlaceRepository.findByKeyValue("key", "value");
        StopPlace actual = stopPlaceRepository.findOne(id);
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getKeyList().getKeyValue().get(0).getKey()).isEqualTo("key");
        Assertions.assertThat(actual.getKeyList().getKeyValue().get(0).getValue()).isEqualTo("value");

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
    public void findStopPlaceByMunicipalityAndName() throws Exception {
        String stopPlaceName = "Nesbru";
        String municipalityName = "Asker";
        TopographicPlace municipality = createMunicipality(municipalityName, null);
        StopPlace stopPlace = createStopPlaceWithMunicipality(stopPlaceName, municipality);
        stopPlaceRepository.save(stopPlace);
        Pageable pageable = new PageRequest(0, 10);

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(stopPlaceName, Long.valueOf(stopPlace.getTopographicPlaceRef().getRef()), null, pageable);
        assertThat(result).isNotEmpty();
        System.out.println(result.getContent().get(0));
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

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(stopPlaceName, municipality.getId(), county.getId(), pageable);
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

        Page<StopPlace> result = stopPlaceRepository.findStopPlace(stopPlaceName, null, county.getId(), pageable);
        assertThat(result).isNotEmpty();
        System.out.println(result.getContent().get(0));
    }

    @Test
    public void findStopPlaceByCountyAndNameAndExpectEmptyResult() throws Exception {
        String municipalityName = "Asker";
        String countyName = "Akershus";

        TopographicPlace county = createCounty(countyName);
        TopographicPlace municipality = createMunicipality(municipalityName, county);
        createStopPlaceWithMunicipality("Somewhere", municipality);

        Pageable pageable = new PageRequest(0, 10);

        Page<StopPlace> result = stopPlaceRepository.findStopPlace("Somewhere else", null, county.getId(), pageable);
        assertThat(result).isEmpty();
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

        TopographicPlaceRefStructure municipalityRef = new TopographicPlaceRefStructure();
        municipalityRef.setRef(municipality.getId().toString());
        stopPlace.setTopographicPlaceRef(municipalityRef);

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
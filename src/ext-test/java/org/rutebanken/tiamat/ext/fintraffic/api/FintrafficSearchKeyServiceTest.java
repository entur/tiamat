package org.rutebanken.tiamat.ext.fintraffic.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rutebanken.tiamat.ext.fintraffic.api.model.FintrafficReadApiSearchKey;
import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.model.PrivateCodeStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.VehicleModeEnumeration;
import org.rutebanken.tiamat.repository.StopPlaceRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class FintrafficSearchKeyServiceTest {

    private FintrafficSearchKeyService service;

    @BeforeEach
    void setUp() {
        StopPlaceRepository stopPlaceRepositoryMock = mock(StopPlaceRepository.class);

        AreaCodeMappingConfig areaCodeMappingConfig = new AreaCodeMappingConfig();
        areaCodeMappingConfig.setAreaCodes(Map.of(
                "lft", "499,905",
                "hsl", "049,091,092,235,245,257,753,755,858"
        ));
        areaCodeMappingConfig.buildReverseIndex();

        service = new FintrafficSearchKeyService(
                new ObjectMapper(),
                areaCodeMappingConfig,
                stopPlaceRepositoryMock
        );
    }

    private StopPlace createStopPlace(String netexId, VehicleModeEnumeration transportMode) {
        return createStopPlace(netexId, transportMode, 1L);
    }

    private StopPlace createStopPlace(String netexId, VehicleModeEnumeration transportMode, long version) {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId(netexId);
        stopPlace.setTransportMode(transportMode);
        stopPlace.setVersion(version);
        return stopPlace;
    }

    private StopPlace createStopPlaceWithMunicipality(String netexId, VehicleModeEnumeration transportMode, String municipalityCode) {
        StopPlace stopPlace = createStopPlace(netexId, transportMode);
        TopographicPlace topographicPlace = new TopographicPlace();
        PrivateCodeStructure privateCode = new PrivateCodeStructure();
        privateCode.setValue(municipalityCode);
        topographicPlace.setPrivateCode(privateCode);
        stopPlace.setTopographicPlace(topographicPlace);
        return stopPlace;
    }

    @Test
    void generateSearchKeyJSON_regularStopPlace_containsTransportMode() {
        StopPlace stopPlace = createStopPlace("FSR:StopPlace:1", VehicleModeEnumeration.BUS);

        String json = service.generateSearchKeyJSON(stopPlace);

        assertThat(json).contains("bus");
    }

    @Test
    void generateSearchKeyJSON_regularStopPlace_noTransportMode_emptyArray() {
        StopPlace stopPlace = createStopPlace("FSR:StopPlace:2", null);

        String json = service.generateSearchKeyJSON(stopPlace);

        // transportModes should be empty
        assertThat(json).contains("\"transportModes\":[]");
    }

    @Test
    void generateSearchKeyJSON_parentStopPlace_collectsChildTransportModes() {
        StopPlace parentStopPlace = createStopPlace("FSR:StopPlace:10", null);
        parentStopPlace.setParentStopPlace(true);

        StopPlace child1 = createStopPlace("FSR:StopPlace:11", VehicleModeEnumeration.BUS, 1L);
        StopPlace child2 = createStopPlace("FSR:StopPlace:12", VehicleModeEnumeration.TRAM, 2L);
        HashSet<StopPlace> children = new HashSet<>();
        children.add(child1);
        children.add(child2);
        parentStopPlace.setChildren(children);

        String json = service.generateSearchKeyJSON(parentStopPlace);

        assertThat(json).contains("bus");
        assertThat(json).contains("tram");
    }

    @Test
    void generateSearchKeyJSON_parentStopPlace_deduplicatesChildTransportModes() {
        StopPlace parentStopPlace = createStopPlace("FSR:StopPlace:20", null);
        parentStopPlace.setParentStopPlace(true);

        StopPlace child1 = createStopPlace("FSR:StopPlace:21", VehicleModeEnumeration.BUS, 1L);
        StopPlace child2 = createStopPlace("FSR:StopPlace:22", VehicleModeEnumeration.BUS, 2L);
        HashSet<StopPlace> children = new HashSet<>();
        children.add(child1);
        children.add(child2);
        parentStopPlace.setChildren(children);

        String json = service.generateSearchKeyJSON(parentStopPlace);

        FintrafficReadApiSearchKey searchKey = parseSearchKey(json);
        long busCount = Arrays.stream(searchKey.transportModes()).filter("bus"::equals).count();
        assertThat(busCount).isEqualTo(1);
    }

    @Test
    void generateSearchKeyJSON_parentStopPlace_mergesParentAndChildTransportModes() {
        StopPlace parentStopPlace = createStopPlace("FSR:StopPlace:30", VehicleModeEnumeration.WATER, 1L);
        parentStopPlace.setParentStopPlace(true);

        StopPlace child = createStopPlace("FSR:StopPlace:31", VehicleModeEnumeration.BUS, 2L);
        HashSet<StopPlace> children = new HashSet<>();
        children.add(child);
        parentStopPlace.setChildren(children);

        String json = service.generateSearchKeyJSON(parentStopPlace);

        assertThat(json).contains("water");
        assertThat(json).contains("bus");
    }

    @Test
    void generateSearchKeyJSON_parentStopPlace_childWithNullTransportMode_ignored() {
        StopPlace parentStopPlace = createStopPlace("FSR:StopPlace:40", null);
        parentStopPlace.setParentStopPlace(true);

        StopPlace childWithMode = createStopPlace("FSR:StopPlace:41", VehicleModeEnumeration.BUS, 1L);
        StopPlace childNoMode = createStopPlace("FSR:StopPlace:42", null, 2L);
        HashSet<StopPlace> children = new HashSet<>();
        children.add(childWithMode);
        children.add(childNoMode);
        parentStopPlace.setChildren(children);

        String json = service.generateSearchKeyJSON(parentStopPlace);

        assertThat(json).contains("bus");
        FintrafficReadApiSearchKey searchKey = parseSearchKey(json);
        assertThat(searchKey.transportModes()).containsExactly("bus");
    }

    @Test
    void generateSearchKeyJSON_parentStopPlace_noChildren_emptyTransportModes() {
        StopPlace parentStopPlace = createStopPlace("FSR:StopPlace:50", null);
        parentStopPlace.setParentStopPlace(true);
        parentStopPlace.setChildren(Set.of());

        String json = service.generateSearchKeyJSON(parentStopPlace);

        assertThat(json).contains("\"transportModes\":[]");
    }

    @Test
    void generateSearchKeyJSON_fareZone_returnsEmptySearchKey() {
        FareZone fareZone = new FareZone();
        fareZone.setNetexId("TKL:FareZone:A");
        fareZone.setVersion(1L);

        FintrafficReadApiSearchKey searchKey = parseSearchKey(service.generateSearchKeyJSON(fareZone));

        assertThat(searchKey.transportModes()).isEmpty();
        assertThat(searchKey.areaCodes()).isEmpty();
        assertThat(searchKey.municipalityCodes()).isEmpty();
    }

    private FintrafficReadApiSearchKey parseSearchKey(String json) {
        try {
            return new ObjectMapper().readValue(json, FintrafficReadApiSearchKey.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse search key JSON: " + json, e);
        }
    }

    @Test
    void generateSearchKeyJSON_stopPlaceWithMunicipality_containsAreaCode() {
        StopPlace stopPlace = createStopPlaceWithMunicipality("FSR:StopPlace:100", VehicleModeEnumeration.BUS, "499");

        FintrafficReadApiSearchKey searchKey = parseSearchKey(service.generateSearchKeyJSON(stopPlace));

        assertThat(searchKey.areaCodes()).containsExactly("LFT");
        assertThat(searchKey.municipalityCodes()).containsExactly("499");
    }

    @Test
    void generateSearchKeyJSON_stopPlaceWithNoTopographicPlace_emptyAreaCodes() {
        StopPlace stopPlace = createStopPlace("FSR:StopPlace:101", VehicleModeEnumeration.BUS);

        FintrafficReadApiSearchKey searchKey = parseSearchKey(service.generateSearchKeyJSON(stopPlace));

        assertThat(searchKey.areaCodes()).isEmpty();
        assertThat(searchKey.municipalityCodes()).isEmpty();
    }

    @Test
    void generateSearchKeyJSON_stopPlaceWithUnknownMunicipalityCode_emptyAreaCodes() {
        StopPlace stopPlace = createStopPlaceWithMunicipality("FSR:StopPlace:102", VehicleModeEnumeration.BUS, "999");

        FintrafficReadApiSearchKey searchKey = parseSearchKey(service.generateSearchKeyJSON(stopPlace));

        assertThat(searchKey.areaCodes()).isEmpty();
        assertThat(searchKey.municipalityCodes()).containsExactly("999");
    }

    @Test
    void generateSearchKeyJSON_stopPlaceWithMunicipality_areaCodeIsUppercase() {
        StopPlace stopPlace = createStopPlaceWithMunicipality("FSR:StopPlace:103", VehicleModeEnumeration.BUS, "091");

        FintrafficReadApiSearchKey searchKey = parseSearchKey(service.generateSearchKeyJSON(stopPlace));

        assertThat(searchKey.areaCodes()).containsExactly("HSL");
    }
}

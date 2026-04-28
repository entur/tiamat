package org.rutebanken.tiamat.ext.fintraffic.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rutebanken.tiamat.ext.fintraffic.api.model.FintrafficReadApiSearchKey;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.VehicleModeEnumeration;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FintrafficSearchKeyServiceTest {

    private FintrafficSearchKeyService service;

    @BeforeEach
    void setUp() {
        TopographicPlaceRepository topographicPlaceRepositoryMock = mock(TopographicPlaceRepository.class);
        StopPlaceRepository stopPlaceRepositoryMock = mock(StopPlaceRepository.class);
        when(topographicPlaceRepositoryMock.findTopographicPlace(any())).thenReturn(List.of());

        service = new FintrafficSearchKeyService(
                new ObjectMapper(),
                topographicPlaceRepositoryMock,
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

    private FintrafficReadApiSearchKey parseSearchKey(String json) {
        try {
            return new ObjectMapper().readValue(json, FintrafficReadApiSearchKey.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse search key JSON: " + json, e);
        }
    }
}

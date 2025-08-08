package org.rutebanken.tiamat.ext.fintraffic.auth;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.rutebanken.tiamat.exporter.params.TopographicPlaceSearch;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.model.Value;
import org.rutebanken.tiamat.model.VehicleModeEnumeration;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FintrafficAuthorizationServiceTest {
    private final TopographicPlaceRepository topographicPlaceRepositoryMock = mock(TopographicPlaceRepository.class);
    private final TrivoreAuthorizations trivoreAuthorizationsMock = mock(TrivoreAuthorizations.class);

    @BeforeEach
    void setUp() {
        TopographicPlace place = getTopographicPlace();
        when(topographicPlaceRepositoryMock.findTopographicPlace(any(TopographicPlaceSearch.class))).thenReturn(List.of(place));
        when(trivoreAuthorizationsMock.getAccessibleCodespaces()).thenReturn(Set.of("ABC", "XYZ"));
    }

    private static @NotNull TopographicPlace getTopographicPlace() {
        TopographicPlace place = new TopographicPlace();
        GeometryFactory fact = new GeometryFactory();
        Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(0, 0),
                new Coordinate(1, 0),
                new Coordinate(1, 1),
                new Coordinate(0, 1),
                new Coordinate(0, 0),
        };
        LinearRing ring = fact.createLinearRing(coordinates);
        place.setPolygon(new Polygon(ring, null, fact));
        place.setTopographicPlaceType(TopographicPlaceTypeEnumeration.REGION);
        Map<String, Value> values = place.getKeyValues();
        values.put("codespace", new Value("ABC"));
        return place;
    }

    private static @NotNull Point getPoint(Coordinate coordinate) {
        GeometryFactory fact = new GeometryFactory();
        return fact.createPoint(coordinate);
    }

    private static @NotNull StopPlace getStopPlace(String netexId, VehicleModeEnumeration transportMode, Point centroid) {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId(netexId);
        stopPlace.setTransportMode(transportMode);
        stopPlace.setCentroid(centroid);
        return stopPlace;
    }


    @Test
    public void testCanEditEntityPoint() {
        FintrafficAuthorizationService authorizationService = new FintrafficAuthorizationService(
                trivoreAuthorizationsMock,
                topographicPlaceRepositoryMock
        );
        assertThat(authorizationService.canEditEntity(getPoint(new Coordinate(0.5, 0.5))), equalTo(true));
        assertThat(authorizationService.canEditEntity(getPoint(new Coordinate(2, 2))), equalTo(false));
    }


    @Test
    public void testCanEditEntityStopPlace() {
        StopPlace stopPlaceAllowedToEdit = getStopPlace("FSR:StopPlace:1", VehicleModeEnumeration.BUS, getPoint(new Coordinate(0.3, 0.3)));
        StopPlace stopPlaceForbiddenToEditCodespace = getStopPlace("AAA:StopPlace:1", VehicleModeEnumeration.BUS, getPoint(new Coordinate(0.3, 0.3)));
        StopPlace stopPlaceForbiddenToEditTransportMode = getStopPlace("FSR:StopPlace:1", VehicleModeEnumeration.RAIL, getPoint(new Coordinate(0.3, 0.3)));
        StopPlace stopPlaceOutOfBounds = getStopPlace("FSR:StopPlace:1", VehicleModeEnumeration.BUS, getPoint(new Coordinate(3, 3)));

        when(trivoreAuthorizationsMock.hasAccess(matches("StopPlace"), matches("BUS"), eq(TrivorePermission.MANAGE))).thenReturn(true);
        when(trivoreAuthorizationsMock.hasAccess(matches("StopPlace"), matches("RAIL"), eq(TrivorePermission.MANAGE))).thenReturn(false);
        when(trivoreAuthorizationsMock.hasAccessToCodespace(matches("FSR"))).thenReturn(true);
        when(trivoreAuthorizationsMock.hasAccessToCodespace(matches("AAA"))).thenReturn(false);

        FintrafficAuthorizationService authorizationService = new FintrafficAuthorizationService(
                trivoreAuthorizationsMock,
                topographicPlaceRepositoryMock
        );
        assertThat(authorizationService.canEditEntity(stopPlaceAllowedToEdit), equalTo(true));
        assertThat(authorizationService.canEditEntity(stopPlaceForbiddenToEditCodespace), equalTo(false));
        assertThat(authorizationService.canEditEntity(stopPlaceForbiddenToEditTransportMode), equalTo(false));
        assertThat(authorizationService.canEditEntity(stopPlaceOutOfBounds), equalTo(false));
    }
}

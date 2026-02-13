package org.rutebanken.tiamat.ext.fintraffic.auth;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.rutebanken.tiamat.exporter.params.TopographicPlaceSearch;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.model.Value;
import org.rutebanken.tiamat.model.VehicleModeEnumeration;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FintrafficAuthorizationServiceTest {

    private static @Nonnull TopographicPlace getTopographicPlace() {
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

    private static @Nonnull Point getPoint(Coordinate coordinate) {
        GeometryFactory fact = new GeometryFactory();
        return fact.createPoint(coordinate);
    }

    private static @Nonnull StopPlace getStopPlace(String netexId, VehicleModeEnumeration transportMode, Point centroid) {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId(netexId);
        stopPlace.setTransportMode(transportMode);
        stopPlace.setCentroid(centroid);
        return stopPlace;
    }

    private static @Nonnull Parking getParking(String netexId, Point centroid) {
        Parking parking = new Parking();
        parking.setNetexId(netexId);
        parking.setCentroid(centroid);
        return parking;
    }

    private static @Nonnull Quay getQuay(String netexId, Point centroid) {
        Quay quay = new Quay();
        quay.setNetexId(netexId);
        quay.setCentroid(centroid);
        return quay;
    }

    private static FintrafficAuthorizationService getAuthorizationService(
            boolean enableCodeSpaceFiltering,
            String allowedCodeSpace
    ) {
        TopographicPlaceRepository topographicPlaceRepositoryMock = mock(TopographicPlaceRepository.class);
        TrivoreAuthorizations trivoreAuthorizationsMock = mock(TrivoreAuthorizations.class);

        TopographicPlace place = getTopographicPlace();
        when(topographicPlaceRepositoryMock.findTopographicPlace(any(TopographicPlaceSearch.class))).thenReturn(List.of(place));
        when(trivoreAuthorizationsMock.getAccessibleCodespaces()).thenReturn(Set.of("ABC", "XYZ"));

        when(trivoreAuthorizationsMock.hasAccess(matches("StopPlace"), matches("BUS"), eq(TrivorePermission.MANAGE), anyBoolean())).thenReturn(true);
        when(trivoreAuthorizationsMock.hasAccess(matches("Parking"), matches("\\{all\\}"), eq(TrivorePermission.MANAGE), anyBoolean())).thenReturn(true);
        when(trivoreAuthorizationsMock.hasAccess(matches("Quay"), matches("\\{all\\}"), eq(TrivorePermission.MANAGE), anyBoolean())).thenReturn(true);
        when(trivoreAuthorizationsMock.hasAccess(matches("StopPlace"), matches("RAIL"), eq(TrivorePermission.MANAGE), anyBoolean())).thenReturn(false);
        if (enableCodeSpaceFiltering) {
            when(trivoreAuthorizationsMock.hasAccessToCodespace(matches(allowedCodeSpace))).thenReturn(true);
            when(trivoreAuthorizationsMock.hasAccessToCodespace(not(matches(allowedCodeSpace)))).thenReturn(false);
        } else {
            when(trivoreAuthorizationsMock.hasAccessToCodespace(anyString())).thenReturn(true);
        }

        return new FintrafficAuthorizationService(
                trivoreAuthorizationsMock,
                topographicPlaceRepositoryMock
        );
    }


    @Test
    public void testCanEditEntityPoint() {
        FintrafficAuthorizationService authorizationService = getAuthorizationService(false, null);
        assertThat(authorizationService.canEditEntity(getPoint(new Coordinate(0.5, 0.5))), equalTo(true));
        assertThat(authorizationService.canEditEntity(getPoint(new Coordinate(2, 2))), equalTo(false));
    }


    @Test
    public void testCanEditEntityStopPlace() {
        StopPlace stopPlaceAllowedToEdit = getStopPlace("FSR:StopPlace:1", VehicleModeEnumeration.BUS, getPoint(new Coordinate(0.3, 0.3)));
        StopPlace stopPlaceForbiddenToEditCodespace = getStopPlace("AAA:StopPlace:1", VehicleModeEnumeration.BUS, getPoint(new Coordinate(0.3, 0.3)));
        StopPlace stopPlaceForbiddenToEditTransportMode = getStopPlace("FSR:StopPlace:1", VehicleModeEnumeration.RAIL, getPoint(new Coordinate(0.3, 0.3)));
        StopPlace stopPlaceOutOfBounds = getStopPlace("FSR:StopPlace:1", VehicleModeEnumeration.BUS, getPoint(new Coordinate(3, 3)));

        FintrafficAuthorizationService authorizationService = getAuthorizationService(true, "FSR");

        assertThat(authorizationService.canEditEntity(stopPlaceAllowedToEdit), equalTo(true));
        assertThat(authorizationService.canEditEntity(stopPlaceForbiddenToEditCodespace), equalTo(false));
        assertThat(authorizationService.canEditEntity(stopPlaceForbiddenToEditTransportMode), equalTo(false));
        assertThat(authorizationService.canEditEntity(stopPlaceOutOfBounds), equalTo(false));
    }

    @Test
    public void testCanEditEntityParking() {
        Parking parkingAllowedToEdit = getParking("FSR:Parking:1", getPoint(new Coordinate(0.3, 0.3)));
        Parking parkingOutOfBounds = getParking("FSR:Parking:2", getPoint(new Coordinate(3, 3)));

        FintrafficAuthorizationService authorizationService = getAuthorizationService(false, null);

        assertThat(authorizationService.canEditEntity(parkingAllowedToEdit), equalTo(true));
        assertThat(authorizationService.canEditEntity(parkingOutOfBounds), equalTo(false));
    }


    @Test
    public void testCanEditStopPlaceWithNestedEntities() {
        StopPlace stopPlaceWithQuayAndNestedStopPlace = getStopPlace("FSR:StopPlace:1", VehicleModeEnumeration.BUS, getPoint(new Coordinate(0.3, 0.3)));
        StopPlace childStopPlace = getStopPlace("FSR:StopPlace:2", VehicleModeEnumeration.BUS, getPoint(new Coordinate(0.5, 0.5)));
        stopPlaceWithQuayAndNestedStopPlace.setChildren(Set.of(childStopPlace));
        Quay quay = getQuay("FSR:Quay:1", getPoint(new Coordinate(0.4, 0.4)));
        stopPlaceWithQuayAndNestedStopPlace.setQuays(Set.of(quay));
        FintrafficAuthorizationService authorizationService = getAuthorizationService(false, null);
        assertThat(authorizationService.canEditEntity(stopPlaceWithQuayAndNestedStopPlace), equalTo(true));
    }

    @Test
    public void testCanEditStopPlaceWithNestedEntitiesNotAllowed() {
        StopPlace stopPlaceWithQuayAndNestedStopPlace = getStopPlace("FSR:StopPlace:1", VehicleModeEnumeration.BUS, getPoint(new Coordinate(0.3, 0.3)));
        StopPlace childStopPlace = getStopPlace("FSR:StopPlace:2", VehicleModeEnumeration.BUS, getPoint(new Coordinate(0.5, 0.5)));
        stopPlaceWithQuayAndNestedStopPlace.setChildren(Set.of(childStopPlace));
        Quay quay = getQuay("FSR:Quay:1", getPoint(new Coordinate(1.4, 0.4)));
        stopPlaceWithQuayAndNestedStopPlace.setQuays(Set.of(quay));
        FintrafficAuthorizationService authorizationService = getAuthorizationService(false, null);
        assertThat(authorizationService.canEditEntity(stopPlaceWithQuayAndNestedStopPlace), equalTo(false));
    }
}

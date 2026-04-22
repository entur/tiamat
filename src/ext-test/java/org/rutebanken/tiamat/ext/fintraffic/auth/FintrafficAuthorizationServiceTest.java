package org.rutebanken.tiamat.ext.fintraffic.auth;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.rutebanken.tiamat.exporter.params.TopographicPlaceSearch;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
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

    private static @Nonnull TopographicPlace getMunicipalityTopographicPlace(String municipalityCode,
                                                                             Coordinate[] coordinates) {
        TopographicPlace place = new TopographicPlace();
        GeometryFactory fact = new GeometryFactory();
        LinearRing ring = fact.createLinearRing(coordinates);
        Polygon polygon = new Polygon(ring, null, fact);
        place.setMultiSurface(new MultiPolygon(new Polygon[]{polygon}, fact));
        place.setTopographicPlaceType(TopographicPlaceTypeEnumeration.MUNICIPALITY);
        place.setPrivateCode(new org.rutebanken.tiamat.model.PrivateCodeStructure(municipalityCode, "type"));
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

    private static FintrafficAuthorizationService getAuthorizationService() {
        return getAuthorizationService(true, true, false);
    }

    private static FintrafficAuthorizationService getAuthorizationService(
            boolean codespaceEnabled,
            boolean municipalityEnabled,
            boolean multiModalStopPlaceSupportDisabled
    ) {
        TopographicPlaceRepository topographicPlaceRepositoryMock = mock(TopographicPlaceRepository.class);
        TrivoreAuthorizations trivoreAuthorizationsMock = mock(TrivoreAuthorizations.class);

        TopographicPlace regionPlace = getTopographicPlace();
        TopographicPlace municipalityPlace = getMunicipalityTopographicPlace("091", new Coordinate[] {
                new Coordinate(2, 2),
                new Coordinate(3, 2),
                new Coordinate(3, 3),
                new Coordinate(2, 3),
                new Coordinate(2, 2),
        });
        when(topographicPlaceRepositoryMock.findTopographicPlace(any(TopographicPlaceSearch.class)))
                .thenReturn(List.of(regionPlace, municipalityPlace));
        when(trivoreAuthorizationsMock.getAccessibleCodespaces()).thenReturn(Set.of("ABC", "XYZ"));
        when(trivoreAuthorizationsMock.getAccessibleMunicipalityCodes()).thenReturn(Set.of("091"));

        when(trivoreAuthorizationsMock.hasAccess(matches("StopPlace"), matches("BUS"), eq(TrivorePermission.MANAGE), anyBoolean())).thenReturn(true);
        when(trivoreAuthorizationsMock.hasAccess(matches("Parking"), matches("\\{all\\}"), eq(TrivorePermission.MANAGE), anyBoolean())).thenReturn(true);
        when(trivoreAuthorizationsMock.hasAccess(matches("Quay"), matches("\\{all\\}"), eq(TrivorePermission.MANAGE), anyBoolean())).thenReturn(true);
        when(trivoreAuthorizationsMock.hasAccess(matches("StopPlace"), matches("RAIL"), eq(TrivorePermission.MANAGE), anyBoolean())).thenReturn(false);
        when(trivoreAuthorizationsMock.hasAccess(matches("GroupOfStopPlaces"), matches("\\{all\\}"), eq(TrivorePermission.MANAGE), anyBoolean())).thenReturn(true);

        return new FintrafficAuthorizationService(
                trivoreAuthorizationsMock,
                topographicPlaceRepositoryMock,
                codespaceEnabled,
                municipalityEnabled,
                multiModalStopPlaceSupportDisabled
        );
    }


    @Test
    public void testCanEditEntityPoint() {
        FintrafficAuthorizationService authorizationService = getAuthorizationService();
        assertThat(authorizationService.canEditEntity(getPoint(new Coordinate(0.5, 0.5))), equalTo(true));
        assertThat(authorizationService.canEditEntity(getPoint(new Coordinate(2, 2))), equalTo(false));
    }


    @Test
    public void testCanEditEntityStopPlace() {
        StopPlace stopPlaceAllowedToEdit = getStopPlace("FSR:StopPlace:1", VehicleModeEnumeration.BUS, getPoint(new Coordinate(0.3, 0.3)));
        StopPlace stopPlaceForbiddenToEditTransportMode = getStopPlace("FSR:StopPlace:1", VehicleModeEnumeration.RAIL, getPoint(new Coordinate(0.3, 0.3)));
        StopPlace stopPlaceOutOfBounds = getStopPlace("FSR:StopPlace:1", VehicleModeEnumeration.BUS, getPoint(new Coordinate(3, 3)));

        FintrafficAuthorizationService authorizationService = getAuthorizationService();

        assertThat(authorizationService.canEditEntity(stopPlaceAllowedToEdit), equalTo(true));
        assertThat(authorizationService.canEditEntity(stopPlaceForbiddenToEditTransportMode), equalTo(false));
        assertThat(authorizationService.canEditEntity(stopPlaceOutOfBounds), equalTo(false));
    }

    @Test
    public void testCanEditEntityParking() {
        Parking parkingAllowedToEdit = getParking("FSR:Parking:1", getPoint(new Coordinate(0.3, 0.3)));
        Parking parkingOutOfBounds = getParking("FSR:Parking:2", getPoint(new Coordinate(3, 3)));

        FintrafficAuthorizationService authorizationService = getAuthorizationService();

        assertThat(authorizationService.canEditEntity(parkingAllowedToEdit), equalTo(true));
        assertThat(authorizationService.canEditEntity(parkingOutOfBounds), equalTo(false));
    }


    @Test
    public void testCanEditParentStopPlaceAllChildTransportModesAllowed() {
        // Parent stop place with children that all have allowed transport modes (BUS)
        StopPlace parentStopPlace = getStopPlace("FSR:StopPlace:100", null, getPoint(new Coordinate(0.3, 0.3)));
        parentStopPlace.setParentStopPlace(true);
        StopPlace child1 = getStopPlace("FSR:StopPlace:101", VehicleModeEnumeration.BUS, getPoint(new Coordinate(0.3, 0.3)));
        StopPlace child2 = getStopPlace("FSR:StopPlace:102", VehicleModeEnumeration.BUS, getPoint(new Coordinate(0.4, 0.4)));
        parentStopPlace.setChildren(Set.of(child1, child2));

        FintrafficAuthorizationService authorizationService = getAuthorizationService();
        assertThat(authorizationService.canEditEntity(parentStopPlace), equalTo(true));
    }

    @Test
    public void testCanEditParentStopPlaceChildTransportModeNotAllowed() {
        // Parent stop place with a child that has a forbidden transport mode (RAIL)
        StopPlace parentStopPlace = getStopPlace("FSR:StopPlace:200", null, getPoint(new Coordinate(0.3, 0.3)));
        parentStopPlace.setParentStopPlace(true);
        StopPlace child = getStopPlace("FSR:StopPlace:201", VehicleModeEnumeration.RAIL, getPoint(new Coordinate(0.3, 0.3)));
        parentStopPlace.setChildren(Set.of(child));

        FintrafficAuthorizationService authorizationService = getAuthorizationService();
        assertThat(authorizationService.canEditEntity(parentStopPlace), equalTo(false));
    }

    @Test
    public void testCanNotEditParentStopPlaceMultiModalSupportDisabled() {
        // Parent stop place with children that all have allowed transport modes (BUS)
        // Multi-modal stop place support is disabled, so parent stop place should not be editable even if child transport mode is allowed
        StopPlace parentStopPlace = getStopPlace("FSR:StopPlace:100", null, getPoint(new Coordinate(0.3, 0.3)));
        parentStopPlace.setParentStopPlace(true);
        StopPlace child1 = getStopPlace("FSR:StopPlace:101", VehicleModeEnumeration.BUS, getPoint(new Coordinate(0.3, 0.3)));
        StopPlace child2 = getStopPlace("FSR:StopPlace:102", VehicleModeEnumeration.BUS, getPoint(new Coordinate(0.4, 0.4)));
        parentStopPlace.setChildren(Set.of(child1, child2));

        FintrafficAuthorizationService authorizationService = getAuthorizationService(true, true, true);
        assertThat(authorizationService.canEditEntity(parentStopPlace), equalTo(false));
    }

    @Test
    public void testCanEditParentStopPlaceWithMixedChildTransportModes() {
        // Parent stop place with children of mixed transport modes: one allowed (BUS), one denied (RAIL)
        StopPlace parentStopPlace = getStopPlace("FSR:StopPlace:300", null, getPoint(new Coordinate(0.3, 0.3)));
        parentStopPlace.setParentStopPlace(true);
        StopPlace childBus = getStopPlace("FSR:StopPlace:301", VehicleModeEnumeration.BUS, getPoint(new Coordinate(0.3, 0.3)));
        StopPlace childRail = getStopPlace("FSR:StopPlace:302", VehicleModeEnumeration.RAIL, getPoint(new Coordinate(0.4, 0.4)));
        parentStopPlace.setChildren(Set.of(childBus, childRail));

        FintrafficAuthorizationService authorizationService = getAuthorizationService();
        assertThat(authorizationService.canEditEntity(parentStopPlace), equalTo(false));
    }

    @Test
    public void testCanEditParentStopPlaceWithNoChildren() {
        // Parent stop place with no children
        StopPlace parentStopPlace = getStopPlace("FSR:StopPlace:400", null, getPoint(new Coordinate(0.3, 0.3)));
        parentStopPlace.setParentStopPlace(true);
        parentStopPlace.setChildren(Set.of());

        FintrafficAuthorizationService authorizationService = getAuthorizationService();
        assertThat(authorizationService.canEditEntity(parentStopPlace), equalTo(false));
    }

    @Test
    public void testCanEditStopPlaceWithNestedEntities() {
        StopPlace stopPlaceWithQuayAndNestedStopPlace = getStopPlace("FSR:StopPlace:1", VehicleModeEnumeration.BUS, getPoint(new Coordinate(0.3, 0.3)));
        StopPlace childStopPlace = getStopPlace("FSR:StopPlace:2", VehicleModeEnumeration.BUS, getPoint(new Coordinate(0.5, 0.5)));
        stopPlaceWithQuayAndNestedStopPlace.setChildren(Set.of(childStopPlace));
        Quay quay = getQuay("FSR:Quay:1", getPoint(new Coordinate(0.4, 0.4)));
        stopPlaceWithQuayAndNestedStopPlace.setQuays(Set.of(quay));
        FintrafficAuthorizationService authorizationService = getAuthorizationService();
        assertThat(authorizationService.canEditEntity(stopPlaceWithQuayAndNestedStopPlace), equalTo(true));
    }

    @Test
    public void testCanEditStopPlaceWithNestedEntitiesNotAllowed() {
        StopPlace stopPlaceWithQuayAndNestedStopPlace = getStopPlace("FSR:StopPlace:1", VehicleModeEnumeration.BUS, getPoint(new Coordinate(0.3, 0.3)));
        StopPlace childStopPlace = getStopPlace("FSR:StopPlace:2", VehicleModeEnumeration.BUS, getPoint(new Coordinate(0.5, 0.5)));
        stopPlaceWithQuayAndNestedStopPlace.setChildren(Set.of(childStopPlace));
        Quay quay = getQuay("FSR:Quay:1", getPoint(new Coordinate(1.4, 0.4)));
        stopPlaceWithQuayAndNestedStopPlace.setQuays(Set.of(quay));
        FintrafficAuthorizationService authorizationService = getAuthorizationService();
        assertThat(authorizationService.canEditEntity(stopPlaceWithQuayAndNestedStopPlace), equalTo(false));
    }

    @Test
    public void testCanEditEntityByMunicipalityCodes() {
        // Point (2.5, 2.5) is inside municipality polygon (2,2)-(3,3) but outside region polygon (0,0)-(1,1)
        StopPlace stopPlace = getStopPlace("FSR:StopPlace:10", VehicleModeEnumeration.BUS, getPoint(new Coordinate(2.5, 2.5)));
        FintrafficAuthorizationService authorizationService = getAuthorizationService();
        assertThat(authorizationService.canEditEntity(stopPlace), equalTo(true));
    }

    @Test
    public void testCanEditEntityByMunicipalityCodesOutOfBounds() {
        // Point (5, 5) is outside both region and municipality polygons
        StopPlace stopPlace = getStopPlace("FSR:StopPlace:11", VehicleModeEnumeration.BUS, getPoint(new Coordinate(5, 5)));
        FintrafficAuthorizationService authorizationService = getAuthorizationService();
        assertThat(authorizationService.canEditEntity(stopPlace), equalTo(false));
    }

    @Test
    public void testCanEditEntityByEitherCodespaceOrMunicipality() {
        // Point (0.5, 0.5) is inside region polygon — should pass via codespace check
        StopPlace stopPlaceInRegion = getStopPlace("FSR:StopPlace:12", VehicleModeEnumeration.BUS, getPoint(new Coordinate(0.5, 0.5)));
        // Point (2.5, 2.5) is inside municipality polygon — should pass via municipality check
        StopPlace stopPlaceInMunicipality = getStopPlace("FSR:StopPlace:13", VehicleModeEnumeration.BUS, getPoint(new Coordinate(2.5, 2.5)));

        FintrafficAuthorizationService authorizationService = getAuthorizationService();
        assertThat(authorizationService.canEditEntity(stopPlaceInRegion), equalTo(true));
        assertThat(authorizationService.canEditEntity(stopPlaceInMunicipality), equalTo(true));
    }

    @Test
    public void testCodespaceOnlyMode() {
        FintrafficAuthorizationService authorizationService = getAuthorizationService(true, false, false);
        // Point inside region (codespace) — allowed
        assertThat(authorizationService.canEditEntity(getPoint(new Coordinate(0.5, 0.5))), equalTo(true));
        // Point inside municipality area but municipality auth disabled — denied
        assertThat(authorizationService.canEditEntity(getPoint(new Coordinate(2.5, 2.5))), equalTo(false));
    }

    @Test
    public void testMunicipalityOnlyMode() {
        FintrafficAuthorizationService authorizationService = getAuthorizationService(false, true, false);
        // Point inside region but codespace auth disabled — denied
        assertThat(authorizationService.canEditEntity(getPoint(new Coordinate(0.5, 0.5))), equalTo(false));
        // Point inside municipality area — allowed
        assertThat(authorizationService.canEditEntity(getPoint(new Coordinate(2.5, 2.5))), equalTo(true));
    }

    @Test
    public void testBothAuthorizationMethodsDisabled() {
        FintrafficAuthorizationService authorizationService = getAuthorizationService(false, false, false);
        // Both disabled — all geographic edits denied
        assertThat(authorizationService.canEditEntity(getPoint(new Coordinate(0.5, 0.5))), equalTo(false));
        assertThat(authorizationService.canEditEntity(getPoint(new Coordinate(2.5, 2.5))), equalTo(false));
    }

    @Test
    public void testGroupOfStopPlacesEditable() {
        GroupOfStopPlaces groupOfStopPlaces = new GroupOfStopPlaces();
        groupOfStopPlaces.setNetexId("FSR:GroupOfStopPlaces:1");

        FintrafficAuthorizationService authorizationService = getAuthorizationService();
        assertThat(authorizationService.canEditEntity(groupOfStopPlaces), equalTo(true));
    }
}

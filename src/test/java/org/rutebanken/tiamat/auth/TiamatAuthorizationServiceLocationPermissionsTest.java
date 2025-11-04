package org.rutebanken.tiamat.auth;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.BusSubmodeEnumeration;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ENTITY_TYPE;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;


public class TiamatAuthorizationServiceLocationPermissionsTest extends TiamatIntegrationTest {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private MockedRoleAssignmentExtractor roleAssignmentExtractor;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;


    @Test
    @Transactional
    public void outSideLocationPermissionsTest() {

        setUpSecurityContext();

        RoleAssignment roleAssignment = RoleAssignment.builder()
                .withRole(ROLE_EDIT_STOPS)
                .withOrganisation("OST")
                .withAdministrativeZone("KVE:TopographicalPlace:01")
                .withEntityClassification(ENTITY_TYPE, "StopPlace")
                .withEntityClassification("StopPlaceType", "!airport")
                .withEntityClassification("StopPlaceType", "!railStation")
                .withEntityClassification("Submode", "!railReplacementBus")
                .build();

        roleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignment);


        Point point = geometryFactory.createPoint(new Coordinate(9.536819, 61.772281));
        Point point2 = geometryFactory.createPoint(new Coordinate(5.536819, 50.772281));


        TopographicPlace municipality = new TopographicPlace();
        municipality.setNetexId("KVE:TopographicalPlace:01");
        municipality.setVersion(1);
        municipality.setPolygon(createPolygon(point));
        topographicPlaceRepository.saveAndFlush(municipality);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        stopPlace.setBusSubmode(BusSubmodeEnumeration.REGIONAL_BUS);
        stopPlace.setTopographicPlace(municipality);
        stopPlace.setCentroid(point);
        stopPlaceRepository.saveAndFlush(stopPlace);


        assertThat("Can not edit stopplaces", authorizationService.canEditEntity(point2), is(false));

        roleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignment);

        final Set<StopTypeEnumeration> locationAllowedStopPlaceTypes = authorizationService.getLocationAllowedStopPlaceTypes(false, point);
        assertThat("Allowed stop place types", locationAllowedStopPlaceTypes.size(), is(0));

        roleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignment);
        final Set<StopTypeEnumeration> locationBannedStopPlaceTypes = authorizationService.getLocationBannedStopPlaceTypes(false, point);
        assertThat("Banned stop place types", locationBannedStopPlaceTypes.isEmpty(), is(true));
        assertThat("Banned stop place types", locationBannedStopPlaceTypes.contains(StopTypeEnumeration.AIRPORT), is(false));

    }


    @Test
    @Transactional
    public void insideLocationPermissionTest() {
        setUpSecurityContext();

        RoleAssignment roleAssignment = RoleAssignment.builder()
                .withRole(ROLE_EDIT_STOPS)
                .withOrganisation("OST")
                .withAdministrativeZone("KVE:TopographicalPlace:01")
                .withEntityClassification(ENTITY_TYPE, "*")
                .withEntityClassification("StopPlaceType", "!airport")
                .withEntityClassification("StopPlaceType", "!railStation")
                .withEntityClassification("Submode", "!railReplacementBus")
                .build();

        roleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignment);

        Point point = geometryFactory.createPoint(new Coordinate(9.536819, 61.772281));
        Point point2 = geometryFactory.createPoint(new Coordinate(8.536819, 59.772281));


        TopographicPlace municipality = new TopographicPlace();
        municipality.setNetexId("KVE:TopographicalPlace:01");
        municipality.setVersion(1);
        municipality.setPolygon(createPolygon(point));
        topographicPlaceRepository.saveAndFlush(municipality);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        stopPlace.setBusSubmode(BusSubmodeEnumeration.REGIONAL_BUS);
        stopPlace.setTopographicPlace(municipality);
        stopPlace.setCentroid(point);
        stopPlaceRepository.saveAndFlush(stopPlace);


        final boolean canEditEntity = authorizationService.canEditEntity(point2);
        assertThat("Can edit stopplaces", canEditEntity, is(true));



        final Set<StopTypeEnumeration> locationAllowedStopPlaceTypes = authorizationService.getLocationAllowedStopPlaceTypes(canEditEntity, point);
        assertThat("Allowed stop place types", locationAllowedStopPlaceTypes.isEmpty(), is(true));
        assertThat("Allowed stop place types", locationAllowedStopPlaceTypes.contains(StopTypeEnumeration.AIRPORT), is(false));

        roleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignment);

        final Set<StopTypeEnumeration> locationBannedStopPlaceTypes = authorizationService.getLocationBannedStopPlaceTypes(canEditEntity, point);
        assertThat("Banned stop place types", locationBannedStopPlaceTypes.size(), is(2));
        assertThat("Banned stop place types", locationBannedStopPlaceTypes.contains(StopTypeEnumeration.AIRPORT), is(true));
    }

    private Polygon createPolygon(Point point) {
        Geometry bufferedPoint = point.buffer(10);
        LinearRing linearRing = new LinearRing(new CoordinateArraySequence(bufferedPoint.getCoordinates()), geometryFactory);
        return geometryFactory.createPolygon(linearRing, null);
    }

}



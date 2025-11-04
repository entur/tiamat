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

package org.rutebanken.tiamat.auth;

import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.diff.generic.SubmodeEnumuration;
import org.rutebanken.tiamat.model.BusSubmodeEnumeration;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.WaterSubmodeEnumeration;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ENTITY_CLASSIFIER_ALL_TYPES;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ENTITY_TYPE;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_DELETE_STOPS;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;

@Transactional // Because of the authorization service logs entities which could read lazy loaded fields
public class TiamatAuthorizationServiceTest extends TiamatIntegrationTest {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private MockedRoleAssignmentExtractor mockedRoleAssignmentExtractor;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Test
    public void authorizedForStopPlaceTypeWhenOthersBlacklisted() {

        RoleAssignment roleAssignment = RoleAssignment.builder()
                .withRole(ROLE_EDIT_STOPS)
//                .withAdministrativeZone("01")
                .withOrganisation("OST")
                .withEntityClassification(ENTITY_TYPE, "StopPlace")
                .withEntityClassification("StopPlaceType", "!airport")
                .withEntityClassification("StopPlaceType", "!railStation")
                .build();


        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignment);
        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);


        boolean authorized = authorizationService.canEditEntity(stopPlace);
        assertThat(authorized, is(true));
    }

    @Test
    public void authorizedByQuay() {

        RoleAssignment roleAssignment = RoleAssignment.builder()
                .withRole(ROLE_EDIT_STOPS)
                .withOrganisation("OST")
                .withEntityClassification(ENTITY_TYPE, "StopPlace")
                .withEntityClassification("StopPlaceType", "onstreetBus")
                .build();


        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignment);
        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);

        Quay quay = new Quay();
        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);

        boolean authorized = authorizationService.canEditEntity(quay);
        assertThat(authorized, is(true));
    }

    @Test
    public void notAuthorizedForBlacklistedStopPlaceTypes() {

        RoleAssignment roleAssignment = RoleAssignment.builder()
                .withRole(ROLE_EDIT_STOPS)
                .withOrganisation("OST")
                .withEntityClassification(ENTITY_TYPE, "StopPlace")
                .withEntityClassification("StopPlaceType", "!airport")
                .withEntityClassification("StopPlaceType", "!railStation")
                .build();
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignment);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);
        boolean authorized = authorizationService.canEditEntity(stopPlace);
        assertThat(authorized, is(false));
    }

    /**
     * EntityType=StopPlace, StopPlaceType=!railStation,!airport, Submode=!railReplacementBus
     */
    @Test
    public void notAuthorizedWithSubmodeAndType() {
        RoleAssignment roleAssignment = RoleAssignment.builder()
                .withRole(ROLE_EDIT_STOPS)
                .withOrganisation("OST")
                .withEntityClassification(ENTITY_TYPE, "StopPlace")
                .withEntityClassification("StopPlaceType", "!airport")
                .withEntityClassification("Submode", "!railReplacementBus")
                .build();

        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignment);
        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.AIRPORT);
        stopPlace.setBusSubmode(BusSubmodeEnumeration.RAIL_REPLACEMENT_BUS);

        boolean authorized = authorizationService.canEditEntity(stopPlace);
        assertThat("Should NOT be authorized as both type and submode does not match", authorized, is(false));
    }

    @Test
    public void authorizedWithSubmodeAndType() {
        RoleAssignment roleAssignment = RoleAssignment.builder()
                .withRole(ROLE_EDIT_STOPS)
                .withOrganisation("OST")
                .withEntityClassification(ENTITY_TYPE, "StopPlace")
                .withEntityClassification("StopPlaceType", "!airport")
                .withEntityClassification("Submode", "!railReplacementBus")
                .build();

        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        stopPlace.setBusSubmode(BusSubmodeEnumeration.REGIONAL_BUS);

        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignment);
        boolean authorized = authorizationService.canEditEntity(stopPlace);
        assertThat("Should be authorized as both type and submode are allowed", authorized, is(true));
    }
    /**
     * Test  allowed stop place types
     * EntityType=StopPlace, StopPlaceType=railStation,railReplacementBus
     */

    @Test
    public void authorizedGetAllowedStopPlaceTypesTest() {
        final List<RoleAssignment> roleAssignments = roleAssignmentsForRailAndRailReplacementMocked(ROLE_EDIT_STOPS);
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignments);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);
        stopPlace.setBusSubmode(BusSubmodeEnumeration.RAIL_REPLACEMENT_BUS);

        final Set<StopTypeEnumeration> allowedStopPlaceTypes = authorizationService.getAllowedStopPlaceTypes(stopPlace);
        assertThat("Should contain allowed StopPlaceType", allowedStopPlaceTypes.contains(StopTypeEnumeration.RAIL_STATION), is(true));
    }


    @Test
    public void authorizedGetBannedStopPlaceTypesEntityFilterTest() {
        final List<RoleAssignment> roleAssignments = roleAssignmentsMultipleRoles();
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignments);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);

        final Set<StopTypeEnumeration> bannedStopPlaceTypes = authorizationService.getBannedStopPlaceTypes(stopPlace);
        assertThat("Should contain allowed StopPlaceType", bannedStopPlaceTypes.contains(StopTypeEnumeration.RAIL_STATION), is(false));
        assertThat("Should contain allowed StopPlaceType", bannedStopPlaceTypes.contains(StopTypeEnumeration.AIRPORT), is(false));
    }


    /**
     * Test  banned stop place types
     * EntityType=StopPlace, StopPlaceType=railStation,railReplacementBus
     * Test ignored as it is not working as expected
     */
    @Test
    @Ignore
    public void authorizedGetBannedStopPlaceTypesTest() {
        RoleAssignment roleAssignment = RoleAssignment.builder()
                .withRole(ROLE_EDIT_STOPS)
                .withOrganisation("OST")
                .withAdministrativeZone("KVE:TopographicalPlace:01")
                .withEntityClassification(ENTITY_TYPE, "StopPlace")
                .withEntityClassification("StopPlaceType", "!airport")
                .withEntityClassification("StopPlaceType", "!railStation")
                .withEntityClassification("Submode", "!railReplacementBus")
                .build();

        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignment);

        Point point = geometryFactory.createPoint(new Coordinate(9.536819, 61.772281));
        Point point2 = geometryFactory.createPoint(new Coordinate(23.682516, 70.664175));


        TopographicPlace municipality = new TopographicPlace();
        municipality.setNetexId("KVE:TopographicalPlace:01");
        municipality.setVersion(1);
        municipality.setPolygon(createPolygon(point));
        topographicPlaceRepository.saveAndFlush(municipality);

        TopographicPlace municipality2 = new TopographicPlace();
        municipality2.setNetexId("KVE:TopographicalPlace:02");
        municipality2.setVersion(1);
        municipality2.setPolygon(createPolygon(point2));
        topographicPlaceRepository.saveAndFlush(municipality2);



        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        stopPlace.setBusSubmode(BusSubmodeEnumeration.REGIONAL_BUS);
        stopPlace.setTopographicPlace(municipality);
        stopPlace.setCentroid(point);
        stopPlaceRepository.saveAndFlush(stopPlace);

        StopPlace railStation = new StopPlace();
        railStation.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);
        railStation.setTopographicPlace(municipality);
        railStation.setCentroid(point);
        stopPlaceRepository.saveAndFlush(railStation);

        StopPlace outsideStopPlace = new StopPlace();
        outsideStopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        outsideStopPlace.setBusSubmode(BusSubmodeEnumeration.REGIONAL_BUS);
        outsideStopPlace.setTopographicPlace(municipality2);
        outsideStopPlace.setCentroid(point2);
        stopPlaceRepository.saveAndFlush(outsideStopPlace);

        final Set<StopTypeEnumeration> bannedStopPlaceTypes = authorizationService.getBannedStopPlaceTypes(stopPlace);

        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignment);
        final Set<SubmodeEnumuration> bannedSubmodes = authorizationService.getBannedSubmodes(stopPlace);

        assertThat("Should contain banned StopPlaceType", bannedStopPlaceTypes.contains("airport"), is(true));
        assertThat("Should contain banned StopPlaceType", bannedStopPlaceTypes.contains("railStation"), is(true));
        assertThat("Should contain banned Submode", bannedSubmodes.contains("railReplacementBus"), is(true));

        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignment);
        boolean authorized = authorizationService.canEditEntity(stopPlace);
        assertThat("Should be authorized as both type and submode are allowed", authorized, is(true));

        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignment);
        boolean authorizedRail = authorizationService.canEditEntity(railStation);
        assertThat("Should not be authorized as rail station is banned", authorizedRail, is(false));


        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignment);
        boolean authorizedOutside = authorizationService.canEditEntity(outsideStopPlace);
        assertThat("Should be authorized as outside stop place is not in the same topographical place", authorizedOutside, is(false));

        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignment);
        final Set<SubmodeEnumuration> bannedSubmodesOutside = authorizationService.getBannedSubmodes(outsideStopPlace);
        assertThat("Should contain all banned Submode", bannedSubmodesOutside.contains(ENTITY_CLASSIFIER_ALL_TYPES), is(true));

        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignment);
        final Set<SubmodeEnumuration> bannedStopPlaceTypesOutside = authorizationService.getBannedSubmodes(outsideStopPlace);
        assertThat("Should not contain banned stop place type", bannedStopPlaceTypesOutside.isEmpty(), is(true));
    }

    /**
     * Test real life example from ninkasi
     */
    @Test
    public void testNSBEditStopsRoleAssignmentsOnlyRail() {
        roleAssignmentsForRailAndRailReplacementMocked(ROLE_EDIT_STOPS);
        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);
        boolean authorized = authorizationService.canEditEntities( Arrays.asList(stopPlace));
        assertThat("type rail station should be allowed", authorized, is(true));
    }

    @Test
    public void testNSBEditStopsRoleAssignmentsOnlyRailReplacementBus() {
        roleAssignmentsForRailAndRailReplacementMocked(ROLE_EDIT_STOPS);
        StopPlace stopPlace = new StopPlace();
        stopPlace.setBusSubmode(BusSubmodeEnumeration.RAIL_REPLACEMENT_BUS);
        boolean authorized = authorizationService.canEditEntities( Arrays.asList(stopPlace));
        assertThat("rail replacement bus should be allowed", authorized, is(true));
    }

    @Test
    public void testNSBEditStopsRoleAssignmentsRailAndReplacementBus() {
        roleAssignmentsForRailAndRailReplacementMocked(ROLE_EDIT_STOPS);
        StopPlace stopPlace = new StopPlace();
        stopPlace.setBusSubmode(BusSubmodeEnumeration.RAIL_REPLACEMENT_BUS);
        stopPlace.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);
        boolean authorized = authorizationService.canEditEntities( Arrays.asList(stopPlace));
        assertThat("rail replacement bus and rail station should not both be set in real life. Role assignments are OR-ed. So should give true.", authorized, is(true));
    }

    @Test
    public void testNSBEditStopsRoleAssignmentsWaterSubmode() {
        roleAssignmentsForRailAndRailReplacementMocked(ROLE_EDIT_STOPS);
        StopPlace stopPlace = new StopPlace();
        stopPlace.setWaterSubmode(WaterSubmodeEnumeration.AIRPORT_BOAT_LINK);
        boolean authorized = authorizationService.canEditEntities( Arrays.asList(stopPlace));
        assertThat("submode airport boat link not allowed", authorized, is(false));
    }

    @Test
    public void testNSBEditStopsRoleAssignmentsBusStation() {
        roleAssignmentsForRailAndRailReplacementMocked(ROLE_EDIT_STOPS);
        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        boolean authorized = authorizationService.canEditEntities( Arrays.asList(stopPlace));
        assertThat("bus station not allowed when sub mode not set", authorized, is(false));
    }

    /**
     * Test if user has multiple roles
     * A User has roles for editing stops TopographicPlace 1 and 2
     */
    @Test
    public void testUserWithMultipleRoles() {
        final List<RoleAssignment> roleAssignments = roleAssignmentsMultipleRoles();

        Point point = geometryFactory.createPoint(new Coordinate(9.536819, 61.772281));
        Point point2 = geometryFactory.createPoint(new Coordinate(23.682516, 70.664175));

        TopographicPlace municipality = new TopographicPlace();
        municipality.setNetexId("KVE:TopographicalPlace:01");
        municipality.setVersion(1);
        municipality.setPolygon(createPolygon(point));
        topographicPlaceRepository.saveAndFlush(municipality);

        TopographicPlace municipality2 = new TopographicPlace();
        municipality2.setNetexId("KVE:TopographicalPlace:02");
        municipality2.setVersion(1);
        municipality2.setPolygon(createPolygon(point2));
        topographicPlaceRepository.saveAndFlush(municipality2);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        stopPlace.setBusSubmode(BusSubmodeEnumeration.REGIONAL_BUS);
        stopPlace.setTopographicPlace(municipality);
        stopPlace.setCentroid(point);
        stopPlaceRepository.saveAndFlush(stopPlace);

        StopPlace stopPlace2 = new StopPlace();
        stopPlace2.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        stopPlace2.setBusSubmode(BusSubmodeEnumeration.REGIONAL_BUS);
        stopPlace2.setTopographicPlace(municipality2);
        stopPlace2.setCentroid(point2);

        stopPlaceRepository.saveAndFlush(stopPlace2);

        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignments);
        assertThat("Should be authorized as both type and submode are allowed", authorizationService.canEditEntity(stopPlace), is(true));
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignments);
        assertThat("Should be authorized as both type and submode are allowed", authorizationService.canDeleteEntity(stopPlace), is(true));

        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignments);
        assertThat("Should be authorized as both type and submode are allowed", authorizationService.canEditEntity(stopPlace2), is(true));
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignments);
        assertThat("Should be authorized as both type and submode are allowed", authorizationService.canDeleteEntity(stopPlace2), is(false));

    }

    private List<RoleAssignment> roleAssignmentsForRailAndRailReplacementMocked(String role) {
        List<RoleAssignment> roleAssignments = Arrays.asList(RoleAssignment.builder().withRole(role)
                        .withOrganisation("NSB")
                        .withEntityClassification(ENTITY_TYPE, "StopPlace")
                        .withEntityClassification("StopPlaceType", "railStation")
                        .build(),
                RoleAssignment.builder().withRole(role)
                        .withOrganisation("NSB")
                        .withEntityClassification(ENTITY_TYPE, "StopPlace")
                        .withEntityClassification("Submode", "railReplacementBus")
                        .build());
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignments);
        return roleAssignments;
    }

    private List<RoleAssignment> roleAssignmentsMultipleRoles() {
        return Arrays.asList(RoleAssignment.builder()
                .withRole(ROLE_EDIT_STOPS)
                .withOrganisation("OST")
                .withAdministrativeZone("KVE:TopographicalPlace:01")
                .withEntityClassification(ENTITY_TYPE, "StopPlace")
                .withEntityClassification("StopPlaceType", "!airport")
                .withEntityClassification("StopPlaceType", "!railStation")
                .withEntityClassification("Submode", "!railReplacementBus")
                .build(),
                RoleAssignment.builder()
                        .withRole(ROLE_DELETE_STOPS)
                        .withOrganisation("OST")
                        .withAdministrativeZone("KVE:TopographicalPlace:01")
                        .withEntityClassification(ENTITY_TYPE, "StopPlace")
                        .withEntityClassification("StopPlaceType", "!airport")
                        .withEntityClassification("StopPlaceType", "!railStation")
                        .withEntityClassification("Submode", "!railReplacementBus")
                        .build(),
                RoleAssignment.builder()
                .withRole(ROLE_EDIT_STOPS)
                .withOrganisation("OST")
                .withAdministrativeZone("KVE:TopographicalPlace:02")
                .withEntityClassification(ENTITY_TYPE, "StopPlace")
                .withEntityClassification("StopPlaceType", "!airport")
                .withEntityClassification("StopPlaceType", "!railStation")
                .withEntityClassification("Submode", "!railReplacementBus")
                .build()
                );

    }

    private Polygon createPolygon(Point point) {
        Geometry bufferedPoint = point.buffer(10);
        LinearRing linearRing = new LinearRing(new CoordinateArraySequence(bufferedPoint.getCoordinates()), geometryFactory);
        return geometryFactory.createPolygon(linearRing, null);
    }

}
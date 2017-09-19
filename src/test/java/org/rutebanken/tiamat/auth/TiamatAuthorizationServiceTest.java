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

import org.junit.Test;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ENTITY_TYPE;

@Transactional // Because of the authorization service logs entities which could read lazy loaded fields
public class TiamatAuthorizationServiceTest extends TiamatIntegrationTest {

    @Autowired
    private ReflectionAuthorizationService reflectionAuthorizationService;

    @Autowired
    private MockedRoleAssignmentExtractor mockedRoleAssignmentExtractor;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Test
    public void authorizedForStopPlaceTypeWhenOthersBlacklisted() {

        RoleAssignment roleAssignment = RoleAssignment.builder()
                .withRole("editStops")
//                .withAdministrativeZone("01")
                .withOrganisation("OST")
                .withEntityClassification(ENTITY_TYPE, "StopPlace")
                .withEntityClassification("StopPlaceType", "!airport")
                .withEntityClassification("StopPlaceType", "!railStation")
                .build();

        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);

        boolean authorized = reflectionAuthorizationService.authorized(roleAssignment, stopPlace, roleAssignment.r);
        assertThat(authorized, is(true));
    }

    @Test
    public void authorizedByQuay() {

        RoleAssignment roleAssignment = RoleAssignment.builder()
                .withRole("editStops")
                .withOrganisation("OST")
                .withEntityClassification(ENTITY_TYPE, "StopPlace")
                .withEntityClassification("StopPlaceType", "onstreetBus")
                .build();

        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);

        Quay quay = new Quay();
        stopPlace.getQuays().add(quay);

        stopPlaceRepository.save(stopPlace);

        boolean authorized = reflectionAuthorizationService.authorized(roleAssignment, quay, roleAssignment.r);
        assertThat(authorized, is(true));
    }

    @Test
    public void notAuthorizedForBlacklistedStopPlaceTypes() {

        RoleAssignment roleAssignment = RoleAssignment.builder()
                .withRole("editStops")
                .withOrganisation("OST")
                .withEntityClassification(ENTITY_TYPE, "StopPlace")
                .withEntityClassification("StopPlaceType", "!airport")
                .withEntityClassification("StopPlaceType", "!railStation")
                .build();

        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);
        boolean authorized = reflectionAuthorizationService.authorized(roleAssignment, stopPlace, roleAssignment.r);
        assertThat(authorized, is(false));
    }

    /**
     * EntityType=StopPlace, StopPlaceType=!railStation,!airport, Submode=!railReplacementBus
     */
    @Test
    public void notAuthorizedWithSubmodeAndType() {
        RoleAssignment roleAssignment = RoleAssignment.builder()
                .withRole("editStops")
                .withOrganisation("OST")
                .withEntityClassification(ENTITY_TYPE, "StopPlace")
                .withEntityClassification("StopPlaceType", "!airport")
                .withEntityClassification("Submode", "!railReplacementBus")
                .build();

        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.AIRPORT);
        stopPlace.setBusSubmode(BusSubmodeEnumeration.RAIL_REPLACEMENT_BUS);

        boolean authorized = reflectionAuthorizationService.authorized(roleAssignment, stopPlace, roleAssignment.r);
        assertThat("Should NOT be authorized as both type and submode does not match", authorized, is(false));
    }

    @Test
    public void authorizedWithSubmodeAndType() {
        RoleAssignment roleAssignment = RoleAssignment.builder()
                .withRole("editStops")
                .withOrganisation("OST")
                .withEntityClassification(ENTITY_TYPE, "StopPlace")
                .withEntityClassification("StopPlaceType", "!airport")
                .withEntityClassification("Submode", "!railReplacementBus")
                .build();

        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        stopPlace.setBusSubmode(BusSubmodeEnumeration.REGIONAL_BUS);

        boolean authorized = reflectionAuthorizationService.authorized(roleAssignment, stopPlace, roleAssignment.r);
        assertThat("Should be authorized as both type and submode are allowed", authorized, is(true));
    }

    /**
     * Test real life example from ninkasi
     */
    @Test
    public void testNSBEditStopsRoleAssignmentsOnlyRail() {
        String role = "editStops";
        roleAssignmentsForRailAndRailReplacementMocked(role);
        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);
        boolean authorized = reflectionAuthorizationService.isAuthorized(role, Arrays.asList(stopPlace));
        assertThat("type rail station should be allowed", authorized, is(true));
    }

    @Test
    public void testNSBEditStopsRoleAssignmentsOnlyRailReplacementBus() {
        String role = "editStops";
        roleAssignmentsForRailAndRailReplacementMocked(role);
        StopPlace stopPlace = new StopPlace();
        stopPlace.setBusSubmode(BusSubmodeEnumeration.RAIL_REPLACEMENT_BUS);
        boolean authorized = reflectionAuthorizationService.isAuthorized(role, Arrays.asList(stopPlace));
        assertThat("rail replacement bus should be allowed", authorized, is(true));
    }

    @Test
    public void testNSBEditStopsRoleAssignmentsRailAndReplacementBus() {
        String role = "editStops";
        roleAssignmentsForRailAndRailReplacementMocked(role);
        StopPlace stopPlace = new StopPlace();
        stopPlace.setBusSubmode(BusSubmodeEnumeration.RAIL_REPLACEMENT_BUS);
        stopPlace.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);
        boolean authorized = reflectionAuthorizationService.isAuthorized(role, Arrays.asList(stopPlace));
        assertThat("rail replacement bus and rail station should not both be set in real life. Role assignments are OR-ed. So should give true.", authorized, is(true));
    }

    @Test
    public void testNSBEditStopsRoleAssignmentsWaterSubmode() {
        String role = "editStops";
        roleAssignmentsForRailAndRailReplacementMocked(role);
        StopPlace stopPlace = new StopPlace();
        stopPlace.setWaterSubmode(WaterSubmodeEnumeration.AIRPORT_BOAT_LINK);
        boolean authorized = reflectionAuthorizationService.isAuthorized(role, Arrays.asList(stopPlace));
        assertThat("submode airport boat link not allowed", authorized, is(false));
    }

    @Test
    public void testNSBEditStopsRoleAssignmentsBusStation() {
        String role = "editStops";
        roleAssignmentsForRailAndRailReplacementMocked(role);
        StopPlace stopPlace = new StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        boolean authorized = reflectionAuthorizationService.isAuthorized(role, Arrays.asList(stopPlace));
        assertThat("bus station not allowed when sub mode not set", authorized, is(false));
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
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignmentList(roleAssignments);
        return roleAssignments;
    }

}
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
import org.rutebanken.tiamat.auth.check.TiamatOriganisationChecker;
import org.rutebanken.tiamat.auth.check.TopographicPlaceChecker;
import org.rutebanken.tiamat.config.AuthorizationServiceConfig;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.service.stopplace.MultiModalStopPlaceEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ENTITY_TYPE;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;


/**
 * Testing authorization for generic use cases is done in {@link TiamatAuthorizationServiceTest}.
 * This test class covers special cases unique to stop places.
 */
public class StopPlaceAuthorizationServiceTest extends TiamatIntegrationTest {

    @Autowired
    private StopPlaceAuthorizationService stopPlaceAuthorizationService;

    @Autowired
    private MockedRoleAssignmentExtractor mockedRoleAssignmentExtractor;

    @Autowired
    private TopographicPlaceChecker topographicPlaceChecker;

    @Autowired
    private TiamatOriganisationChecker tiamatOriganisationChecker;

    @Autowired
    private TiamatEntityResolver tiamatEntityResolver;

    @Autowired
    private MultiModalStopPlaceEditor multiModalStopPlaceEditor;


    private static final RoleAssignment ADMIN =
            RoleAssignment.builder()
                .withRole(ROLE_EDIT_STOPS)
                .withOrganisation("OST")
                .withEntityClassification(ENTITY_TYPE, "StopPlace")
                .build();

    private final ReflectionAuthorizationService reflectionAuthorizationService;



    public StopPlaceAuthorizationServiceTest() {

        this.reflectionAuthorizationService = new AuthorizationServiceConfig().getAuthorizationService(
                mockedRoleAssignmentExtractor,
                true,
                tiamatOriganisationChecker,
                topographicPlaceChecker,
                tiamatEntityResolver);
    }


    @Test
    public void authorizedOnstreetBusWhenAccessToOnstreetBus() {

        // Setup using admin role assignment
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(ADMIN);

        StopPlace onstreetBus = new StopPlace(new EmbeddableMultilingualString("onstreetBus"));
        onstreetBus.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);

        StopPlace railStation = new StopPlace(new EmbeddableMultilingualString("railStation"));
        railStation.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);

        StopPlace railReplacementBus = new StopPlace(new EmbeddableMultilingualString("railReplacementBus"));
        railReplacementBus.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        railReplacementBus.setBusSubmode(BusSubmodeEnumeration.RAIL_REPLACEMENT_BUS);

        List<StopPlace> childStops = Arrays.asList(onstreetBus, railStation, railReplacementBus);
        stopPlaceRepository.save(childStops);

        StopPlace existingVersion = multiModalStopPlaceEditor.createMultiModalParentStopPlace(
                childStops.stream().map(s -> s.getNetexId()).collect(Collectors.toList()),
                new EmbeddableMultilingualString("Multi modal stop placee"));


        // This user can only edit
        RoleAssignment roleAssignment = RoleAssignment.builder()
                .withRole(ROLE_EDIT_STOPS)
                .withOrganisation("OST")
                .withEntityClassification(ENTITY_TYPE, "StopPlace")
                .withEntityClassification("StopPlaceType", "!airport")
                .withEntityClassification("StopPlaceType", "!railStation")
                .build();

        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignment);

        StopPlace newVersion = stopPlaceVersionedSaverService.createCopy(existingVersion, StopPlace.class);
        newVersion.getChildren().removeIf(child -> !child.getNetexId().equals(onstreetBus.getNetexId()));

        stopPlaceAuthorizationService.assertEditAuthorized(existingVersion, newVersion);
    }

    @Test
    public void authorizedRailStationChildWhenAccessToRailStation() {

        // Setup using admin role assignment
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(ADMIN);

        StopPlace onstreetBus = new StopPlace(new EmbeddableMultilingualString("onstreetBus"));
        onstreetBus.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);

        StopPlace railStation = new StopPlace(new EmbeddableMultilingualString("railStation"));
        railStation.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);

        StopPlace railReplacementBus = new StopPlace(new EmbeddableMultilingualString("railReplacementBus"));
        railReplacementBus.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        railReplacementBus.setBusSubmode(BusSubmodeEnumeration.RAIL_REPLACEMENT_BUS);

        List<StopPlace> childStops = Arrays.asList(onstreetBus, railStation, railReplacementBus);
        stopPlaceRepository.save(childStops);

        StopPlace existingVersion = multiModalStopPlaceEditor.createMultiModalParentStopPlace(
                childStops.stream().map(s -> s.getNetexId()).collect(Collectors.toList()),
                new EmbeddableMultilingualString("Multi modal stop placee"));


        // This user can only edit
        RoleAssignment roleAssignment = RoleAssignment.builder()
                .withRole(ROLE_EDIT_STOPS)
                .withOrganisation("OST")
                .withEntityClassification(ENTITY_TYPE, "StopPlace")
                .withEntityClassification("StopPlaceType", "railStation")
                .build();

        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignment);

        StopPlace newVersion = stopPlaceVersionedSaverService.createCopy(existingVersion, StopPlace.class);
        newVersion.getChildren().removeIf(child -> !child.getNetexId().equals(railStation.getNetexId()));

        stopPlaceAuthorizationService.assertEditAuthorized(existingVersion, newVersion);
    }

    @Test
    public void notAuthorizedOnstreetBusChildWhenAccessToRailStationOnly() {

        // Setup using admin role assignment
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(ADMIN);

        StopPlace onstreetBus = new StopPlace(new EmbeddableMultilingualString("onstreetBus"));
        onstreetBus.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);

        StopPlace railStation = new StopPlace(new EmbeddableMultilingualString("railStation"));
        railStation.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);

        StopPlace railReplacementBus = new StopPlace(new EmbeddableMultilingualString("railReplacementBus"));
        railReplacementBus.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        railReplacementBus.setBusSubmode(BusSubmodeEnumeration.RAIL_REPLACEMENT_BUS);

        List<StopPlace> childStops = Arrays.asList(onstreetBus, railStation, railReplacementBus);
        stopPlaceRepository.save(childStops);

        StopPlace existingVersion = multiModalStopPlaceEditor.createMultiModalParentStopPlace(
                childStops.stream().map(s -> s.getNetexId()).collect(Collectors.toList()),
                new EmbeddableMultilingualString("Multi modal stop placee"));


        // This user can only edit
        RoleAssignment roleAssignment = RoleAssignment.builder()
                .withRole(ROLE_EDIT_STOPS)
                .withOrganisation("OST")
                .withEntityClassification(ENTITY_TYPE, "StopPlace")
                .withEntityClassification("StopPlaceType", "railStation")
                .build();

        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignment);

        StopPlace newVersion = stopPlaceVersionedSaverService.createCopy(existingVersion, StopPlace.class);
        newVersion.getChildren().removeIf(child -> !child.getNetexId().equals(onstreetBus.getNetexId()));

        assertThatThrownBy(() ->
                stopPlaceAuthorizationService.assertEditAuthorized(existingVersion, newVersion))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    public void notAllowedToSetTerminationDateWhenNoAccessToAllChildren() {

        // Setup using admin role assignment
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(ADMIN);

        StopPlace onstreetBus = new StopPlace(new EmbeddableMultilingualString("onstreetBus"));
        onstreetBus.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);

        StopPlace railStation = new StopPlace(new EmbeddableMultilingualString("railStation"));
        railStation.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);

        StopPlace railReplacementBus = new StopPlace(new EmbeddableMultilingualString("railReplacementBus"));
        railReplacementBus.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        railReplacementBus.setBusSubmode(BusSubmodeEnumeration.RAIL_REPLACEMENT_BUS);

        List<StopPlace> childStops = Arrays.asList(onstreetBus, railStation, railReplacementBus);
        stopPlaceRepository.save(childStops);

        StopPlace existingVersion = multiModalStopPlaceEditor.createMultiModalParentStopPlace(
                childStops.stream().map(s -> s.getNetexId()).collect(Collectors.toList()),
                new EmbeddableMultilingualString("Multi modal stop placee"));


        // This user can only edit
        RoleAssignment roleAssignment = RoleAssignment.builder()
                .withRole(ROLE_EDIT_STOPS)
                .withOrganisation("OST")
                .withEntityClassification(ENTITY_TYPE, "StopPlace")
                .withEntityClassification("StopPlaceType", "onstreetBus")
                .build();

        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignment);

        StopPlace newVersion = stopPlaceVersionedSaverService.createCopy(existingVersion, StopPlace.class);
        newVersion.getChildren().removeIf(child -> !child.getNetexId().equals(onstreetBus.getNetexId()));

        newVersion.setValidBetween(new ValidBetween(null, Instant.now()));

        assertThatThrownBy(() ->
                stopPlaceAuthorizationService.assertEditAuthorized(existingVersion, newVersion))
                .isInstanceOf(AccessDeniedException.class);
    }

}
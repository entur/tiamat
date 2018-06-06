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

import org.junit.Before;
import org.junit.Test;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ENTITY_TYPE;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;


/**
 * This test class covers testing special cases unique to stop places, and multimodal stop places.
 * <p>
 * Testing authorization for generic use cases is done in {@link TiamatAuthorizationServiceTest}.
 */
public class StopPlaceAuthorizationServiceTest extends TiamatIntegrationTest {

    /**
     * Admin role used for creating stops during test setup.
     */
    private static final RoleAssignment ADMIN =
            RoleAssignment.builder()
                    .withRole(ROLE_EDIT_STOPS)
                    .withOrganisation("OST")
                    .withEntityClassification(ENTITY_TYPE, "StopPlace")
                    .build();

    /**
     * Multimodal stop place editor is used for creating test cases.
     */
    @Autowired
    private MultiModalStopPlaceEditor multiModalStopPlaceEditor;

    /**
     * The reflection authorization service is the generic authorization service used by {@link StopPlaceAuthorizationService}
     */
    private ReflectionAuthorizationService reflectionAuthorizationService;

    /**
     * Class being tested
     */
    @Autowired
    private StopPlaceAuthorizationService stopPlaceAuthorizationService;

    @Autowired
    private TiamatEntityResolver tiamatEntityResolver;


    @Autowired
    private TiamatOriganisationChecker tiamatOriganisationChecker;

    @Autowired
    private TopographicPlaceChecker topographicPlaceChecker;

    /**
     * Mocked class for extracting role assignments.
     * <p>
     * Not using {@link MockedRoleAssignmentExtractor} because it resets the returned role assignment on each call.
     * The {@link StopPlaceAuthorizationService} makes several calls.
     */
    private RoleAssignmentExtractor roleAssignmentExtractor;

    /**
     * Set up stopPlaceAuthorizationService with custom roleAssignmentExtractor.
     * Borrowing the config class to get field mappings.
     */
    @Before
    public void StopPlaceAuthorizationServiceTest() {
        roleAssignmentExtractor = mock(RoleAssignmentExtractor.class);

        this.reflectionAuthorizationService = new AuthorizationServiceConfig().getAuthorizationService(
                roleAssignmentExtractor,
                true,
                tiamatOriganisationChecker,
                topographicPlaceChecker,
                tiamatEntityResolver);

        stopPlaceAuthorizationService = new StopPlaceAuthorizationService(reflectionAuthorizationService);
    }

    @Test
    public void authorizedOnstreetBusWhenAccessToOnstreetBus() {

        setRoleAssignmentReturned(ADMIN);

        StopPlace onstreetBus = createOnstreetBus();
        StopPlace railStation = createRailStation();
        StopPlace railReplacementBus = createRailReplacementBus();

        List<StopPlace> childStops = Arrays.asList(onstreetBus, railStation, railReplacementBus);
        stopPlaceRepository.save(childStops);

        StopPlace existingVersion = multiModalStopPlaceEditor.createMultiModalParentStopPlace(
                toIdList(childStops),
                new EmbeddableMultilingualString("Multi modal stop place with onstreetBus, railStation and railReplacementBus"));

        RoleAssignment roleAssignment = canEditAllTypesExcept("airport", "railStation");

        setRoleAssignmentReturned(roleAssignment);

        StopPlace newVersion = stopPlaceVersionedSaverService.createCopy(existingVersion, StopPlace.class);
        removeAllChildrenExcept(newVersion, onstreetBus.getNetexId());

        stopPlaceAuthorizationService.assertEditAuthorized(existingVersion, newVersion);
    }

    @Test
    public void authorizedRailStationChildWhenAccessToRailStation() {

        // Setup using admin role assignment
        setRoleAssignmentReturned(ADMIN);

        StopPlace onstreetBus = createOnstreetBus();
        StopPlace railStation = createRailStation();
        StopPlace railReplacementBus = createRailReplacementBus();

        List<StopPlace> childStops = Arrays.asList(onstreetBus, railStation, railReplacementBus);
        stopPlaceRepository.save(childStops);

        StopPlace existingVersion = multiModalStopPlaceEditor.createMultiModalParentStopPlace(
                toIdList(childStops),
                new EmbeddableMultilingualString("Another multi modal stop place with onstreetBus, railStation and railReplacementBus"));

        RoleAssignment roleAssignment = canOnlyEdit("railStation");

        setRoleAssignmentReturned(roleAssignment);

        StopPlace newVersion = stopPlaceVersionedSaverService.createCopy(existingVersion, StopPlace.class);

        removeAllChildrenExcept(newVersion, railStation.getNetexId());

        stopPlaceAuthorizationService.assertEditAuthorized(existingVersion, newVersion);
    }

    @Test
    public void notAuthorizedOnstreetBusChildWhenAccessToRailStationOnly() {

        // Setup using admin role assignment
        setRoleAssignmentReturned(ADMIN);

        StopPlace onstreetBus = createOnstreetBus();
        StopPlace railStation = createRailStation();
        StopPlace railReplacementBus = createRailReplacementBus();

        List<StopPlace> childStops = Arrays.asList(onstreetBus, railStation, railReplacementBus);
        stopPlaceRepository.save(childStops);

        StopPlace existingVersion = multiModalStopPlaceEditor.createMultiModalParentStopPlace(
                toIdList(childStops),
                new EmbeddableMultilingualString("Multiple multi modal stop place with onstreetBus, railStation and railReplacementBus"));

        RoleAssignment roleAssignment = canOnlyEdit("railStation");

        setRoleAssignmentReturned(roleAssignment);

        StopPlace newVersion = stopPlaceVersionedSaverService.createCopy(existingVersion, StopPlace.class);
        removeAllChildrenExcept(newVersion, onstreetBus.getNetexId());

        assertThatThrownBy(() ->
                stopPlaceAuthorizationService.assertEditAuthorized(existingVersion, newVersion))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    public void notAllowedToSetTerminationDateWhenNoAccessToAllChildren() {

        // Setup using admin role assignment
        setRoleAssignmentReturned(ADMIN);

        StopPlace onstreetBus = createOnstreetBus();
        StopPlace railStation = createRailStation();
        StopPlace railReplacementBus = createRailReplacementBus();

        List<StopPlace> childStops = Arrays.asList(onstreetBus, railStation, railReplacementBus);
        stopPlaceRepository.save(childStops);

        StopPlace existingVersion = multiModalStopPlaceEditor.createMultiModalParentStopPlace(
                toIdList(childStops),
                new EmbeddableMultilingualString("Multi modal stop place that should be attempted terminated with validbetween"));

        RoleAssignment roleAssignment = canOnlyEdit("onstreetBus");

        setRoleAssignmentReturned(roleAssignment);

        StopPlace newVersion = stopPlaceVersionedSaverService.createCopy(existingVersion, StopPlace.class);
        removeAllChildrenExcept(newVersion, onstreetBus.getNetexId());

        // Set termination date
        newVersion.setValidBetween(new ValidBetween(null, Instant.now()));

        assertThatThrownBy(() ->
                stopPlaceAuthorizationService.assertEditAuthorized(existingVersion, newVersion))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    public void adminAllowedToTerminate() {

        // Setup using admin role assignment
        setRoleAssignmentReturned(ADMIN);

        StopPlace onstreetBus = createOnstreetBus();
        StopPlace railStation = createRailStation();
        StopPlace railReplacementBus = createRailReplacementBus();

        List<StopPlace> childStops = Arrays.asList(onstreetBus, railStation, railReplacementBus);
        stopPlaceRepository.save(childStops);

        StopPlace existingVersion = multiModalStopPlaceEditor.createMultiModalParentStopPlace(
                toIdList(childStops),
                new EmbeddableMultilingualString("Multi modal stop place that should be terminated by an admin user"));

        StopPlace newVersion = stopPlaceVersionedSaverService.createCopy(existingVersion, StopPlace.class);

        newVersion.setValidBetween(new ValidBetween(null, Instant.now()));
        stopPlaceAuthorizationService.assertEditAuthorized(existingVersion, newVersion);
    }

    private RoleAssignment canOnlyEdit(String stopPlaceType) {
        return RoleAssignment.builder()
                .withRole(ROLE_EDIT_STOPS)
                .withOrganisation("OST")
                .withEntityClassification(ENTITY_TYPE, "StopPlace")
                .withEntityClassification("StopPlaceType", stopPlaceType)
                .build();
    }

    private RoleAssignment canEditAllTypesExcept(String... excludedTypes) {
        RoleAssignment.Builder roleAssignmentBuilder = RoleAssignment.builder()
                .withRole(ROLE_EDIT_STOPS)
                .withOrganisation("OST")
                .withEntityClassification(ENTITY_TYPE, "StopPlace");

        for (String excludedType : excludedTypes) {
            roleAssignmentBuilder.withEntityClassification("StopPlaceType", "!" + excludedType);
        }

        return roleAssignmentBuilder.build();
    }

    private List<String> toIdList(List<StopPlace> children) {
        return children.stream().map(s -> s.getNetexId()).collect(Collectors.toList());
    }

    private void setRoleAssignmentReturned(RoleAssignment roleAssignment) {

        List<RoleAssignment> roleAssignments = Arrays.asList(roleAssignment);
        when(roleAssignmentExtractor.getRoleAssignmentsForUser()).thenReturn(roleAssignments);
        when(roleAssignmentExtractor.getRoleAssignmentsForUser(any())).thenReturn(roleAssignments);
    }

    private void removeAllChildrenExcept(StopPlace parentStopPlace, String exceptThisNetexId) {
        parentStopPlace.getChildren().removeIf(child -> !child.getNetexId().equals(exceptThisNetexId));
    }

    private StopPlace createOnstreetBus() {
        StopPlace onstreetBus = new StopPlace(new EmbeddableMultilingualString("onstreetBus"));
        onstreetBus.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        return onstreetBus;
    }

    private StopPlace createRailStation() {
        StopPlace railStation = new StopPlace(new EmbeddableMultilingualString("railStation"));
        railStation.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);
        return railStation;
    }

    private StopPlace createRailReplacementBus() {
        StopPlace railReplacementBus = new StopPlace(new EmbeddableMultilingualString("railReplacementBus"));
        railReplacementBus.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        railReplacementBus.setBusSubmode(BusSubmodeEnumeration.RAIL_REPLACEMENT_BUS);
        return railReplacementBus;
    }
}
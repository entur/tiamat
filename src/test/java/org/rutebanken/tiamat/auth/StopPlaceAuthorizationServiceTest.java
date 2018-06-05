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
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.auth.check.TiamatOriganisationChecker;
import org.rutebanken.tiamat.auth.check.TopographicPlaceChecker;
import org.rutebanken.tiamat.config.AuthorizationServiceConfig;
import org.rutebanken.tiamat.model.BusSubmodeEnumeration;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.service.stopplace.MultiModalStopPlaceEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    public void authorizedForStopPlaceTypeWhenOthersBlacklisted() {

        RoleAssignment roleAssignment = RoleAssignment.builder()
                .withRole(ROLE_EDIT_STOPS)
                .withOrganisation("OST")
                .withEntityClassification(ENTITY_TYPE, "StopPlace")
                .withEntityClassification("StopPlaceType", "!airport")
                .withEntityClassification("StopPlaceType", "!railStation")
                .build();

        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(roleAssignment);


        StopPlace onstreetBus = new StopPlace();
        onstreetBus.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);

        StopPlace railStation = new StopPlace();
        railStation.setStopPlaceType(StopTypeEnumeration.RAIL_STATION);

        StopPlace railReplacementBus = new StopPlace();
        railReplacementBus.setStopPlaceType(StopTypeEnumeration.ONSTREET_BUS);
        railReplacementBus.setBusSubmode(BusSubmodeEnumeration.RAIL_REPLACEMENT_BUS);

        List<StopPlace> childStops = Arrays.asList(onstreetBus, railStation, railReplacementBus);
        stopPlaceRepository.save(childStops);

        StopPlace multiModalStopPlacee = multiModalStopPlaceEditor.createMultiModalParentStopPlace(
                childStops.stream().map(s -> s.getNetexId()).collect(Collectors.toList()),
                new EmbeddableMultilingualString("Multi modal stop placee"));


        stopPlaceAuthorizationService.assertAuthorized(ROLE_EDIT_STOPS, multiModalStopPlacee);
    }

}
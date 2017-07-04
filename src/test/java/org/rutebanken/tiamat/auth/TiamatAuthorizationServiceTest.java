package org.rutebanken.tiamat.auth;

import org.junit.Test;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ENTITY_TYPE;

public class TiamatAuthorizationServiceTest extends TiamatIntegrationTest {

    @Autowired
    private ReflectionAuthorizationService reflectionAuthorizationService;


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

}
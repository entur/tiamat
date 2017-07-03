package org.rutebanken.tiamat.auth;

import org.junit.Test;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ENTITY_TYPE;

public class TiamatAuthorizationServiceTest extends TiamatIntegrationTest {

    @Autowired
    private TiamatAuthorizationService tiamatAuthorizationService;


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

        boolean authorized = tiamatAuthorizationService.authorized(roleAssignment, stopPlace, roleAssignment.r);
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
        boolean authorized = tiamatAuthorizationService.authorized(roleAssignment, stopPlace, roleAssignment.r);
        assertThat(authorized, is(false));
    }

}
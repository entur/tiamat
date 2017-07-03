package org.rutebanken.tiamat.auth;

import org.rutebanken.helper.organisation.AuthorizationConstants;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.ParkingTypeEnumeration;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;

import java.util.ArrayList;
import java.util.List;

public class RoleAssignmentListBuilder {

    private List<RoleAssignment> roleAssignments = new ArrayList<>();

    public static RoleAssignmentListBuilder builder() {
        return new RoleAssignmentListBuilder();
    }

    public List<RoleAssignment> build() {
        return roleAssignments;
    }

    public RoleAssignmentListBuilder withAccessAllAreas() {
        return withStopPlaceOfType(AuthorizationConstants.ENTITY_CLASSIFIER_ALL_TYPES)
                .withParkingOfType(AuthorizationConstants.ENTITY_CLASSIFIER_ALL_TYPES);
    }


    public RoleAssignmentListBuilder withStopPlaceOfType(StopTypeEnumeration type) {
        return withStopPlaceOfType(type.value());
    }

    public RoleAssignmentListBuilder withStopPlaceOfType(String type) {
        RoleAssignment allStopPlaceAccess = RoleAssignment.builder().withRole(AuthorizationConstants.ROLE_EDIT_STOPS)
                .withOrganisation("NOT_YET_CHECKED")
                .withEntityClassification(AuthorizationConstants.ENTITY_TYPE, StopPlace.class.getSimpleName())
                .withEntityClassification("StopPlaceType", type)
                .build();

        roleAssignments.add(allStopPlaceAccess);
        return this;
    }

    public RoleAssignmentListBuilder withParkingOfType(ParkingTypeEnumeration type) {
        return withParkingOfType(type.value());
    }

    public RoleAssignmentListBuilder withParkingOfType(String type) {
        RoleAssignment allParkings = RoleAssignment.builder().withRole(AuthorizationConstants.ROLE_EDIT_STOPS)
                .withOrganisation("NOT_YET_CHECKED")
                .withEntityClassification(AuthorizationConstants.ENTITY_TYPE, Parking.class.getSimpleName())
                .withEntityClassification("parkingType", type)
                .build();

        roleAssignments.add(allParkings);
        return this;
    }


}

package org.rutebanken.tiamat.auth;

import org.rutebanken.helper.organisation.AuthorizationConstants;
import org.rutebanken.helper.organisation.RoleAssignment;
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
        return withRole(AuthorizationConstants.ROLE_EDIT_STOPS, AuthorizationConstants.ENTITY_CLASSIFIER_ALL_TYPES);
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

    private RoleAssignmentListBuilder withRole(String roleName, String entityType) {
        RoleAssignment roleAssignment = RoleAssignment.builder().withRole(roleName)
                                                .withOrganisation("NOT_YET_CHECKED")
                                                .withEntityClassification(AuthorizationConstants.ENTITY_TYPE, entityType)
                                                .build();

        roleAssignments.add(roleAssignment);
        return this;
    }


}

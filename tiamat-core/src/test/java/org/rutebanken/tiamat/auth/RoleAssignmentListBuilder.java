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
        return withRole(AuthorizationConstants.ROLE_EDIT_STOPS, AuthorizationConstants.ENTITY_CLASSIFIER_ALL_TYPES)
                .withRole(AuthorizationConstants.ROLE_DELETE_STOPS, AuthorizationConstants.ENTITY_CLASSIFIER_ALL_TYPES);
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

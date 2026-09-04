package org.rutebanken.tiamat.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rutebanken.helper.organisation.RoleAssignment;

import java.util.List;

class DefaultAuthorizationServiceTest {

    @Test
    void canEditAllEntities() {
        List<RoleAssignment> roleAssignments = RoleAssignmentListBuilder.builder().withAccessAllAreas().build();
        DefaultAuthorizationService defaultAuthorizationService = new DefaultAuthorizationService(null,false, null, null, null);
        Assertions.assertTrue(defaultAuthorizationService.verifyCanEditAllEntities(roleAssignments));
    }

    @Test
    void canEditAllEntitiesMissingRoleAssignment() {
        List<RoleAssignment> roleAssignments = RoleAssignmentListBuilder.builder().build();
        DefaultAuthorizationService defaultAuthorizationService = new DefaultAuthorizationService(null,false, null, null, null);
        Assertions.assertFalse(defaultAuthorizationService.verifyCanEditAllEntities(roleAssignments));

    }

    /**
     * Disabled authorization is permissive everywhere else in the stack, for instance
     * PublicationDeliveryImporter only consults the authorization service when it is enabled.
     * The write API is gated by @PreAuthorize on canUseWriteApi(), which has no such call site
     * guard, so failing closed here makes the whole API return 403 in a deployment that
     * deliberately turned authorization off.
     */
    @Test
    void canUseWriteApiWhenAuthorizationIsDisabled() {
        DefaultAuthorizationService defaultAuthorizationService = new DefaultAuthorizationService(null, false, null, null, null);
        Assertions.assertTrue(defaultAuthorizationService.canUseWriteApi());
    }
}
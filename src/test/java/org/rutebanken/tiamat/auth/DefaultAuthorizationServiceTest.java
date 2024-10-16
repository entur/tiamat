package org.rutebanken.tiamat.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

class DefaultAuthorizationServiceTest {

    @Test
    void verifyCanEditAllEntities() {
        List<RoleAssignment> roleAssignments = RoleAssignmentListBuilder.builder().withAccessAllAreas().build();
        DefaultAuthorizationService defaultAuthorizationService = new DefaultAuthorizationService(null, null, null);
        Assertions.assertDoesNotThrow(() -> defaultAuthorizationService.verifyCanEditAllEntities(roleAssignments));
    }

    @Test
    void verifyCanEditAllEntitiesMissingRoleAssignment() {
        List<RoleAssignment> roleAssignments = RoleAssignmentListBuilder.builder().build();
        DefaultAuthorizationService defaultAuthorizationService = new DefaultAuthorizationService(null, null, null);
        Assertions.assertThrows(AccessDeniedException.class, () -> defaultAuthorizationService.verifyCanEditAllEntities(roleAssignments));
    }
}
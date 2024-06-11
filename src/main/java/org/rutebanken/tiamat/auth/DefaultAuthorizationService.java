package org.rutebanken.tiamat.auth;

import org.apache.commons.lang3.StringUtils;
import org.rutebanken.helper.organisation.AuthorizationConstants;
import org.rutebanken.helper.organisation.DataScopedAuthorizationService;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.rutebanken.tiamat.model.EntityStructure;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.rutebanken.helper.organisation.AuthorizationConstants.*;

public class DefaultAuthorizationService implements AuthorizationService {
    private final DataScopedAuthorizationService dataScopedAuthorizationService;
    private final RoleAssignmentExtractor roleAssignmentExtractor;

    public DefaultAuthorizationService(DataScopedAuthorizationService dataScopedAuthorizationService, RoleAssignmentExtractor roleAssignmentExtractor) {
        this.dataScopedAuthorizationService = dataScopedAuthorizationService;
        this.roleAssignmentExtractor = roleAssignmentExtractor;
        }

    @Override
    public void verifyCanEditAllEntities() {
        verifyCanEditAllEntities(roleAssignmentExtractor.getRoleAssignmentsForUser());
    }

    void verifyCanEditAllEntities(List<RoleAssignment> roleAssignments) {
        if (roleAssignments
                .stream()
                .noneMatch(roleAssignment -> ROLE_EDIT_STOPS.equals(roleAssignment.getRole())
                        && roleAssignment.getEntityClassifications() != null
                        && roleAssignment.getEntityClassifications().get(AuthorizationConstants.ENTITY_TYPE) != null
                        && roleAssignment.getEntityClassifications().get(AuthorizationConstants.ENTITY_TYPE).contains(ENTITY_CLASSIFIER_ALL_ATTRIBUTES)
                        && StringUtils.isEmpty(roleAssignment.getAdministrativeZone())
                )) {
            throw new AccessDeniedException("Insufficient privileges for operation");
        }
    }

    @Override
    public boolean canEditEntities(Collection<? extends EntityStructure> entities) {
        return dataScopedAuthorizationService.isAuthorized(ROLE_EDIT_STOPS, entities);
    }

    @Override
    public <T extends EntityStructure> boolean canEditEntity(RoleAssignment roleAssignment, T entity) {
        return dataScopedAuthorizationService.authorized(roleAssignment, entity, ROLE_EDIT_STOPS);
    }

    @Override
    public void verifyCanEditEntities(Collection<? extends EntityStructure> entities) {
        dataScopedAuthorizationService.assertAuthorized(ROLE_EDIT_STOPS, entities);
    }

    @Override
    public void verifyCanDeleteEntities(Collection<? extends EntityStructure> entities) {
        dataScopedAuthorizationService.assertAuthorized(ROLE_DELETE_STOPS, entities);

    }

    @Override
    public <T extends EntityStructure> Set<String> getRelevantRolesForEntity(T entity) {
        return dataScopedAuthorizationService.getRelevantRolesForEntity(entity);
    }


}

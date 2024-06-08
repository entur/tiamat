package org.rutebanken.tiamat.auth;

import org.rutebanken.helper.organisation.DataScopedAuthorizationService;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.model.EntityStructure;

import java.util.Collection;
import java.util.Set;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_DELETE_STOPS;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;

public class DefaultAuthorizationService implements AuthorizationService {
    private final DataScopedAuthorizationService dataScopedAuthorizationService;

    public DefaultAuthorizationService(DataScopedAuthorizationService dataScopedAuthorizationService) {
        this.dataScopedAuthorizationService = dataScopedAuthorizationService;
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

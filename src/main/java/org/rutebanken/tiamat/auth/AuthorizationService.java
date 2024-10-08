package org.rutebanken.tiamat.auth;

import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.model.EntityStructure;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collection;
import java.util.Set;

/**
 * Authorize operations for the current user.
 */
public interface AuthorizationService {

    /**
     * Verify that the current user have right to edit any entity?
     */
    void verifyCanEditAllEntities();


    /**
     * Does the current user have edit right on all the given entities?
     */
    boolean canEditEntities(Collection<? extends EntityStructure> entities);

    /**
     * Verify that the current user has edit right on all the given entities.
     * @throws AccessDeniedException if not.
     */
    void verifyCanEditEntities(Collection<? extends EntityStructure> entities);

    /**
     * Verify that the current user has delete right on all the given entities.
     * @throws AccessDeniedException if not.
     */
    void verifyCanDeleteEntities(Collection<? extends EntityStructure> entities);

    /**
     * Verify that the current user has right to delete the given entity.
     * @throws AccessDeniedException if not.
     */
    boolean canDeleteEntity(EntityStructure entity);

    /**
     * Verify that the current user has right to edit the given entity.
     * @throws AccessDeniedException if not.
     */
    boolean canEditEntity(EntityStructure entity);

    /**
     * Return the subset of the roles that the current user holds that apply to this entity.
     * */
    <T extends EntityStructure> Set<String> getRelevantRolesForEntity(T entity);

    /**
     * Does the role assignment give edit right on the given entity?
     * (for unit tests only)
     */
    <T extends EntityStructure> boolean canEditEntity(RoleAssignment roleAssignment, T entity);

    Set<String> getAllowedStopPlaceTypes(Object entity);

    Set<String> getBannedStopPlaceTypes(Object entity);

    Set<String> getAllowedSubmodes(Object entity);

    Set<String> getBannedSubmodes(Object entity);



}

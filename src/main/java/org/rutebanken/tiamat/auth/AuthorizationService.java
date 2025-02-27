package org.rutebanken.tiamat.auth;

import org.locationtech.jts.geom.Point;
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
    boolean verifyCanEditAllEntities();


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
     */
    boolean canDeleteEntity(EntityStructure entity);

    /**
     * Verify that the current user has right to edit the given entity.
     */
    boolean canEditEntity(EntityStructure entity);

    boolean canEditEntity(Point point);

    /**
     * Does the role assignment give edit right on the given entity?
     * (for unit tests only)
     */
    <T extends EntityStructure> boolean canEditEntity(RoleAssignment roleAssignment, T entity);

    Set<String> getAllowedStopPlaceTypes(EntityStructure entity);

    Set<String> getLocationAllowedStopPlaceTypes(boolean canEdit, Point point);

    Set<String> getBannedStopPlaceTypes(EntityStructure entity);

    Set<String> getLocationBannedStopPlaceTypes(boolean canEdit,Point point);

    Set<String> getAllowedSubmodes(EntityStructure entity);

    Set<String> getLocationAllowedSubmodes(boolean canEdit,Point point);

    Set<String> getBannedSubmodes(EntityStructure entity);

    Set<String> getLocationBannedSubmodes(boolean canEdit,Point point);


    boolean isGuest();
}

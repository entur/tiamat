package org.rutebanken.tiamat.auth;

import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.diff.generic.SubmodeEnumuration;
import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collection;
import java.util.Set;

/**
 * Authorization service interface for managing authorization of users to perform operations
 * on entities.
 */
public interface AuthorizationService {

    /**
     * Checks if the current user has permission to edit any entity.
     *
     * @return {@code true} if the user can edit all entities, otherwise {@code false}.
     */
    boolean canEditAllEntities();

    /**
     * Checks if the current user has permission to edit all the given entities.
     *
     * @param entities The collection of entities to check.
     * @return {@code true} if the user can edit all entities, otherwise {@code false}.
     */
    boolean canEditEntities(Collection<? extends EntityStructure> entities);

    /*
     * Verify that the current user has edit rights on all the given entities.
     *
     * @param entities The collection of entities to verify.
     * @throws AccessDeniedException if the user lacks the necessary permissions.
     */
    void verifyCanEditEntities(Collection<? extends EntityStructure> entities);

    /**
     * Ensures that the current user has delete rights on all the given entities.
     *
     * @param entities The collection of entities to verify.
     * @throws AccessDeniedException if the user lacks the necessary permissions.
     */
    void verifyCanDeleteEntities(Collection<? extends EntityStructure> entities);

    /**
     * Checks if the current user has the right to delete the given entity.
     *
     * @param entity The entity to check.
     * @return {@code true} if the user can delete the entity, otherwise {@code false}.
     */
    boolean canDeleteEntity(EntityStructure entity);

    /**
     * Checks if the current user has the right to edit the given entity.
     *
     * @param entity The entity to check.
     * @return {@code true} if the user can edit the entity, otherwise {@code false}.
     */
    boolean canEditEntity(EntityStructure entity);

    /**
     * Checks if the current user has the right to edit an entity located at the given point.
     *
     * @param point The geographic location of the entity.
     * @return {@code true} if the user can edit the entity at the specified location, otherwise {@code false}.
     */
    boolean canEditEntity(Point point);

    /**
     * Retrieves the set of allowed stop place types for the given entity.
     *
     * @param entity The entity for which allowed stop place types should be retrieved.
     * @return A set of allowed stop place types.
     */
    Set<StopTypeEnumeration> getAllowedStopPlaceTypes(EntityStructure entity);

    /**
     * Retrieves the allowed stop place types for a specific location based on the user's permissions.
     *
     * @param canEdit Whether the user has edit rights at the location.
     * @param point   The geographic location.
     * @return A set of allowed stop place types for the given location.
     */
    Set<StopTypeEnumeration> getLocationAllowedStopPlaceTypes(boolean canEdit, Point point);

    /**
     * Retrieves the set of banned stop place types for the given entity.
     *
     * @param entity The entity for which banned stop place types should be retrieved.
     * @return A set of banned stop place types.
     */
    Set<StopTypeEnumeration> getBannedStopPlaceTypes(EntityStructure entity);

    /**
     * Retrieves the banned stop place types for a specific location based on the user's permissions.
     *
     * @param canEdit Whether the user has edit rights at the location.
     * @param point   The geographic location.
     * @return A set of banned stop place types for the given location.
     */
    Set<StopTypeEnumeration> getLocationBannedStopPlaceTypes(boolean canEdit, Point point);

    /**
     * Retrieves the set of allowed submodes for the given entity.
     *
     * @param entity The entity for which allowed submodes should be retrieved.
     * @return A set of allowed submodes.
     */
    Set<SubmodeEnumuration> getAllowedSubmodes(EntityStructure entity);

    /**
     * Retrieves the allowed submodes for a specific location based on the user's permissions.
     *
     * @param canEdit Whether the user has edit rights at the location.
     * @param point   The geographic location.
     * @return A set of allowed submodes for the given location.
     */
    Set<SubmodeEnumuration> getLocationAllowedSubmodes(boolean canEdit, Point point);

    /**
     * Retrieves the set of banned submodes for the given entity.
     *
     * @param entity The entity for which banned submodes should be retrieved.
     * @return A set of banned submodes.
     */
    Set<SubmodeEnumuration> getBannedSubmodes(EntityStructure entity);

    /**
     * Retrieves the banned submodes for a specific location based on the user's permissions.
     *
     * @param canEdit Whether the user has edit rights at the location.
     * @param point   The geographic location.
     * @return A set of banned submodes for the given location.
     */
    Set<SubmodeEnumuration> getLocationBannedSubmodes(boolean canEdit, Point point);

     /**
      * Checks if the current user is accessing the system as a guest.
      *
      * @return {@code true} if the user is a guest, otherwise {@code false}.
      */
    boolean isGuest();
}

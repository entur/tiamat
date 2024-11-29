package org.rutebanken.tiamat.auth;

import org.apache.commons.lang3.StringUtils;
import org.rutebanken.helper.organisation.AuthorizationConstants;
import org.rutebanken.helper.organisation.DataScopedAuthorizationService;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.rutebanken.tiamat.auth.check.TopographicPlaceChecker;
import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.service.groupofstopplaces.GroupOfStopPlacesMembersResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ENTITY_CLASSIFIER_ALL_ATTRIBUTES;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_DELETE_STOPS;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;

public class DefaultAuthorizationService implements AuthorizationService {
    private final DataScopedAuthorizationService dataScopedAuthorizationService;
    private final boolean authorizationEnabled;
    private final RoleAssignmentExtractor roleAssignmentExtractor;
    private static final String STOP_PLACE_TYPE = "StopPlaceType";
    private static final String SUBMODE = "Submode";
    private final TopographicPlaceChecker topographicPlaceChecker;
    private final GroupOfStopPlacesMembersResolver groupOfStopPlacesMembersResolver;

    public DefaultAuthorizationService(DataScopedAuthorizationService dataScopedAuthorizationService,
                                       boolean authorizationEnabled,
                                       RoleAssignmentExtractor roleAssignmentExtractor,
                                       TopographicPlaceChecker topographicPlaceChecker, GroupOfStopPlacesMembersResolver groupOfStopPlacesMembersResolver) {
        this.dataScopedAuthorizationService = dataScopedAuthorizationService;
        this.authorizationEnabled = authorizationEnabled;
        this.roleAssignmentExtractor = roleAssignmentExtractor;
        this.topographicPlaceChecker = topographicPlaceChecker;
        this.groupOfStopPlacesMembersResolver = groupOfStopPlacesMembersResolver;
    }

    @Override
    public boolean verifyCanEditAllEntities() {
        if(hasNoAuthentications()) {
            return false;
        }
        return verifyCanEditAllEntities(roleAssignmentExtractor.getRoleAssignmentsForUser());
    }

    boolean verifyCanEditAllEntities(List<RoleAssignment> roleAssignments) {
        return roleAssignments
                .stream()
                .anyMatch(roleAssignment -> ROLE_EDIT_STOPS.equals(roleAssignment.getRole())
                                             && roleAssignment.getEntityClassifications() != null
                                             && roleAssignment.getEntityClassifications().get(AuthorizationConstants.ENTITY_TYPE) != null
                                             && roleAssignment.getEntityClassifications().get(AuthorizationConstants.ENTITY_TYPE).contains(ENTITY_CLASSIFIER_ALL_ATTRIBUTES)
                                             && StringUtils.isEmpty(roleAssignment.getAdministrativeZone())
                );
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

    @Override
    public boolean canDeleteEntity(EntityStructure entity) {
        return canEditDeleteEntity(entity, ROLE_DELETE_STOPS);
    }

    @Override
    public boolean canEditEntity(EntityStructure entity) {
        return canEditDeleteEntity(entity, ROLE_EDIT_STOPS);
    }

    @Override
    public Set<String> getAllowedStopPlaceTypes(Object entity){
       return getStopTypesOrSubmode(STOP_PLACE_TYPE, true, entity);
    }

    @Override
    public Set<String> getBannedStopPlaceTypes(Object entity) {
        if(!dataScopedAuthorizationService.isAuthorized(ROLE_EDIT_STOPS, List.of(entity))) {
            return Set.of(ENTITY_CLASSIFIER_ALL_ATTRIBUTES);
        }
        return getStopTypesOrSubmode(STOP_PLACE_TYPE, false, entity);
    }

    @Override
    public Set<String> getAllowedSubmodes(Object entity) {
        return  getStopTypesOrSubmode(SUBMODE, true, entity);
    }

    @Override
    public Set<String> getBannedSubmodes(Object entity) {
        if(!dataScopedAuthorizationService.isAuthorized(ROLE_EDIT_STOPS, List.of(entity))) {
            return Set.of(ENTITY_CLASSIFIER_ALL_ATTRIBUTES);
        }
        return getStopTypesOrSubmode(SUBMODE, false, entity);
    }

    @Override
    public boolean isGuest() {
        if (hasNoAuthentications()) {
            return true;
        }
        return roleAssignmentExtractor.getRoleAssignmentsForUser().isEmpty();
    }

    private Set<String> getStopTypesOrSubmode(String type, boolean isAllowed, Object entity) {
        if (hasNoAuthentications()) {
            return Set.of();
        }
        return roleAssignmentExtractor.getRoleAssignmentsForUser().stream()
                .filter(roleAssignment -> filterByRole(roleAssignment,entity))
                .filter(roleAssignment -> roleAssignment.getEntityClassifications() != null)
                .filter(roleAssignment -> topographicPlaceChecker.entityMatchesAdministrativeZone(roleAssignment, entity))
                .filter(roleAssignment -> roleAssignment.getEntityClassifications().get(type) != null)
                .map(roleAssignment -> roleAssignment.getEntityClassifications().get(type))
                .flatMap(List::stream)
                .filter(types -> isAllowed != types.startsWith("!"))
                .map(types -> isAllowed ? types : types.substring(1))
                .collect(Collectors.toSet());
    }

    private boolean filterByRole(RoleAssignment roleAssignment,Object entity) {
        return dataScopedAuthorizationService.authorized(roleAssignment, entity, ROLE_EDIT_STOPS)
                      || dataScopedAuthorizationService.authorized(roleAssignment, entity, ROLE_DELETE_STOPS);

    }


    private boolean hasNoAuthentications() {
        if(!authorizationEnabled) {
            return true;
        }
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return !(auth instanceof JwtAuthenticationToken);
    }

    private boolean canEditDeleteEntity(EntityStructure entity, String role) {
        if (hasNoAuthentications()) {
            return false;
        }

        if (entity instanceof GroupOfStopPlaces groupOfStopPlaces) {
            final List<StopPlace> gospMembers = groupOfStopPlacesMembersResolver.resolve(groupOfStopPlaces);
            return gospMembers.stream()
                    .allMatch(stopPlace -> dataScopedAuthorizationService.isAuthorized(role, List.of(stopPlace)));
        } else {
            return dataScopedAuthorizationService.isAuthorized(role, List.of(entity));
        }
    }
}

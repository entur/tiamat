package org.rutebanken.tiamat.auth;

import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.geom.Point;
import org.rutebanken.helper.organisation.AuthorizationConstants;
import org.rutebanken.helper.organisation.DataScopedAuthorizationService;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.helper.organisation.RoleAssignmentExtractor;
import org.rutebanken.tiamat.auth.check.TopographicPlaceChecker;
import org.rutebanken.tiamat.diff.generic.SubmodeEnumuration;
import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
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
    public boolean canEditAllEntities() {
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
    public void verifyCanEditEntities(Collection<? extends EntityStructure> entities) {
        dataScopedAuthorizationService.assertAuthorized(ROLE_EDIT_STOPS, entities);
    }

    @Override
    public void verifyCanDeleteEntities(Collection<? extends EntityStructure> entities) {
        dataScopedAuthorizationService.assertAuthorized(ROLE_DELETE_STOPS, entities);

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
    public boolean canEditEntity(Point point) {
        if(hasNoAuthentications()) {
            return false;
        }
         return roleAssignmentExtractor.getRoleAssignmentsForUser().stream()
                .filter(roleAssignment -> roleAssignment.getRole().equals(ROLE_EDIT_STOPS))
                  .anyMatch(roleAssignment -> topographicPlaceChecker.pointMatchesAdministrativeZone(roleAssignment, point));

    }

    @Override
    public Set<StopTypeEnumeration> getAllowedStopPlaceTypes(EntityStructure entity){
        final Set<String> allowedStopTypes = getStopTypesOrSubmode(STOP_PLACE_TYPE, true, entity);

        return convertToStopTypeEnumeration(allowedStopTypes);

    }

    private Set<StopTypeEnumeration> convertToStopTypeEnumeration(Set<String> stopTypes) {

        if(stopTypes.contains("*")){
            return Set.of();
        }
        return stopTypes.stream()
                .map(StopTypeEnumeration::fromValue)
                .collect(Collectors.toSet());
    }

    private Set<SubmodeEnumuration> convertToSubmodeEnumeration(Set<String> submodes) {

        if(submodes.contains("*")) {
            return Set.of();
        }
        return submodes.stream()
                .map(SubmodeEnumuration::fromValue)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<StopTypeEnumeration> getLocationAllowedStopPlaceTypes(boolean canEdit, Point point) {
        final Set<String> stopTypes = getLocationStopTypesOrSubmode(canEdit, STOP_PLACE_TYPE, true, point);
        return convertToStopTypeEnumeration(stopTypes);
    }

    @Override
    public Set<StopTypeEnumeration> getBannedStopPlaceTypes(EntityStructure entity) {


        if(hasNoAuthentications() || !dataScopedAuthorizationService.isAuthorized(ROLE_EDIT_STOPS, List.of(entity))) {
            return convertToStopTypeEnumeration(Set.of("*"));
        }
        final Set<String> stopType = getStopTypesOrSubmode(STOP_PLACE_TYPE, false, entity);
        return convertToStopTypeEnumeration(stopType);
    }

    @Override
    public Set<StopTypeEnumeration> getLocationBannedStopPlaceTypes(boolean canEdit, Point point) {
        final Set<String> bannedStopTypes = getLocationStopTypesOrSubmode(canEdit, STOP_PLACE_TYPE, false, point);
        return convertToStopTypeEnumeration(bannedStopTypes);
    }

    @Override
    public Set<SubmodeEnumuration> getAllowedSubmodes(EntityStructure entity) {
        final Set<String> submodes = getStopTypesOrSubmode(SUBMODE, true, entity);
        return convertToSubmodeEnumeration(submodes);
    }

    @Override
    public Set<SubmodeEnumuration> getLocationAllowedSubmodes(boolean canEdit, Point point) {
        final Set<String> submodes = getLocationStopTypesOrSubmode(canEdit, SUBMODE, true, point);
        return convertToSubmodeEnumeration(submodes);
    }

    @Override
    public Set<SubmodeEnumuration> getBannedSubmodes(EntityStructure entity) {
        if(hasNoAuthentications() || !dataScopedAuthorizationService.isAuthorized(ROLE_EDIT_STOPS, List.of(entity))) {
            return convertToSubmodeEnumeration(Set.of("*"));
        }
        final Set<String> submode = getStopTypesOrSubmode(SUBMODE, false, entity);
        return convertToSubmodeEnumeration(submode);
    }

    @Override
    public Set<SubmodeEnumuration> getLocationBannedSubmodes(boolean canEdit, Point point) {
        final Set<String> submode = getLocationStopTypesOrSubmode(canEdit, SUBMODE, false, point);
        return convertToSubmodeEnumeration(submode);
    }

    @Override
    public boolean isGuest() {
        if (hasNoAuthentications()) {
            return true;
        }
        return roleAssignmentExtractor.getRoleAssignmentsForUser().isEmpty();
    }

    private Set<String> getStopTypesOrSubmode(String type, boolean isAllowed, EntityStructure entity) {
        if (hasNoAuthentications()) {
            return Set.of();
        }
        return roleAssignmentExtractor.getRoleAssignmentsForUser().stream()
                .filter(roleAssignment -> canEditDeleteEntity(entity,roleAssignment.getRole()))
                .filter(roleAssignment -> roleAssignment.getEntityClassifications() != null)
                .filter(roleAssignment -> topographicPlaceChecker.entityMatchesAdministrativeZone(roleAssignment, entity))
                .filter(roleAssignment -> roleAssignment.getEntityClassifications().get(type) != null)
                .map(roleAssignment -> roleAssignment.getEntityClassifications().get(type))
                .flatMap(List::stream)
                .filter(types -> isAllowed != types.startsWith("!"))
                .map(types -> isAllowed ? types : types.substring(1))
                .collect(Collectors.toSet());
    }

    private Set<String> getLocationStopTypesOrSubmode(boolean canEdit, String type, boolean isAllowed, Point point) {
        if (hasNoAuthentications()) {
                return Set.of();
        }
        if (!canEdit && !isAllowed) {
            return Set.of("*");
        }
        Set<String> stopTypesSubmodes = roleAssignmentExtractor.getRoleAssignmentsForUser().stream()
                .filter(roleAssignment -> roleAssignment.getEntityClassifications() != null)
                .filter(roleAssignment -> topographicPlaceChecker.entityMatchesAdministrativeZone(roleAssignment,point ))
                .filter(roleAssignment -> roleAssignment.getEntityClassifications().get(type) != null)
                .map(roleAssignment -> roleAssignment.getEntityClassifications().get(type))
                .flatMap(List::stream)
                .filter(types -> isAllowed != types.startsWith("!"))
                .map(types -> isAllowed ? types : types.substring(1))
                .collect(Collectors.toSet());
        if (canEdit && stopTypesSubmodes.isEmpty() && isAllowed) {
            stopTypesSubmodes.add(ENTITY_CLASSIFIER_ALL_ATTRIBUTES);
        }
        return stopTypesSubmodes;
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
            if(entity instanceof StopPlace stopPlace) {
                if(!stopPlace.getChildren().isEmpty()) {
                    return stopPlace.getChildren().stream()
                            .allMatch(child -> dataScopedAuthorizationService.isAuthorized(role, List.of(child)));
                } else {
                    return dataScopedAuthorizationService.isAuthorized(role, List.of(stopPlace));
                }

            }
            return dataScopedAuthorizationService.isAuthorized(role, List.of(entity));
        }
    }
}

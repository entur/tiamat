package org.rutebanken.tiamat.ext.fintraffic.auth;

import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.diff.generic.SubmodeEnumuration;
import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.rutebanken.tiamat.ext.fintraffic.auth.TrivoreAuthorizations.ENTITY_TYPE_ALL;
import static org.rutebanken.tiamat.ext.fintraffic.auth.TrivoreAuthorizations.TRANSPORT_MODE_ALL;
import static org.rutebanken.tiamat.ext.fintraffic.auth.TrivorePermission.ADMINISTER;
import static org.rutebanken.tiamat.ext.fintraffic.auth.TrivorePermission.MANAGE;

public class FintrafficAuthorizationService implements AuthorizationService {
    private static final Set<StopTypeEnumeration> ALL_STOP_PLACE_TYPES = EnumSet.allOf(StopTypeEnumeration.class);

    private static final Set<SubmodeEnumuration> ALL_SUBMODES = EnumSet.allOf(SubmodeEnumuration.class);

    private final Logger logger = LoggerFactory.getLogger(FintrafficAuthorizationService.class);

    private final TrivoreAuthorizations trivoreAuthorizations;

    public FintrafficAuthorizationService(TrivoreAuthorizations trivoreAuthorizations) {
        this.trivoreAuthorizations = trivoreAuthorizations;
    }

    @Override
    public boolean canEditAllEntities() {
        return trivoreAuthorizations.hasAccess(ENTITY_TYPE_ALL, TRANSPORT_MODE_ALL, MANAGE);
    }

    @Override
    public boolean canEditEntities(Collection<? extends EntityStructure> entities) {
        return trivoreAuthorizations.hasAccess(ENTITY_TYPE_ALL, TRANSPORT_MODE_ALL, MANAGE)
                || entities.stream().allMatch(this::canEditEntity);
    }

    @Override
    public void verifyCanEditEntities(Collection<? extends EntityStructure> entities) {
        if (!canEditEntities(entities)) {
            throw new AccessDeniedException("current user is not allowed to edit entities");
        }
    }

    @Override
    public void verifyCanDeleteEntities(Collection<? extends EntityStructure> entities) {
        if (!entities.stream().allMatch(this::canDeleteEntity)) {
            throw new AccessDeniedException("current user is not allowed to delete entities");
        }
    }

    @Override
    public boolean canDeleteEntity(EntityStructure entity) {
        String codespace = getCodespace(entity.getNetexId());
        return trivoreAuthorizations.hasAccess(detectEntityType(entity), detectTransportMode(entity), ADMINISTER)
                && trivoreAuthorizations.hasAccessToCodespace(codespace);
    }

    @Override
    public boolean canEditEntity(EntityStructure entity) {
        String codespace = getCodespace(entity.getNetexId());
        return trivoreAuthorizations.hasAccess(detectEntityType(entity), detectTransportMode(entity), MANAGE)
                && trivoreAuthorizations.hasAccessToCodespace(codespace);
    }

    private static String detectEntityType(EntityStructure entity) {
        return entity.getClass().getSimpleName();
    }

    private static String detectTransportMode(EntityStructure entity) {
        return switch (entity) {
            case StopPlace sp -> sp.getTransportMode().value();
            default -> TRANSPORT_MODE_ALL;
        };
    }

    @Override
    public boolean canEditEntity(Point point) {
        logger.trace("FintrafficAuthorizationService.canEditEntity({})", point);
        return true;  // TODO: implement geofencing support
    }

    @Override
    public Set<StopTypeEnumeration> getAllowedStopPlaceTypes(EntityStructure entity) {
        logger.trace("FintrafficAuthorizationService.getAllowedStopPlaceTypes({}, {})", entity.getId(), entity.getNetexId());
        return ALL_STOP_PLACE_TYPES;
    }

    @Override
    public Set<StopTypeEnumeration> getLocationAllowedStopPlaceTypes(boolean canEdit, Point point) {
        logger.trace("FintrafficAuthorizationService.getLocationAllowedStopPlaceTypes({}, {})", canEdit, point);
        return ALL_STOP_PLACE_TYPES;
    }

    @Override
    public Set<StopTypeEnumeration> getBannedStopPlaceTypes(EntityStructure entity) {
        logger.trace("FintrafficAuthorizationService.getBannedStopPlaceTypes({}, {})", entity.getId(), entity.getNetexId());
        return Set.of();
    }

    @Override
    public Set<StopTypeEnumeration> getLocationBannedStopPlaceTypes(boolean canEdit, Point point) {
        logger.trace("FintrafficAuthorizationService.getLocationBannedStopPlaceTypes({}, {})", canEdit, point);
        return Set.of();
    }

    @Override
    public Set<SubmodeEnumuration> getAllowedSubmodes(EntityStructure entity) {
        logger.trace("FintrafficAuthorizationService.getAllowedSubmodes({}, {})", entity.getId(), entity.getNetexId());
        return ALL_SUBMODES;
    }

    @Override
    public Set<SubmodeEnumuration> getLocationAllowedSubmodes(boolean canEdit, Point point) {
        logger.trace("FintrafficAuthorizationService.getLocationAllowedSubmodes({}, {})", canEdit, point);
        return ALL_SUBMODES;
    }

    @Override
    public Set<SubmodeEnumuration> getBannedSubmodes(EntityStructure entity) {
        logger.trace("FintrafficAuthorizationService.getBannedSubmodes({}, {})", entity.getId(), entity.getNetexId());
        return Set.of();
    }

    @Override
    public Set<SubmodeEnumuration> getLocationBannedSubmodes(boolean canEdit, Point point) {
        logger.trace("FintrafficAuthorizationService.getLocationBannedSubmodes({}, {})", canEdit, point);
        return Set.of();
    }

    @Override
    public boolean isGuest() {
        return !trivoreAuthorizations.isAuthenticated();
    }

    private final Pattern netexIdPattern = Pattern.compile("([A-Z]{3}):([^:]*):([^:]*)");
    private String getCodespace(String netexId) {
        Matcher matcher = netexIdPattern.matcher(netexId);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            throw new InvalidNetexIdException(netexId + " is not a valid NeTEx id");
        }
    }
}

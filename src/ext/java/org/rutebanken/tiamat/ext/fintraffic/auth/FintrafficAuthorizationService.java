package org.rutebanken.tiamat.ext.fintraffic.auth;

import org.locationtech.jts.geom.Point;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.model.AirSubmodeEnumeration;
import org.rutebanken.tiamat.model.BusSubmodeEnumeration;
import org.rutebanken.tiamat.model.CoachSubmodeEnumeration;
import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.model.FunicularSubmodeEnumeration;
import org.rutebanken.tiamat.model.MetroSubmodeEnumeration;
import org.rutebanken.tiamat.model.RailSubmodeEnumeration;
import org.rutebanken.tiamat.model.SelfDriveSubmodeEnumeration;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.TaxiSubmodeEnumeration;
import org.rutebanken.tiamat.model.TelecabinSubmodeEnumeration;
import org.rutebanken.tiamat.model.TramSubmodeEnumeration;
import org.rutebanken.tiamat.model.VehicleModeEnumeration;
import org.rutebanken.tiamat.model.WaterSubmodeEnumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FintrafficAuthorizationService implements AuthorizationService {
    private static final Set<String> ALL_STOP_PLACE_TYPES = Arrays.stream(StopTypeEnumeration.values())
            .map(StopTypeEnumeration::value)
            .collect(Collectors.toSet());

    //private static final Set<String> ALL_STOP_PLACE_TYPES = Set.of("airSubmode", "busSubmode", "coachSubmode", "funicularSubmode", "metroSubmode", "tramSubmode", "telecabinSubmode", "railSubmode", "waterSubmode");
    //private static final Set<String> ALL_SUBMODES = Set.of("airSubmode", "busSubmode", "coachSubmode", "funicularSubmode", "metroSubmode", "tramSubmode", "telecabinSubmode", "railSubmode", "waterSubmode");

    private static final Set<String> ALL_SUBMODES = Stream.of(
                    Arrays.stream(AirSubmodeEnumeration.values()).map(AirSubmodeEnumeration::value),
                    Arrays.stream(BusSubmodeEnumeration.values()).map(BusSubmodeEnumeration::value),
                    Arrays.stream(CoachSubmodeEnumeration.values()).map(CoachSubmodeEnumeration::value),
                    Arrays.stream(FunicularSubmodeEnumeration.values()).map(FunicularSubmodeEnumeration::value),
                    Arrays.stream(MetroSubmodeEnumeration.values()).map(MetroSubmodeEnumeration::value),
                    Arrays.stream(RailSubmodeEnumeration.values()).map(RailSubmodeEnumeration::value),
                    Arrays.stream(SelfDriveSubmodeEnumeration.values()).map(SelfDriveSubmodeEnumeration::value),
                    Arrays.stream(TaxiSubmodeEnumeration.values()).map(TaxiSubmodeEnumeration::value),
                    Arrays.stream(TelecabinSubmodeEnumeration.values()).map(TelecabinSubmodeEnumeration::value),
                    Arrays.stream(TramSubmodeEnumeration.values()).map(TramSubmodeEnumeration::value),
                    Arrays.stream(VehicleModeEnumeration.values()).map(VehicleModeEnumeration::value),
                    Arrays.stream(WaterSubmodeEnumeration.values()).map(WaterSubmodeEnumeration::value))
            .flatMap(Function.identity())
            .collect(Collectors.toSet());

    private final Logger logger = LoggerFactory.getLogger(FintrafficAuthorizationService.class);

    private final TrivoreAuthorizations trivoreAuthorizations;

    public FintrafficAuthorizationService(TrivoreAuthorizations trivoreAuthorizations) {
        this.trivoreAuthorizations = trivoreAuthorizations;
    }

    @Override
    public boolean verifyCanEditAllEntities() {
        boolean allowed = trivoreAuthorizations.canEditAllEntities();
        logger.debug("verifyCanEditAllEntities() = {}", allowed);
        return allowed;
    }

    @Override
    public boolean canEditEntities(Collection<? extends EntityStructure> entities) {
        boolean allowed = trivoreAuthorizations.canManageAllEntities()
                || trivoreAuthorizations.canEditAllEntities()
                || entities.stream().allMatch(this::canEditEntity);
        logger.debug("canEditEntities(...) = {}", allowed);
        return allowed;
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
        boolean allowed = trivoreAuthorizations.canManageAllEntities()
                || trivoreAuthorizations.canDeleteAllEntities()
                || trivoreAuthorizations.canManageCodespaceEntities(codespace)
                || trivoreAuthorizations.canDeleteCodespaceEntities(codespace);
        logger.debug("canDeleteEntity({}, {}) = {}", entity.getId(), entity.getNetexId(), allowed);
        return allowed;
    }

    @Override
    public boolean canEditEntity(EntityStructure entity) {
        String codespace = getCodespace(entity.getNetexId());
        boolean allowed = trivoreAuthorizations.canManageAllEntities()
                || trivoreAuthorizations.canEditAllEntities()
                || trivoreAuthorizations.canManageCodespaceEntities(codespace)
                || trivoreAuthorizations.canEditEntities(codespace);
        logger.debug("canEditEntity({}, {}) = {}", entity.getId(), entity.getNetexId(), allowed);
        return allowed;
    }

    @Override
    public boolean canEditEntity(Point point) {
        logger.trace("FintrafficAuthorizationService.canEditEntity({})", point);
        return true;  // TODO: implement geography support
    }

    @Override
    public <T extends EntityStructure> Set<String> getRelevantRolesForEntity(T entity) {
        // TODO: will be removed
        return Set.of();
    }

    @Override
    public <T extends EntityStructure> boolean canEditEntity(RoleAssignment roleAssignment, T entity) {
        // TODO: will be removed
        return canEditEntity(entity);
    }

    @Override
    public Set<String> getAllowedStopPlaceTypes(EntityStructure entity) {
        logger.trace("FintrafficAuthorizationService.getAllowedStopPlaceTypes({}, {})", entity.getId(), entity.getNetexId());
        return ALL_STOP_PLACE_TYPES;
    }

    @Override
    public Set<String> getLocationAllowedStopPlaceTypes(boolean canEdit, Point point) {
        logger.trace("FintrafficAuthorizationService.getLocationAllowedStopPlaceTypes({}, {})", canEdit, point);
        return ALL_STOP_PLACE_TYPES;
    }

    @Override
    public Set<String> getBannedStopPlaceTypes(EntityStructure entity) {
        logger.trace("FintrafficAuthorizationService.getBannedStopPlaceTypes({}, {})", entity.getId(), entity.getNetexId());
        return Set.of();
    }

    @Override
    public Set<String> getLocationBannedStopPlaceTypes(boolean canEdit, Point point) {
        logger.trace("FintrafficAuthorizationService.getLocationBannedStopPlaceTypes({}, {})", canEdit, point);
        return Set.of();
    }

    @Override
    public Set<String> getAllowedSubmodes(EntityStructure entity) {
        logger.trace("FintrafficAuthorizationService.getAllowedSubmodes({}, {})", entity.getId(), entity.getNetexId());
        return ALL_SUBMODES;
    }

    @Override
    public Set<String> getLocationAllowedSubmodes(boolean canEdit, Point point) {
        logger.trace("FintrafficAuthorizationService.getLocationAllowedSubmodes({}, {})", canEdit, point);
        return ALL_SUBMODES;
    }

    @Override
    public Set<String> getBannedSubmodes(EntityStructure entity) {
        logger.trace("FintrafficAuthorizationService.getBannedSubmodes({}, {})", entity.getId(), entity.getNetexId());
        return Set.of();
    }

    @Override
    public Set<String> getLocationBannedSubmodes(boolean canEdit, Point point) {
        logger.trace("FintrafficAuthorizationService.getLocationBannedSubmodes({}, {})", canEdit, point);
        return Set.of();
    }

    @Override
    public boolean isGuest() {
        logger.trace("FintrafficAuthorizationService.isGuest()");
        return !trivoreAuthorizations.isAuthenticated();
    }

    private final Pattern netexIdPattern = Pattern.compile("[A-Z]{3}:([^:]*):([^:]*)");
    private String getCodespace(String netexId) {
        Matcher matcher = netexIdPattern.matcher(netexId);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            throw new InvalidNetexIdException(netexId + " is not a valid NeTEx id");
        }
    }
}

package org.rutebanken.tiamat.ext.fintraffic.auth;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.diff.generic.SubmodeEnumuration;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.TopographicPlaceSearch;
import org.rutebanken.tiamat.model.EntityStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.model.Zone_VersionStructure;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.rutebanken.tiamat.ext.fintraffic.auth.TrivoreAuthorizations.ENTITY_TYPE_ALL;
import static org.rutebanken.tiamat.ext.fintraffic.auth.TrivoreAuthorizations.TRANSPORT_MODE_ALL;
import static org.rutebanken.tiamat.ext.fintraffic.auth.TrivorePermission.ADMINISTER;
import static org.rutebanken.tiamat.ext.fintraffic.auth.TrivorePermission.MANAGE;

public class FintrafficAuthorizationService implements AuthorizationService {
    private static final Set<StopTypeEnumeration> ALL_STOP_PLACE_TYPES = EnumSet.allOf(StopTypeEnumeration.class);

    private final Logger logger = LoggerFactory.getLogger(FintrafficAuthorizationService.class);

    private final TrivoreAuthorizations trivoreAuthorizations;

    private final TopographicPlaceRepository topographicPlaceRepository;

    private final LoadingCache<String, List<TopographicPlace>> fintrafficAdministrativeZoneCache;

    public FintrafficAuthorizationService(TrivoreAuthorizations trivoreAuthorizations,
                                          TopographicPlaceRepository topographicPlaceRepository) {
        this.trivoreAuthorizations = trivoreAuthorizations;
        this.topographicPlaceRepository = topographicPlaceRepository;
        this.fintrafficAdministrativeZoneCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(Duration.ofMinutes(10))
                .build(new CacheLoader<>() {
                    @Override
                    @Nonnull
                    public List<TopographicPlace> load(@Nonnull String codespace) {
                        return loadTopographicPlaces(codespace);
                    }
                });
    }

    @Override
    public boolean canEditAllEntities() {
        return trivoreAuthorizations.hasAccess(ENTITY_TYPE_ALL, TRANSPORT_MODE_ALL, MANAGE);
    }

    @Override
    public boolean canEditEntities(Collection<? extends EntityStructure> entities) {
        return trivoreAuthorizations.hasAccess(ENTITY_TYPE_ALL, TRANSPORT_MODE_ALL, MANAGE, true)
                || entities.stream().allMatch(e -> canEditEntity(e, true));
    }

    @Override
    public void verifyCanEditEntities(Collection<? extends EntityStructure> entities) {
        if (!canEditEntities(entities)) {
            throw new AccessDeniedException("current user is not allowed to edit entities");
        }
    }

    @Override
    public void verifyCanDeleteEntities(Collection<? extends EntityStructure> entities) {
        if (!entities.stream().allMatch(e -> this.canDeleteEntity(e, true))) {
            throw new AccessDeniedException("current user is not allowed to delete entities");
        }
    }

    @Override
    public boolean canDeleteEntity(EntityStructure entity) {
        return this.canDeleteEntity(entity, false);
    }

    private boolean canDeleteEntity(EntityStructure entity, boolean logEvent) {
        return trivoreAuthorizations.hasAccess(detectEntityType(entity), detectTransportMode(entity), ADMINISTER, logEvent);
    }

    @Override
    public boolean canEditEntity(EntityStructure entity) {
       return canEditEntity(entity, false);
    }

    private boolean canEditEntity(EntityStructure entity, boolean logEvent) {
        if (entity == null) {
            return true;
        }
        if (!trivoreAuthorizations.hasAccess(detectEntityType(entity), detectTransportMode(entity), MANAGE, logEvent)) {
            return false;
        }

        if (entity instanceof StopPlace stop) {
            // Ensure that user has sufficient permission to edit all nested entities
            if (!stop.getChildren().stream().allMatch(e -> canEditEntity(e, logEvent))) {
                return false;
            }
            if (!stop.getQuays().stream().allMatch(e -> canEditEntity(e, logEvent))) {
                return false;
            }
        }

        if (entity instanceof Zone_VersionStructure zone) {
            return canEditEntity(zone.getCentroid(), logEvent);
        }
        return true;
    }

    private static String detectEntityType(EntityStructure entity) {
        return entity.getClass().getSimpleName();
    }

    private static String detectTransportMode(EntityStructure entity) {
        return switch (entity) {
            case StopPlace sp -> Optional.ofNullable(sp.getTransportMode()).map(Enum::name).orElse(TRANSPORT_MODE_ALL);
            default -> TRANSPORT_MODE_ALL;
        };
    }

    @Override
    public boolean canEditEntity(Point point) {
        return canEditEntity(point, false);
    }

    private boolean canEditEntity(Point point, boolean logEvent) {
        logger.trace("FintrafficAuthorizationService.canEditEntity({})", point);
        Set<String> accessibleCodespaces = trivoreAuthorizations.getAccessibleCodespaces();

        if (accessibleCodespaces.isEmpty()) {
            if (logEvent) {
                logger.info("User [{}] has no accessible codespaces, cannot edit entity at point {}.", TrivoreAuthorizations.getCurrentSubject(), point);
            }
            logger.trace("FintrafficAuthorizationService.canEditEntity({}) codespaces is empty", point);
            return false;
        }
        boolean result = accessibleCodespaces.stream().anyMatch(codespace ->
                {
                    try {
                        List<TopographicPlace> topographicPlacesForCodespace = fintrafficAdministrativeZoneCache.get(codespace);
                        return topographicPlacesForCodespace.stream().anyMatch(tp -> tp.getPolygon() != null && tp.getPolygon().contains(point));
                    } catch (ExecutionException e) {
                        logger.warn("Failed to fetch topographic places for codespaces [{}]", accessibleCodespaces, e);
                        return false;
                    }
                }
        );
        if (logEvent) {
            String isAllowed = result ? "is allowed": "is not allowed";
            logger.info("User [{}] with codespaces {} {} to edit entity at point {}.", TrivoreAuthorizations.getCurrentSubject(), accessibleCodespaces, isAllowed, point);
        }
        return result;
    }

    private List<TopographicPlace> loadTopographicPlaces(String codespace) {
        logger.trace("FintrafficAuthorizationService.loadTopographicPlaces({})", codespace);
        List<TopographicPlace> topographicPlaces = topographicPlaceRepository.findTopographicPlace(
                TopographicPlaceSearch.newTopographicPlaceSearchBuilder().versionValidity(ExportParams.VersionValidity.CURRENT).build()
        );

        return topographicPlaces.stream()
                .filter(tp -> tp.getTopographicPlaceType().equals(TopographicPlaceTypeEnumeration.REGION))
                .filter(tp -> tp.getKeyValues().get("codespace").getItems().contains(codespace))
                .peek(Zone_VersionStructure::getPolygon) // Prefetch polygons
                .collect(Collectors.toList());
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
        return Set.of();
    }

    @Override
    public Set<SubmodeEnumuration> getLocationAllowedSubmodes(boolean canEdit, Point point) {
        logger.trace("FintrafficAuthorizationService.getLocationAllowedSubmodes({}, {})", canEdit, point);
        return Set.of();
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
}

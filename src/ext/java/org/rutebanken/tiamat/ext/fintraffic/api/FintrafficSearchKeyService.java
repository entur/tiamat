package org.rutebanken.tiamat.ext.fintraffic.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygonal;
import org.locationtech.jts.geom.prep.PreparedPolygon;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.TopographicPlaceSearch;
import org.rutebanken.tiamat.ext.fintraffic.api.model.FintrafficReadApiSearchKey;
import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiSearchKey;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class FintrafficSearchKeyService implements SearchKeyService {
    private final Logger logger = LoggerFactory.getLogger(FintrafficSearchKeyService.class);
    private final ObjectMapper objectMapper;
    private final TopographicPlaceRepository topographicPlaceRepository;
    private final StopPlaceRepository stopPlaceRepository;
    private static final String CODESPACE_KEY = "codespace";
    private final LoadingCache<String, List<PolygonAndAreaCodes>> administrativeZoneGeometryIndex;

    private record PolygonAndAreaCodes(PreparedPolygon polygon, Set<String> areaCodes) {
    }

    public FintrafficSearchKeyService(
            ObjectMapper objectMapper,
            TopographicPlaceRepository topographicPlaceRepository,
            StopPlaceRepository stopPlaceRepository
    ) {
        this.objectMapper = objectMapper;
        this.topographicPlaceRepository = topographicPlaceRepository;
        this.stopPlaceRepository = stopPlaceRepository;

        // Use a loading cache to store administrative zone geometries for efficient spatial queries
        this.administrativeZoneGeometryIndex = CacheBuilder.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(Duration.ofMinutes(5))
                .build(new CacheLoader<>() {
                    @Override
                    @Nonnull
                    public List<PolygonAndAreaCodes> load(@Nonnull String ignoredKey) {
                        return loadPolygonsAndAreaCodes();
                    }
                });
    }

    public String generateSearchKeyJSON(EntityInVersionStructure entity) {
        FintrafficReadApiSearchKey searchKey = this.extractSearchKey(entity);
        return createJSONString(searchKey);
    }

    private FintrafficReadApiSearchKey extractSearchKey(EntityInVersionStructure entity) {
        Optional<StopPlace> parentStopPlace = fetchParentStopPlace(entity);
        Optional<FintrafficReadApiSearchKey> searchKeyFromParent = parentStopPlace.map(this::extractSearchKey);
        if (entity instanceof org.rutebanken.tiamat.model.StopPlace stopPlace) {
            FintrafficReadApiSearchKey stopPlaceSearchKey = extractSearchKey(stopPlace);
            if (searchKeyFromParent.isPresent()) {
                // Merge if parent is present as well
                FintrafficReadApiSearchKey parentSearchKey = searchKeyFromParent.get();
                String[] mergedTransportModes = Stream.concat(
                        Stream.of(stopPlaceSearchKey.transportModes()),
                        Stream.of(parentSearchKey.transportModes())
                ).distinct().toArray(String[]::new);

                String[] mergedAreaCodes = Stream.concat(
                        Stream.of(stopPlaceSearchKey.areaCodes()),
                        Stream.of(parentSearchKey.areaCodes())
                ).distinct().toArray(String[]::new);

                return new FintrafficReadApiSearchKey(mergedTransportModes, mergedAreaCodes);
            } else {
                return stopPlaceSearchKey;
            }
        } else if (entity instanceof Parking) {
            // For Parking, use parent StopPlace search key if available
            return searchKeyFromParent.orElse(null);
        } else {
            return null;
        }
    }

    private Optional<StopPlace> fetchParentStopPlace(EntityInVersionStructure entity) {
        Optional<SiteRefStructure> parentRef = getParentRefForEntity(entity);
        if (parentRef.isPresent()) {
            SiteRefStructure parentRefValue = parentRef.get();
            if (parentRefValue.getRef() == null) {
                // No valid parent reference
                return Optional.empty();
            }

            if (parentRefValue.getVersion() == null) {
                // No version specified, fetch latest
                return Optional.ofNullable(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(parentRefValue.getRef()));
            }

            try {
                long version = Long.parseLong(parentRefValue.getVersion());
                return Optional.ofNullable(stopPlaceRepository.findFirstByNetexIdAndVersion(parentRefValue.getRef(), version));
            } catch (NumberFormatException e) {
                logger.error("Failed to parse version number", e);
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private Optional<SiteRefStructure> getParentRefForEntity(EntityInVersionStructure entity) {
        return switch (entity) {
            case StopPlace sp -> Optional.ofNullable(sp.getParentSiteRef());
            case Parking p -> Optional.ofNullable(p.getParentSiteRef());
            default -> Optional.empty();
        };
    }

    private FintrafficReadApiSearchKey extractSearchKey(org.rutebanken.tiamat.model.StopPlace stopPlace) {
        String[] transportModes = stopPlace.getTransportMode() != null
                ? new String[]{stopPlace.getTransportMode().value()}
                : new String[]{};
        Optional<Point> stopPlaceCentroid = Optional.ofNullable(stopPlace.getCentroid());
        Optional<String[]> areaCodes = stopPlaceCentroid.map(this::getAdministrativeZonesForPoint);
        return new FintrafficReadApiSearchKey(transportModes, areaCodes.orElse(new String[]{}));
    }

    private String createJSONString(ReadApiSearchKey searchKey) {
        try {
            return searchKey != null ? objectMapper.writeValueAsString(searchKey) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert search key to JSON", e);
        }
    }

    private String[] getAdministrativeZonesForPoint(Point point) {
        try {
            List<PolygonAndAreaCodes> polygons = administrativeZoneGeometryIndex.get("ALL_ZONES");
            return new ArrayList<>(polygons).stream()
                    .filter(p -> p.polygon().contains(point))
                    .map(p -> p.areaCodes)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .toArray(String[]::new);
        } catch (Exception e) {
            logger.error("Error retrieving administrative zones for point: {}", point, e);
            throw new RuntimeException("Error retrieving administrative zones for point: " + point, e);
        }
    }

    private List<PolygonAndAreaCodes> loadPolygonsAndAreaCodes() {
        TopographicPlaceSearch search = TopographicPlaceSearch.newTopographicPlaceSearchBuilder()
                .versionValidity(ExportParams.VersionValidity.CURRENT).build();

        List<TopographicPlace> topographicPlaces = topographicPlaceRepository.findTopographicPlace(search);
        return topographicPlaces.stream()
                .filter(tp -> tp.getTopographicPlaceType().equals(TopographicPlaceTypeEnumeration.REGION))
                .filter(tp -> tp.getKeyValues().containsKey(CODESPACE_KEY))
                .filter(tp -> tp.getPolygon() != null)
                .map(tp -> new PolygonAndAreaCodes(
                        // Create prepared polygon for efficient spatial queries
                        new PreparedPolygon((Polygonal) tp.getPolygon().copy()),
                        // Create a copy to ensure immutability
                        Set.copyOf(tp.getKeyValues().get(CODESPACE_KEY).getItems())
                ))
                .toList();
    }
}

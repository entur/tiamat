package org.rutebanken.tiamat.importer.finder;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;


import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;

/**
 * Helper class to find stop places based on saved original ID key.
 * It uses a guava cache to avoid expensive calls to the database.
 */
@Component
public class StopPlaceFromOriginalIdFinder implements StopPlaceFinder {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceFromOriginalIdFinder.class);

    private StopPlaceRepository stopPlaceRepository;

    private Cache<String, Optional<Long>> keyValueCache;

    public StopPlaceFromOriginalIdFinder(StopPlaceRepository stopPlaceRepository,
                                         @Value("${stopPlaceFromOriginalIdFinderCache.maxSize:50000}") int maximumSize,
                                         @Value("${stopPlaceFromOriginalIdFinderCache.expiresAfter:30}") int expiresAfter,
                                         @Value("${stopPlaceFromOriginalIdFinderCache.expiresAfterTimeUnit:DAYS}") TimeUnit expiresAfterTimeUnit) {
        this.stopPlaceRepository = stopPlaceRepository;
        keyValueCache = CacheBuilder.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(expiresAfter, expiresAfterTimeUnit)
                .build();
    }

    @Override
    public StopPlace find(StopPlace stopPlace) {

        Set<String> originalIds = stopPlace.getOrCreateValues(ORIGINAL_ID_KEY);

        if(originalIds.isEmpty()) return null;

        StopPlace existingStopPlace = findByKeyValue(originalIds);

        if (existingStopPlace != null) {
            logger.debug("Found stop place {} from original ID", existingStopPlace.getId());
            return existingStopPlace;
        }
        return null;
    }

    public void update(StopPlace stopPlace) {
        if(stopPlace.getId() == null) {
            logger.warn("Attempt to update cache when stop place does not have any ID! stop place: {}", stopPlace);
            return;
        }
        for(String originalId : stopPlace.getOrCreateValues(ORIGINAL_ID_KEY)) {
            keyValueCache.put(keyValKey(ORIGINAL_ID_KEY, originalId), Optional.ofNullable(stopPlace.getId()));
        }
    }

    private StopPlace findByKeyValue(Set<String> originalIds) {
        for(String originalId : originalIds) {
            String cacheKey = keyValKey(ORIGINAL_ID_KEY, originalId);
            Optional<Long> matchingStopPlaceId = keyValueCache.getIfPresent(cacheKey);
            if(matchingStopPlaceId != null && matchingStopPlaceId.isPresent()) {
                logger.debug("Cache match. Key {}, stop place id: {}", cacheKey, matchingStopPlaceId.get());
                return stopPlaceRepository.findOne(matchingStopPlaceId.get());
            }
        }

        // No cache match
        Long stopPlaceId = stopPlaceRepository.findByKeyValue(ORIGINAL_ID_KEY, originalIds);
        if(stopPlaceId != null) {
            return stopPlaceRepository.findOne(stopPlaceId);
        }
        return null;
    }

    private String keyValKey(String key, String value) {
        return key + "-" + value;
    }
}

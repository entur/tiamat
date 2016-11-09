package org.rutebanken.tiamat.importers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


import static org.rutebanken.tiamat.netexmapping.NetexIdMapper.ORIGINAL_ID_KEY;

/**
 * Helper class to find stop places based on saved original ID key.
 * It uses a guava cache to avoid expensive calls to the database.
 */
@Component
public class StopPlaceFromOriginalIdFinder {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceFromOriginalIdFinder.class);

    private StopPlaceRepository stopPlaceRepository;

    private Cache<String, Optional<Long>> keyValueCache;

    public StopPlaceFromOriginalIdFinder(StopPlaceRepository stopPlaceRepository,
                                         @Value("${stopPlaceFromOriginalIdFinderCache.maxSize:20000}") int maximumSize,
                                         @Value("${stopPlaceFromOriginalIdFinderCache.expiresAfter:30}") int expiresAfter,
                                         @Value("${stopPlaceFromOriginalIdFinderCache.expiresAfterTimeUnit:MINUTES}") TimeUnit expiresAfterTimeUnit) {
        this.stopPlaceRepository = stopPlaceRepository;
        keyValueCache = CacheBuilder.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(expiresAfter, expiresAfterTimeUnit)
                .build();
    }

    public StopPlace find(StopPlace stopPlace) {

        StopPlace existingStopPlace = findByKeyValue(ORIGINAL_ID_KEY, stopPlace.getId());

        if (existingStopPlace != null) {
            logger.debug("Found stop place {} from original ID key {}", existingStopPlace.getId(), stopPlace.getId());
            return existingStopPlace;
        }
        return null;
    }

    public void update(Long originalId, Long newId) {
        keyValueCache.put(keyValKey(ORIGINAL_ID_KEY, originalId), Optional.ofNullable(newId));
    }

    private StopPlace findByKeyValue(String key, Long id) {
        if(id == null) {
            return null;
        }
        String cacheKey = keyValKey(key, id);
        try {
            String stringId = String.valueOf(id);
            Optional<Long> stopPlaceId = keyValueCache.get(cacheKey, () ->
                    Optional.ofNullable(stopPlaceRepository.findByKeyValue(key, Arrays.asList(stringId))));
            if(stopPlaceId.isPresent()) {
                return stopPlaceRepository.findOne(stopPlaceId.get());
            }
            return null;
        }
        catch (ExecutionException e) {
            logger.warn("Caught exception while finding stop place by key and value.", e);
            throw new RuntimeException(e);
        }
    }

    private String keyValKey(String key, Long value) {
        return key + "-" + value;
    }
}

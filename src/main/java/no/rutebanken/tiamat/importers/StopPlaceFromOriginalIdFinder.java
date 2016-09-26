package no.rutebanken.tiamat.importers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import no.rutebanken.tiamat.model.StopPlace;
import no.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static no.rutebanken.tiamat.importers.DefaultStopPlaceImporter.ORIGINAL_ID_KEY;

/**
 * Helper class to find stop places based on saved original ID key.
 * It uses a guava cache to avoid expensive calls to the database.
 */
@Component
public class StopPlaceFromOriginalIdFinder {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceFromOriginalIdFinder.class);

        private StopPlaceRepository stopPlaceRepository;

    private Cache<String, Optional<String>> keyValueCache;


    public StopPlaceFromOriginalIdFinder(StopPlaceRepository stopPlaceRepository) {
        this.stopPlaceRepository = stopPlaceRepository;
         keyValueCache = CacheBuilder.newBuilder()
                .maximumSize(50000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
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

    public void update(String originalId, String newId) {
        keyValueCache.put(keyValKey(ORIGINAL_ID_KEY, originalId), Optional.ofNullable(newId));
    }

    private StopPlace findByKeyValue(String key, String value) {
        String cacheKey = keyValKey(key, value);
        try {
            Optional<String> stopPlaceId = keyValueCache.get(cacheKey, () -> Optional.ofNullable(stopPlaceRepository.findByKeyValue(key, value)));
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

    private String keyValKey(String key, String value) {
        return key + "-" + value;
    }
}

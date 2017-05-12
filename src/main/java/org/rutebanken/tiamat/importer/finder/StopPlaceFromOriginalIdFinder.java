package org.rutebanken.tiamat.importer.finder;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;

/**
 * Helper class to find stop places based on saved original ID key.
 * It uses a guava cache to avoid expensive calls to the database.
 */
@Component
public class StopPlaceFromOriginalIdFinder implements StopPlaceFinder {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceFromOriginalIdFinder.class);

    private StopPlaceRepository stopPlaceRepository;

    private Cache<String, Optional<String>> keyValueCache;

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
            logger.debug("Found stop place {} from original ID", existingStopPlace.getNetexId());
            return existingStopPlace;
        }
        return null;
    }

    public void update(StopPlace stopPlace) {
        if(stopPlace.getNetexId() == null) {
            logger.warn("Attempt to update cache when stop place does not have any ID! stop place: {}", stopPlace);
            return;
        }
        for(String originalId : stopPlace.getOrCreateValues(ORIGINAL_ID_KEY)) {
            keyValueCache.put(keyValKey(ORIGINAL_ID_KEY, originalId), Optional.ofNullable(stopPlace.getNetexId()));
        }
    }

    /**
     * If the postfix is numeric search for
     * A: colon prefixed numeric value
     * B: colon prefixed string value
     *
     * ex: originalId: RUT:StopPlace:0123
     * A: :123
     * B: :0123
     *
     * If the postfix cannot be extracted, return the original ID as is
     *
     * If the postfix is not numeric, search for colon prefixed postfix
     *
     */
    private Stream<String> zeroStrippedPostfixAndUnchanged(String originalId) {

        String stringPostfix = NetexIdHelper.extractIdPostfix(originalId);

        if(stringPostfix.equals(originalId)) {
            // Postfix cannot be extracted.
            return Stream.of(originalId);
        }

        try {
            return Stream.of(colonPrefixed(String.valueOf(NetexIdHelper.extractIdPostfixNumeric(originalId))),
                    colonPrefixed(stringPostfix));
        } catch (NumberFormatException e) {
            return Stream.of(colonPrefixed(stringPostfix));
        }
    }

    private String colonPrefixed(String postfix) {
        return ":" + postfix;
    }

    private StopPlace findByKeyValue(Set<String> originalIds) {

        Set<String> zeroPaddedOrUnchangedOriginalIds = originalIds.stream()
                .flatMap(this::zeroStrippedPostfixAndUnchanged)
                .collect(Collectors.toSet());

        for(String zeroPaddedOrUnchangedOriginalId : zeroPaddedOrUnchangedOriginalIds) {
            String cacheKey = keyValKey(ORIGINAL_ID_KEY, zeroPaddedOrUnchangedOriginalId);
            Optional<String> matchingStopPlaceNetexId = keyValueCache.getIfPresent(cacheKey);
            if(matchingStopPlaceNetexId != null && matchingStopPlaceNetexId.isPresent()) {
                logger.debug("Cache match. Key {}, stop place id: {}", cacheKey, matchingStopPlaceNetexId.get());
                return stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(matchingStopPlaceNetexId.get());
            }
        }

        logger.debug("Looking for stop places from original IDs: {}", zeroPaddedOrUnchangedOriginalIds);

        // No cache match
        String stopPlaceNetexId = stopPlaceRepository.findByKeyValue(ORIGINAL_ID_KEY, zeroPaddedOrUnchangedOriginalIds);
        if(stopPlaceNetexId != null) {
            return stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceNetexId);
        }
        return null;
    }

    private String keyValKey(String key, String value) {
        return key + "-" + value;
    }
}

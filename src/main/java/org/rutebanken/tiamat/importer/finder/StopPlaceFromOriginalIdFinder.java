package org.rutebanken.tiamat.importer.finder;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
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
public class StopPlaceFromOriginalIdFinder {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceFromOriginalIdFinder.class);

    private StopPlaceRepository stopPlaceRepository;

    private Cache<String, Set<String>> keyValueCache;

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

    public List<StopPlace> find(StopPlace stopPlace) {

        Set<String> originalIds = stopPlace.getOrCreateValues(ORIGINAL_ID_KEY);

        if(originalIds.isEmpty()) return Lists.newArrayList();

        List<StopPlace> existingStopPlaces = findByKeyValue(originalIds);

        return existingStopPlaces;
    }

    public void update(StopPlace stopPlace) {
        if(stopPlace.getNetexId() == null) {
            logger.warn("Attempt to update cache when stop place does not have any ID! stop place: {}", stopPlace);
            return;
        }
        for(String originalId : stopPlace.getOrCreateValues(ORIGINAL_ID_KEY)) {
//            keyValueCache.put(keyValKey(ORIGINAL_ID_KEY, originalId), Optional.ofNullable(stopPlace.getNetexId()));
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
            logger.debug("Postfix cannot be extracted, leaving value as is: {}", originalId);
            return Stream.of(originalId);
        }

        try {
            Long numericPostFix = NetexIdHelper.extractIdPostfixNumeric(originalId);

            // Both these should be added:
            // Rut:StopPlace:1232123213
            // Rut:StopPlace:01232123213

            String stringNumericPostfix = String.valueOf(numericPostFix);
            String leadingZeroNumericPostfix = "0"+stringNumericPostfix;

            return Sets.newHashSet(colonPrefixed(stringPostfix), colonPrefixed(leadingZeroNumericPostfix), colonPrefixed(stringNumericPostfix)).stream();
        } catch (NumberFormatException e) {
            return Stream.of(colonPrefixed(stringPostfix));
        }
    }

    private String colonPrefixed(String postfix) {
        return ":" + postfix;
    }

    private List<StopPlace> findByKeyValue(Set<String> originalIds) {

        Set<String> zeroPaddedOrUnchangedOriginalIds = originalIds.stream()
                .flatMap(this::zeroStrippedPostfixAndUnchanged)
                .collect(Collectors.toSet());
//
//                .map(zeroPaddedOrUnchangedOriginalId -> keyValKey(ORIGINAL_ID_KEY, zeroPaddedOrUnchangedOriginalId))
//                .map(cacheKey -> keyValueCache.getIfPresent(cacheKey))
//                .map(cacheResult -> {
//                    if(cacheResult == null) {
//
//                    }
//                })
//                .filter(Objects::nonNull)
//                .filter(set -> !set.isEmpty())
//
//                .collect(Collectors.toSet());
//
//
//
//        for (String zeroPaddedOrUnchangedOriginalId : zeroPaddedOrUnchangedOriginalIds) {
//            String cacheKey = keyValKey(ORIGINAL_ID_KEY, zeroPaddedOrUnchangedOriginalId);
//            Set<String> matchingStopPlaceNetexIds = keyValueCache.getIfPresent(cacheKey);
//            if (matchingStopPlaceNetexIds != null) {
//                if (!matchingStopPlaceNetexIds.isEmpty()) {
//                    List<StopPlace> stopPlaces = matchingStopPlaceNetexIds.stream()
//                            .peek(matchingStopPlaceNetexId -> logger.debug("Cache match. Key {}, stop place id: {}", cacheKey, matchingStopPlaceNetexId))
//                            .map(matchingStopPlaceNetexId -> stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(matchingStopPlaceNetexId))
//                            .collect(Collectors.toList());
//
//                    if(!stopPlaces.isEmpty()) {
//                        return stopPlaces;
//                    }
//                }
//            }
//        }

        logger.debug("Looking for stop places from original IDs: {}", zeroPaddedOrUnchangedOriginalIds);

        // No cache match
        Set<String> stopPlaceNetexIds = stopPlaceRepository.findByKeyValues(ORIGINAL_ID_KEY, zeroPaddedOrUnchangedOriginalIds);
        return stopPlaceNetexIds
                .stream()
                .map(stopPlaceNetexId -> stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceNetexId))
                .collect(Collectors.toList());
    }

    private String keyValKey(String key, String value) {
        return key + "-" + value;
    }
}

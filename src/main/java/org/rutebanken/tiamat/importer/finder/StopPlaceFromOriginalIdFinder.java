/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.importer.finder;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.rutebanken.tiamat.general.PeriodicCacheLogger;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
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

    /**
     * One original ID can be used in multiple stop places
     */
    private Cache<String, Set<String>> keyValueCache;

    private final NetexIdHelper netexIdHelper;

    @Autowired
    public StopPlaceFromOriginalIdFinder(StopPlaceRepository stopPlaceRepository,
                                         @Value("${stopPlaceFromOriginalIdFinderCache.maxSize:50000}") int maximumSize,
                                         @Value("${stopPlaceFromOriginalIdFinderCache.expiresAfter:30}") int expiresAfter,
                                         @Value("${stopPlaceFromOriginalIdFinderCache.expiresAfterTimeUnit:DAYS}") TimeUnit expiresAfterTimeUnit,
                                         PeriodicCacheLogger periodicCacheLogger, NetexIdHelper netexIdHelper) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.netexIdHelper = netexIdHelper;
        keyValueCache = CacheBuilder.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(expiresAfter, expiresAfterTimeUnit)
                .recordStats()
                .build();

        periodicCacheLogger.scheduleCacheStatsLogging(keyValueCache, logger);
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

            String cacheKey = keyValKey(ORIGINAL_ID_KEY, originalId);

            Set<String> cachedValue = keyValueCache.getIfPresent(cacheKey);

            if(cachedValue != null && !cachedValue.isEmpty()) {
                logger.debug("Found existing value cached.");
                cachedValue.add(originalId);
                keyValueCache.put(cacheKey, cachedValue);
            } else {
                keyValueCache.put(cacheKey, Sets.newHashSet(originalId));
            }
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

        String stringPostfix = netexIdHelper.extractIdPostfix(originalId);

        if(stringPostfix.equals(originalId)) {
            logger.debug("Postfix cannot be extracted, leaving value as is: {}", originalId);
            return Stream.of(originalId);
        }

        try {
            Long numericPostFix = netexIdHelper.extractIdPostfixNumeric(originalId);

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

        for (String zeroPaddedOrUnchangedOriginalId : zeroPaddedOrUnchangedOriginalIds) {
            String cacheKey = keyValKey(ORIGINAL_ID_KEY, zeroPaddedOrUnchangedOriginalId);
            Set<String> matchingStopPlaceNetexIds = keyValueCache.getIfPresent(cacheKey);
            if (matchingStopPlaceNetexIds != null) {
                if (!matchingStopPlaceNetexIds.isEmpty()) {
                    List<StopPlace> stopPlaces = matchingStopPlaceNetexIds.stream()
                            .peek(matchingStopPlaceNetexId -> logger.debug("Cache match. Key {}, stop place id: {}", cacheKey, matchingStopPlaceNetexId))
                            .map(matchingStopPlaceNetexId -> stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(matchingStopPlaceNetexId))
                            .toList();

                    if(!stopPlaces.isEmpty()) {
                        return stopPlaces;
                    }
                }
            }
        }

        logger.debug("Looking for stop places from original IDs: {}", zeroPaddedOrUnchangedOriginalIds);

        // No cache match
        Set<String> stopPlaceNetexIds = stopPlaceRepository.findByKeyValues(ORIGINAL_ID_KEY, zeroPaddedOrUnchangedOriginalIds);
        return stopPlaceNetexIds
                .stream()
                .map(stopPlaceNetexId -> stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlaceNetexId))
                .peek(this::update)
                .toList();
    }

    private String keyValKey(String key, String value) {
        return key + "-" + value;
    }
}

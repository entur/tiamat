package org.rutebanken.tiamat.importer.finder;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.repository.ParkingRepository;
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
public class ParkingFromOriginalIdFinder implements ParkingFinder {

    private static final Logger logger = LoggerFactory.getLogger(ParkingFromOriginalIdFinder.class);

    private ParkingRepository parkingRepository;

    private Cache<String, Optional<String>> keyValueCache;

    public ParkingFromOriginalIdFinder(ParkingRepository parkingRepository,
                                       @Value("${parkingFromOriginalIdFinderCache.maxSize:50000}") int maximumSize,
                                       @Value("${parkingFromOriginalIdFinderCache.expiresAfter:30}") int expiresAfter,
                                       @Value("${parkingFromOriginalIdFinderCache.expiresAfterTimeUnit:DAYS}") TimeUnit expiresAfterTimeUnit) {
        this.parkingRepository = parkingRepository;
        keyValueCache = CacheBuilder.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(expiresAfter, expiresAfterTimeUnit)
                .build();
    }

    @Override
    public Parking find(Parking parking) {

        Set<String> originalIds = parking.getOrCreateValues(ORIGINAL_ID_KEY);

        if(originalIds.isEmpty()) return null;

        Parking existingParking = findByKeyValue(originalIds);

        if (existingParking != null) {
            logger.debug("Found parking {} from original ID", existingParking.getNetexId());
            return existingParking;
        }
        return null;
    }

    public void update(Parking parking) {
        if(parking.getNetexId() == null) {
            logger.warn("Attempt to update cache when parking does not have any ID! stop place: {}", parking);
            return;
        }
        for(String originalId : parking.getOrCreateValues(ORIGINAL_ID_KEY)) {
            keyValueCache.put(keyValKey(ORIGINAL_ID_KEY, originalId), Optional.ofNullable(parking.getNetexId()));
        }
    }

    private Parking findByKeyValue(Set<String> originalIds) {
        for(String originalId : originalIds) {
            String cacheKey = keyValKey(ORIGINAL_ID_KEY, originalId);
            Optional<String> matchingParkingNetexId = keyValueCache.getIfPresent(cacheKey);
            if(matchingParkingNetexId != null && matchingParkingNetexId.isPresent()) {
                logger.debug("Cache match. Key {}, parking id: {}", cacheKey, matchingParkingNetexId.get());
                return parkingRepository.findFirstByNetexIdOrderByVersionDesc(matchingParkingNetexId.get());
            }
        }

        // No cache match
        String parkingNetexId = parkingRepository.findFirstByKeyValues(ORIGINAL_ID_KEY, originalIds);
        if(parkingNetexId != null) {
            return parkingRepository.findFirstByNetexIdOrderByVersionDesc(parkingNetexId);
        }
        return null;
    }

    private String keyValKey(String key, String value) {
        return key + "-" + value;
    }
}

package org.rutebanken.tiamat.importer.finder;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StopPlaceBySomethingFinder {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceBySomethingFinder.class);


    // key = externalId
    private final Cache<String, SomethingWrapper> originalSomethingCache = CacheBuilder.newBuilder()
            .maximumSize(300000)
            .build();

    // netexid

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    public StopPlace findByExternalId(String externalId) {
        SomethingWrapper wrapper = originalSomethingCache.getIfPresent(externalId);
        if (wrapper != null) {
            return stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(wrapper.getNetexId());
        }
        return null;
    }

    public void updateCache(String externalId, SomethingWrapper somethingWrapper) {
        originalSomethingCache.put(externalId, somethingWrapper);
    }
}

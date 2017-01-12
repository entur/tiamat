package org.rutebanken.tiamat.importers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class NearbyStopPlaceFinder implements StopPlaceFinder {

    private static final double BOUNDING_BOX_BUFFER = 0.004;

    private static final Logger logger = LoggerFactory.getLogger(NearbyStopPlaceFinder.class);

    private StopPlaceRepository stopPlaceRepository;

    private final Cache<String, Optional<Long>> nearbyStopCache;

    @Autowired
    public NearbyStopPlaceFinder(StopPlaceRepository stopPlaceRepository,
                                         @Value("${nearbyStopPlaceFinderCache.maxSize:20000}") int maximumSize,
                                         @Value("${nearbyStopPlaceFinderCache.expiresAfter:30}") int expiresAfter,
                                         @Value("${nearbyStopPlaceFinderCache.expiresAfterTimeUnit:MINUTES}") TimeUnit expiresAfterTimeUnit) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.nearbyStopCache = CacheBuilder.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(expiresAfter, expiresAfterTimeUnit)
                .build();

    }

    @Override
    public StopPlace find(StopPlace stopPlace) {

        if(!stopPlace.hasCoordinates()) {
            return null;
        }

        try {
            Optional<Long> stopPlaceId = nearbyStopCache.get(createKey(stopPlace), () -> {
                Envelope boundingBox = createBoundingBox(stopPlace.getCentroid());
                return Optional.ofNullable(stopPlaceRepository.findNearbyStopPlace(boundingBox, stopPlace.getName().getValue()));
            });
            if(stopPlaceId.isPresent()) {
                return stopPlaceRepository.findOne(stopPlaceId.get());
            }
            return null;
        } catch (ExecutionException e) {
            logger.warn("Caught exception while finding stop place by key and value.", e);
            throw new RuntimeException(e);
        }
    }

    public void update(StopPlace savedStopPlace) {
        if(savedStopPlace.hasCoordinates()) {
            nearbyStopCache.put(createKey(savedStopPlace), Optional.ofNullable(savedStopPlace.getId()));
        }
    }

    public final String createKey(StopPlace stopPlace, Envelope envelope) {
        return stopPlace.getName() + "-" + envelope.toString();
    }

    public final String createKey(StopPlace stopPlace) {
        return createKey(stopPlace, createBoundingBox(stopPlace.getCentroid()));
    }

    public Envelope createBoundingBox(Point point) {

        Geometry buffer = point.buffer(BOUNDING_BOX_BUFFER);

        Envelope envelope = buffer.getEnvelopeInternal();
        logger.trace("Created envelope {}", envelope.toString());

        return envelope;
    }
}

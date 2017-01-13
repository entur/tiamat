package org.rutebanken.tiamat.importers;

import com.vividsolutions.jts.geom.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.rutebanken.tiamat.geo.EnvelopeCreator;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Some nearby stops like airports can be treated as the same if they are close enough.
 */
@Component
public class NearbyStopWithSameTypeFinder {

    private static final Logger logger = LoggerFactory.getLogger(NearbyStopWithSameTypeFinder.class);

    private static final int DEFAULT_LIMIT_METERS = 20;

    private final StopPlaceRepository stopPlaceRepository;

    private final EnvelopeCreator envelopeCreator;
    private final ConcurrentHashMap<StopTypeEnumeration, Integer> typesLimitMap;

    /**
     *
     * @param stopPlaceRepository
     * @param envelopeCreator
     */
    @Autowired
    public NearbyStopWithSameTypeFinder(StopPlaceRepository stopPlaceRepository,
                                        EnvelopeCreator envelopeCreator,
                                        @Value("${nearbyStopWithSameTypeFinder.airportLimit:2000}") int airportLimit,
                                        @Value("${nearbyStopWithSameTypeFinder.railStationLimit:300}") int railStationLimit) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.envelopeCreator = envelopeCreator;
        typesLimitMap = new ConcurrentHashMap<>(2);
        typesLimitMap.put(StopTypeEnumeration.AIRPORT, airportLimit);
        typesLimitMap.put(StopTypeEnumeration.RAIL_STATION, railStationLimit);
    }

    private int getLimit(StopPlace stopPlace) {
        Integer limit = typesLimitMap.get(stopPlace.getStopPlaceType());

        if(limit == null) {
            logger.warn("Could not find limit for stop place type {}. Returning default limit: {}", stopPlace.getStopPlaceType(), DEFAULT_LIMIT_METERS);
            return DEFAULT_LIMIT_METERS;
        }
        return limit;
    }

    public List<StopPlace> find(StopPlace stopPlace) {

        try {
            int limit = getLimit(stopPlace);

            Envelope envelope = envelopeCreator.createFromPoint(stopPlace.getCentroid(), limit);
            List<Long> stopPlacesIds = stopPlaceRepository.findNearbyStopPlace(envelope, stopPlace.getStopPlaceType());
            if(!stopPlacesIds.isEmpty()) {
                logger.debug("Found {} nearby matches on type with stop place ID", stopPlacesIds.size());

                return stopPlaceRepository.findAll(stopPlacesIds);
            }

            logger.debug("Could not find any stop places with type {} and envelope {}", stopPlace.getStopPlaceType(), envelope);

        } catch (FactoryException|TransformException e) {
            logger.error("Error finding nearby stop from type and buffer", e);
        }
        return new ArrayList<>();
    }



}

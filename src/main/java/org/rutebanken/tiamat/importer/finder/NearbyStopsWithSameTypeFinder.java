package org.rutebanken.tiamat.importer.finder;

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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Some nearby stops like airports can be treated as the same if they are close enough.
 */
@Component
public class NearbyStopsWithSameTypeFinder {

    private static final Logger logger = LoggerFactory.getLogger(NearbyStopsWithSameTypeFinder.class);

    private static final int DEFAULT_LIMIT_METERS = 20;

    private final StopPlaceRepository stopPlaceRepository;

    private final EnvelopeCreator envelopeCreator;
    private final ConcurrentHashMap<StopTypeEnumeration, Integer> typesLimitMap;

    /**
     *
     * @param stopPlaceRepository
     * @param envelopeCreator
     *
     */
    @Autowired
    public NearbyStopsWithSameTypeFinder(StopPlaceRepository stopPlaceRepository,
                                         EnvelopeCreator envelopeCreator,
                                         @Value("${nearbyStopWithSameTypeFinder.airportLimit:1000}") int airportLimit,
                                         @Value("${nearbyStopWithSameTypeFinder.railStationLimit:300}") int railStationLimit) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.envelopeCreator = envelopeCreator;
        typesLimitMap = new ConcurrentHashMap<>(2);
        typesLimitMap.put(StopTypeEnumeration.AIRPORT, airportLimit);
        typesLimitMap.put(StopTypeEnumeration.RAIL_STATION, railStationLimit);
    }

    public NearbyStopsWithSameTypeFinder(StopPlaceRepository stopPlaceRepository, EnvelopeCreator envelopeCreator) {
        this(stopPlaceRepository, envelopeCreator, 1000, 300);
    }

    private int getLimit(StopPlace stopPlace) {
        Integer limit = typesLimitMap.get(stopPlace.getStopPlaceType());

        if(limit == null) {
            logger.warn("Could not find limit for stop place type {}. Returning default limit: {}", stopPlace.getStopPlaceType(), DEFAULT_LIMIT_METERS);
            return DEFAULT_LIMIT_METERS;
        }
        logger.debug("Using limit {} for type {}", limit, stopPlace.getStopPlaceType());
        return limit;
    }

    public List<StopPlace> find(StopPlace stopPlace) {
        if(stopPlace.getStopPlaceType() == null) {
            logger.warn("Stop place does not have type set: {}", stopPlace);
            return Collections.emptyList();
        }

        try {
            int limit = getLimit(stopPlace);

            Envelope envelope = envelopeCreator.createFromPoint(stopPlace.getCentroid(), limit);
            List<Long> stopPlacesIds = stopPlaceRepository.findNearbyStopPlace(envelope, stopPlace.getStopPlaceType());
            if(!stopPlacesIds.isEmpty()) {
                logger.debug("Found {} nearby matches on type with stop place ID", stopPlacesIds.size());

                if(stopPlacesIds.size() > 1) {
                    logger.warn("Query for stop places returned more than one. Incoming stop place: {}. Result: ", stopPlace, stopPlacesIds);
                }

                return stopPlaceRepository.findAll(stopPlacesIds);
            }

            logger.debug("Could not find any stop places with type {} and envelope {}", stopPlace.getStopPlaceType(), envelope);

        } catch (FactoryException|TransformException e) {
            logger.error("Error finding nearby stop from type and buffer", e);
        }
        return Collections.emptyList();
    }

}

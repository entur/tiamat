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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Some nearby stops like airports can be treated as the same if they are close enough.
 */
@Component
public class NearbyStopsWithSameTypeFinder {

    private static final Logger logger = LoggerFactory.getLogger(NearbyStopsWithSameTypeFinder.class);

    private static final int DEFAULT_LIMIT_METERS = 20;

    private static final Map<String, Integer> DEFAULT_LIMITS = new HashMap<>();

    static {
        for (StopTypeEnumeration type : StopTypeEnumeration.values()) {
            if(type.equals(StopTypeEnumeration.AIRPORT)) {
                DEFAULT_LIMITS.put(type.value(), 3000);
            } else if (type.equals(StopTypeEnumeration.RAIL_STATION)) {
                DEFAULT_LIMITS.put(type.value(), 1000);
            }
            DEFAULT_LIMITS.put(type.value(), DEFAULT_LIMIT_METERS);
        }
    }

    private final StopPlaceRepository stopPlaceRepository;
    private final EnvelopeCreator envelopeCreator;

    private final ConcurrentHashMap<StopTypeEnumeration, Integer> typesLimitMap;

    /**
     *
     * @param stopPlaceRepository
     * @param envelopeCreator
     * @param limits map with limits per value of StopTypeEnumeration. In meters.
     */
    @Autowired
    private NearbyStopsWithSameTypeFinder(StopPlaceRepository stopPlaceRepository,
                                          EnvelopeCreator envelopeCreator,
                                          @Value("#{${nearbyStopsWithSameTypeFinder.limits:{airport:'3000',railStation:'1000'}}}") Map<String, Integer> limits) {

        this.envelopeCreator = envelopeCreator;
        this.stopPlaceRepository = stopPlaceRepository;

        typesLimitMap = new ConcurrentHashMap<>();

        for(String key : limits.keySet() ) {
            try {
                StopTypeEnumeration stopTypeEnumeration = StopTypeEnumeration.fromValue(key);
                typesLimitMap.put(stopTypeEnumeration, limits.get(key));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("The configured value '"+key+"': '"+limits.get(key)+"' is not possible to resolve as StopTypeEnumeration value.", e);
            }
        }

        logger.info("Limits configured: {}", typesLimitMap);
    }

    public NearbyStopsWithSameTypeFinder(StopPlaceRepository stopPlaceRepository, EnvelopeCreator envelopeCreator) {
        this(stopPlaceRepository, envelopeCreator, DEFAULT_LIMITS);
    }

    private int getLimit(StopPlace stopPlace) {
        Integer limit = typesLimitMap.get(stopPlace.getStopPlaceType());

        if(limit == null) {
            logger.debug("Could not find limit for stop place type {}. Returning default limit: {}", stopPlace.getStopPlaceType(), DEFAULT_LIMIT_METERS);
            return DEFAULT_LIMIT_METERS;
        }
        logger.debug("Using limit {} for type {}", limit, stopPlace.getStopPlaceType());
        return limit;
    }

    public List<StopPlace> find(StopPlace stopPlace) {
        if(stopPlace.getStopPlaceType() == null) {
            logger.warn("Stop place does not have type set: {}", stopPlace);
            return Collections.emptyList();
        } else if(stopPlace.getCentroid() == null) {
            return Collections.emptyList();
        }

        try {
            int limit = getLimit(stopPlace);

            Envelope envelope = envelopeCreator.createFromPoint(stopPlace.getCentroid(), limit);
            List<Long> stopPlacesIds = stopPlaceRepository.findNearbyStopPlace(envelope, stopPlace.getStopPlaceType());
            if(!stopPlacesIds.isEmpty()) {
                logger.debug("Found {} nearby matches on type with stop place ID", stopPlacesIds.size());

                if(stopPlacesIds.size() > 1) {
                    logger.warn("Query for stop places returned more than one. Incoming stop place: {}. Result: {}", stopPlace, stopPlacesIds);
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

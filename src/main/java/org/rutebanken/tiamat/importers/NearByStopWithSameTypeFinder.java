package org.rutebanken.tiamat.importers;

import com.vividsolutions.jts.geom.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.rutebanken.tiamat.geo.EnvelopeCreator;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Some nearby stops like airports can be treated as the same if they are close enough.
 */
@Component
public class NearByStopWithSameTypeFinder implements StopPlaceFinder {

    private static final Logger logger = LoggerFactory.getLogger(NearByStopWithSameTypeFinder.class);

    private final StopPlaceRepository stopPlaceRepository;

    private final EnvelopeCreator envelopeCreator;

    /**
     *
     * @param stopPlaceRepository
     * @param envelopeCreator
     */
    @Autowired
    public NearByStopWithSameTypeFinder(StopPlaceRepository stopPlaceRepository, EnvelopeCreator envelopeCreator) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.envelopeCreator = envelopeCreator;
    }


    @Override
    public StopPlace find(StopPlace stopPlace) {


        try {
            Envelope envelope = envelopeCreator.createFromPoint(stopPlace.getCentroid(), 800.00);
            long stopPlaceId = stopPlaceRepository.findNearbyStopPlace(envelope, stopPlace.getStopPlaceType());
            if(stopPlaceId > 0) {
                logger.debug("Found nearby match on type with stop place ID {}", stopPlaceId);
                return stopPlaceRepository.findOne(stopPlaceId);
            }
        } catch (FactoryException|TransformException e) {
            logger.error("Could not find nearby stop from type and buffer", e);
        }
        return null;
    }



}

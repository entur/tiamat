package org.rutebanken.tiamat.importer.modifier;

import org.apache.commons.lang3.SerializationUtils;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.operation.TransformException;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Component
public class StopPlaceSplitter {

    private static final int QUAY_DISTANCE = 1000;

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceSplitter.class);

    public List<StopPlace> split(List<StopPlace> stops) {
        logger.info("Looking for quays eligible for splitting in {} stop places based on quay distance", stops.size());
        List<StopPlace> result = new ArrayList<>();

        stops.forEach(originalStop -> {
            splitQuaysFromStops(originalStop, result);
        });
        logger.info("Created {} new stops. Returning {} stop places in total", result.size()-stops.size(), result.size());

        return result;
    }

    private void splitQuaysFromStops(StopPlace originalStop, List<StopPlace> result) {
        if(originalStop.getQuays() != null) {

            List<List<Quay>> quayGroups = new ArrayList<>();

            logger.debug("Inspecting quays: {}", originalStop.getQuays());

            Iterator<Quay> stopPlaceQuaysIterator = originalStop.getQuays().iterator();
            while(stopPlaceQuaysIterator.hasNext()) {
                Quay quay = stopPlaceQuaysIterator.next();
                logger.debug("Candidate: {}", quay.getOriginalIds());

                if(quayGroups.isEmpty()) {
                    logger.debug("Empty group. Adding quay");
                    quayGroups.add(new ArrayList<>(Arrays.asList(quay)));
                } else {

                    boolean wasGrouped = false;

                    for (ListIterator<List<Quay>> quayGroupIterator = quayGroups.listIterator(); quayGroupIterator.hasNext();) {
                        List<Quay> existingQuayList = quayGroupIterator.next();

                         wasGrouped = addIfClose(quay, existingQuayList);

                        if(wasGrouped) {
                            // Was grouped in existing quay group. No need to look further
                            break;
                        }
                    }

                    if(!wasGrouped) {
                        // Was not grouped. Which means split out and create new group, then break to avoid splitting/grouping more.
                        logger.debug("Splitting quay {} from stop {} as it is too far away from previous quay.", quay.getOriginalIds(), originalStop.getOriginalIds());
                        quayGroups.add(new ArrayList<>(Arrays.asList(quay)));

                    }
                }
            }

            if(quayGroups.isEmpty() || quayGroups.size() == 1) {
                result.add(originalStop);
            } else {
                result.addAll(createStopsFromQuayGroups(originalStop, quayGroups));
            }
        } else {
            logger.debug("Did not extract any quays from stop place: {}", originalStop.getOriginalIds());
            result.add(originalStop);
        }
    }

    private List<StopPlace> createStopsFromQuayGroups(StopPlace originalStopPlace, List<List<Quay>> quayGroups) {

        originalStopPlace.setCentroid(null);
        return quayGroups.stream()
                .map(quayList -> {
                    StopPlace newStopPlace = SerializationUtils.clone(originalStopPlace);
                    newStopPlace.getQuays().clear();
                    newStopPlace.getOriginalIds().clear();
                    if(!quayList.get(0).getOriginalIds().isEmpty()) {
                        String generatedOriginalId = quayList.get(0).getOriginalIds().iterator().next()+"-generated";
                        newStopPlace.getOriginalIds().add(generatedOriginalId);
                    }

                    newStopPlace.getQuays().addAll(quayList);
                    logger.debug("Created stop with {} quays", quayList.size());
                    return newStopPlace;
                })
                .collect(toList());
    }

    private boolean addIfClose(Quay quay, List<Quay> existingQuayList) {
        for(ListIterator<Quay> alreadyAddedQuayIterator = existingQuayList.listIterator(); alreadyAddedQuayIterator.hasNext();) {
            Quay alreadyAddedQuay = alreadyAddedQuayIterator.next();

            if(quay == alreadyAddedQuay) {
                logger.debug("Quay already added {}", quay.getOriginalIds());
                return true;
            } else if(quay.getCentroid() == null && alreadyAddedQuay.getCentroid() != null) {
                return false;
            } else if (quay.getCentroid() == null && alreadyAddedQuay.getCentroid() == null) {
                alreadyAddedQuayIterator.add(quay);
                return true;
            } else if(close(quay, alreadyAddedQuay)) {
                logger.debug("Quays are close. grouping {} together with: {}", quay.getOriginalIds(), alreadyAddedQuay.getOriginalIds());
                alreadyAddedQuayIterator.add(quay);
                return true;
            }
        }
        return false;
    }

    private boolean close(Quay quay1, Quay quay2) {
        logger.debug("Checking distance between {} and {}", quay1.getOriginalIds(), quay2.getOriginalIds());

        try {
            double distanceInMeters = JTS.orthodromicDistance(
                    quay1.getCentroid().getCoordinate(),
                    quay2.getCentroid().getCoordinate(),
                    DefaultGeographicCRS.WGS84);
            return distanceInMeters < QUAY_DISTANCE;
        } catch (TransformException e) {
            logger.warn("Error checking distance between {} and {}", quay1, quay2, e);
        }
        return true;
    }

}

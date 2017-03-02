package org.rutebanken.tiamat.importer;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.operation.TransformException;
import org.rutebanken.tiamat.model.MultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class QuayMerger {

    private static final Logger logger = LoggerFactory.getLogger(QuayMerger.class);

    @Value("${quayMerger.mergeDistanceMeters:10}")
    public final double MERGE_DISTANCE_METERS = 10;

    @Value("${quayMerger.mergeDistanceMetersExtended:30}")
    public final double MERGE_DISTANCE_METERS_EXTENDED = 30;

    @Value("${quayMerger.maxCompassBearingDifference:60}")
    private final int maxCompassBearingDifference = 60;

    private final VersionIncrementor versionIncrementor;

    @Autowired
    public QuayMerger(VersionIncrementor versionIncrementor) {
        this.versionIncrementor = versionIncrementor;
    }

    /**
     * Inspect quays from incoming AND matching stop place. If they do not exist from before, add them.
     */
    public boolean addNewQuaysOrAppendImportIds(StopPlace newStopPlace, StopPlace existingStopPlace) {

        AtomicInteger updatedQuays = new AtomicInteger();
        AtomicInteger addedQuays = new AtomicInteger();

        logger.debug("About to compare quays for {}", existingStopPlace.getNetexId());

        if (newStopPlace.getQuays() == null) {
            newStopPlace.setQuays(new HashSet<>());
        }

        Set<Quay> result = addNewQuaysOrAppendImportIds(newStopPlace.getQuays(), existingStopPlace.getQuays(), updatedQuays, addedQuays);

        existingStopPlace.setQuays(result);

        logger.debug("Created {} quays and updated {} quays for stop place {}", addedQuays.get(), updatedQuays.get(), existingStopPlace);
        return addedQuays.get() > 0 || updatedQuays.get() > 0;
    }

    public Set<Quay> addNewQuaysOrAppendImportIds(Set<Quay> newQuays, Set<Quay> existingQuays, AtomicInteger updatedQuaysCounter, AtomicInteger addedQuaysCounter) {

        Set<Quay> result = new HashSet<>();
        if(existingQuays != null) {
            result.addAll(existingQuays);
        }

        for(Quay incomingQuay : newQuays) {
            Optional<Quay> matchingQuay = findMatchOnOriginalId(incomingQuay, result);

            if(!matchingQuay.isPresent()) {
                matchingQuay = findMatch(incomingQuay, result);
            }

            if(matchingQuay.isPresent()) {
                updateIfChanged(matchingQuay.get(), incomingQuay, updatedQuaysCounter);
            } else {
                logger.info("Found no match for existing quay {}. Adding it!", incomingQuay);
                result.add(incomingQuay);
                incomingQuay.setCreated(ZonedDateTime.now());
                incomingQuay.setChanged(ZonedDateTime.now());
                versionIncrementor.incrementVersion(incomingQuay);
                addedQuaysCounter.incrementAndGet();
            }
        }

        return result;
    }

    private Optional<Quay> findMatch(Quay incomingQuay, Set<Quay> result) {
        for (Quay alreadyAdded : result) {
            if (matches(incomingQuay, alreadyAdded)) {
                return Optional.of(alreadyAdded);
            }
        }
        return Optional.empty();
    }

    private Optional<Quay> findMatchOnOriginalId(Quay incomingQuay, Set<Quay> result) {
        for(Quay alreadyAdded : result) {
            if(matchesOnOriginalId(incomingQuay, alreadyAdded)) {
                return Optional.of(alreadyAdded);
            }
        }
        return Optional.empty();
    }

    private void updateIfChanged(Quay alreadyAdded, Quay incomingQuay, AtomicInteger updatedQuaysCounter) {
        // The incoming quay could for some reason already have multiple imported IDs.
        boolean idUpdated = alreadyAdded.getOriginalIds().addAll(incomingQuay.getOriginalIds());
        boolean changedByMerge = mergeFields(incomingQuay, alreadyAdded);

        if(idUpdated || changedByMerge) {
            alreadyAdded.setChanged(ZonedDateTime.now());
            updatedQuaysCounter.incrementAndGet();
            versionIncrementor.incrementVersion(alreadyAdded);
        }
    }

    private boolean mergeFields(Quay from, Quay to) {
        boolean changed = false;
        if(hasNameValue(from.getName()) && ! hasNameValue(to.getName())) {
            to.setName(from.getName());
            changed = true;
        }
        if(from.getCompassBearing() != null && to.getCompassBearing() == null) {
            to.setCompassBearing(from.getCompassBearing());
            changed = true;
        }

        return changed;
    }

    private boolean matches(Quay incomingQuay, Quay alreadyAdded) {
        boolean nameMatch = haveMatchingNameOrOneIsMissing(incomingQuay, alreadyAdded);
        boolean publicCodeMatch = haveMatchingPublicCode(incomingQuay, alreadyAdded);

        if (areClose(incomingQuay, alreadyAdded, MERGE_DISTANCE_METERS)
                && haveSimilarOrAnyNullCompassBearing(incomingQuay, alreadyAdded)
                && nameMatch
                && publicCodeMatch) {
            return true;
        } else if(nameMatch && publicCodeMatch && haveSimilarCompassBearing(incomingQuay, alreadyAdded)) {
            logger.debug("Name and compass bearing match. Will compare with a greater limit of distance between quays. {}  {}", incomingQuay, alreadyAdded);

            if(areClose(incomingQuay, alreadyAdded, MERGE_DISTANCE_METERS_EXTENDED)) {
                return true;
            }
        }
        return false;
    }

    /**
     * If the incoming Quay has an original ID that matches on any original ID on an existing Quay.
     * @param incomingQuay incoming Quay
     * @param alreadyAdded the quay that is already added to the stop place's list of quays
     * @return true if found match
     */
    private boolean matchesOnOriginalId(Quay incomingQuay, Quay alreadyAdded) {
        Set<String> strippedAlreadyAddedIds = removePrefixesFromIds(alreadyAdded.getOriginalIds());
        Set<String> strippedIncomingIds = removePrefixesFromIds(incomingQuay.getOriginalIds());

        if(!Collections.disjoint(strippedAlreadyAddedIds, strippedIncomingIds)) {
            logger.info("New quay matches on original ID: {}. Adding all new IDs if any. Existing quay ID: {}", incomingQuay, alreadyAdded.getNetexId());
            return true;
        }
        return false;
    }

    private Set<String> removePrefixesFromIds(Set<String> originalIds) {
        Set<String> strippedIds = new HashSet<>(originalIds.size());
        originalIds.forEach(completeId -> {
            if(completeId.contains(":")) {
                strippedIds.add(completeId.substring(completeId.indexOf(':')));
            } else {
                logger.info("Cannot strip prefix from ID {} as it does not contain colon", completeId);
                strippedIds.add(completeId);
            }
        });
        return strippedIds;
    }

    public boolean haveMatchingNameOrOneIsMissing(Quay quay1, Quay quay2) {
        boolean quay1HasName = hasNameValue(quay1.getName());
        boolean quay2HasName = hasNameValue(quay2.getName());

        if(!quay1HasName && !quay2HasName) {
            logger.debug("None of the quays have name set. Treating as match. {} - {}", quay1.getName(), quay2.getName());
            return true;
        }

        if((quay1HasName && !quay2HasName) || (!quay1HasName && quay2HasName)) {
            logger.debug("Only one of the quays have name set. Treating as match. {} - {}", quay1.getName(), quay2.getName());
            return true;
        }

        if(quay1.getName().getValue().equals(quay2.getName().getValue())) {
            logger.debug("Quay names matches. {} - {}", quay1.getName(), quay2.getName());
            return true;
        }

        logger.debug("Both quays does have names, but they do not match. {} - {}", quay1.getName(), quay2.getName());
        return false;
    }

    private boolean hasNameValue(MultilingualString multilingualString) {
        return multilingualString != null && !Strings.isNullOrEmpty(multilingualString.getValue());
    }

    public boolean areClose(Quay quay1, Quay quay2) {
        return areClose(quay1, quay2, MERGE_DISTANCE_METERS);
    }

    public boolean areClose(Quay quay1, Quay quay2, double mergeDistanceInMeters) {
        if (!quay1.hasCoordinates() || !quay2.hasCoordinates()) {
            return false;
        }

        try {
            double distanceInMeters = JTS.orthodromicDistance(
                    quay1.getCentroid().getCoordinate(),
                    quay2.getCentroid().getCoordinate(),
                    DefaultGeographicCRS.WGS84);

            logger.info("Distance in meters between quays is {} meters. {} - {}", distanceInMeters, quay1, quay2);

            return distanceInMeters < mergeDistanceInMeters;
        } catch (TransformException e) {
            logger.warn("Could not calculate distance between quays {} - {}", quay1, quay2, e);
            return false;
        }
    }

    public boolean haveSimilarOrAnyNullCompassBearing(Quay quay1, Quay quay2) {

        if(quay1.getCompassBearing() == null && quay2.getCompassBearing() == null) {
            return true;
        } else if ((quay1.getCompassBearing() == null && quay2.getCompassBearing() != null) || (quay1.getCompassBearing() != null && quay2.getCompassBearing() == null)) {
            return true;
        }

        return haveSimilarCompassBearing(quay1, quay2);
    }

    private boolean haveSimilarCompassBearing(Quay quay1, Quay quay2) {

        if(quay1.getCompassBearing() == null || quay2.getCompassBearing() == null) {
            return false;
        }
        int quayBearing1 = Math.round(quay1.getCompassBearing());
        int quayBearing2 = Math.round(quay2.getCompassBearing());

        int difference = Math.abs(getAngle(quayBearing1, quayBearing2));

        if (difference > maxCompassBearingDifference) {
            logger.debug("Quays have too much difference in compass bearing {}. {} {}", difference, quay1, quay2);
            return false;
        }

        logger.debug("Compass bearings for quays has less difference than the limit {}. {} {}", difference, quay1, quay2);
        return true;
    }

    private boolean haveMatchingPublicCode(Quay quay1, Quay quay2) {
        return Objects.equals(quay1.getPublicCode(), quay2.getPublicCode());
    }

    private int getAngle(Integer bearing, Integer heading) {
        return ((((bearing - heading) % 360) + 540) % 360) - 180;

    }


}

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

package org.rutebanken.tiamat.importer.matching;

import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.importer.AlternativeStopTypes;
import org.rutebanken.tiamat.importer.KeyValueListAppender;
import org.rutebanken.tiamat.importer.finder.NearbyStopPlaceFinder;
import org.rutebanken.tiamat.importer.finder.StopPlaceByIdFinder;
import org.rutebanken.tiamat.importer.merging.QuayMerger;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.geo.ZoneDistanceChecker;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Component
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class TransactionalMatchingAppendingStopPlaceImporter {

    private static final Logger logger = LoggerFactory.getLogger(TransactionalMatchingAppendingStopPlaceImporter.class);

    private static final boolean CREATE_NEW_QUAYS = false;

    private static final boolean ALLOW_OTHER_TYPE_AS_ANY_MATCH = true;

    @Autowired
    private KeyValueListAppender keyValueListAppender;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private QuayMerger quayMerger;

    @Autowired
    private NetexMapper netexMapper;

    @Autowired
    private NearbyStopPlaceFinder nearbyStopPlaceFinder;

    @Autowired
    private StopPlaceByIdFinder stopPlaceByIdFinder;

    @Autowired
    private ZoneDistanceChecker zoneDistanceChecker;

    @Autowired
    private AlternativeStopTypes alternativeStopTypes;


    public void findAppendAndAdd(final org.rutebanken.tiamat.model.StopPlace incomingStopPlace,
                                 List<StopPlace> matchedStopPlaces,
                                 AtomicInteger stopPlacesCreatedOrUpdated) {


        List<org.rutebanken.tiamat.model.StopPlace> foundStopPlaces = stopPlaceByIdFinder.findStopPlace(incomingStopPlace);
        final int foundStopPlacesCount = foundStopPlaces.size();

        if(!foundStopPlaces.isEmpty()) {

            List<org.rutebanken.tiamat.model.StopPlace> filteredStopPlaces = foundStopPlaces
                    .stream()
                    .filter(foundStopPlace -> {
                        if(zoneDistanceChecker.exceedsLimit(incomingStopPlace, foundStopPlace)) {
                            logger.warn("Found stop place, but the distance between incoming and found stop place is too far in meters: {}. Incoming: {}. Found: {}",
                                    ZoneDistanceChecker.DEFAULT_MAX_DISTANCE,
                                    incomingStopPlace,
                                    foundStopPlace);
                            return false;
                        }
                        return true;
                    })
                    .filter(foundStopPlace -> {

                        if(foundStopPlacesCount == 1) {
                            logger.info("There are only one found stop places. Filtering in stop place regardless of type {}", foundStopPlace);
                            return true;
                        }

                        if(incomingStopPlace.getStopPlaceType() == null) {
                            logger.info("Incoming stop place type is null. Filter in. {}", incomingStopPlace);
                            return true;
                        }

                        if(incomingStopPlace.getStopPlaceType().equals(StopTypeEnumeration.OTHER)) {
                            logger.info("Incoming stop place type is OTHER. Filter in. {}", incomingStopPlace);
                            return true;
                        }

                        if(foundStopPlace.getStopPlaceType().equals(incomingStopPlace.getStopPlaceType())
                                || alternativeStopTypes.matchesAlternativeType(foundStopPlace.getStopPlaceType(), incomingStopPlace.getStopPlaceType())) {
                            return true;
                        }

                        logger.warn("Found match for incoming stop place {}, but the type does not match: {} != {}. Filter out. Incoming stop: {}", foundStopPlace.getNetexId(), incomingStopPlace.getStopPlaceType(), foundStopPlace.getStopPlaceType(), incomingStopPlace);

                        return false;
                    })
                    // Collect distinct on ID
                    .collect(toList());

            foundStopPlaces = filteredStopPlaces;
        }

        if(foundStopPlaces.isEmpty()) {
            org.rutebanken.tiamat.model.StopPlace nearbyStopPlace = nearbyStopPlaceFinder.find(incomingStopPlace, ALLOW_OTHER_TYPE_AS_ANY_MATCH);
            if(nearbyStopPlace !=  null) {
                foundStopPlaces = Arrays.asList(nearbyStopPlace);
            }
        }

        if(foundStopPlaces.isEmpty()) {
            logger.warn("Cannot find stop place from IDs or location: {}. StopPlace toString: {}",
                    incomingStopPlace.importedIdAndNameToString(),
                    incomingStopPlace);
        } else {

            if(foundStopPlaces.size() > 1) {
                logger.warn("Found {} matches for incoming stop place {}. Matches: {}", foundStopPlaces.size(), incomingStopPlace, foundStopPlaces);
            }

            for(org.rutebanken.tiamat.model.StopPlace existingStopPlace : foundStopPlaces) {

                logger.debug("Found matching stop place {}", existingStopPlace);

                keyValueListAppender.appendToOriginalId(NetexIdMapper.ORIGINAL_ID_KEY, incomingStopPlace, existingStopPlace);

                if (incomingStopPlace.getTariffZones() != null) {
                    if (existingStopPlace.getTariffZones() == null) {
                        existingStopPlace.setTariffZones(new HashSet<>());
                    }
                    existingStopPlace.getTariffZones().addAll(incomingStopPlace.getTariffZones());
                }

                quayMerger.appendImportIds(incomingStopPlace, existingStopPlace, CREATE_NEW_QUAYS);

                existingStopPlace = stopPlaceRepository.save(existingStopPlace);
                String netexId = incomingStopPlace.getNetexId();

                matchedStopPlaces.removeIf(stopPlace -> stopPlace.getId().equals(netexId));

                matchedStopPlaces.add(netexMapper.mapToNetexModel(existingStopPlace));
                
                stopPlacesCreatedOrUpdated.incrementAndGet();

            }
        }
    }
}

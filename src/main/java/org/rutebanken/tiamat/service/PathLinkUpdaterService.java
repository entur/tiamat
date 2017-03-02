package org.rutebanken.tiamat.service;

import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.NoSuchElementException;

import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.getNetexId;

@Transactional
@Service
public class PathLinkUpdaterService {

    private static final Logger logger = LoggerFactory.getLogger(PathLinkUpdaterService.class);

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private PathLinkRepository pathLinkRepository;


    public PathLink createOrUpdatePathLink(PathLink incomingPathLink) {

        PathLink resultPathLink;

        boolean updatedExisting;

        if(incomingPathLink.getNetexId() != null) {
            // Update?

            logger.debug("Looking for PathLink with ID: {}", incomingPathLink.getNetexId());

            PathLink existingPathLink = pathLinkRepository.findByNetexId(incomingPathLink.getNetexId());

            if(existingPathLink == null) {
                throw new NoSuchElementException("Specified path link with ID: " + incomingPathLink.getNetexId() + " does not exist");
            }

            logger.debug("Found existing path link: {}", existingPathLink);

            boolean changed = false;
            if(incomingPathLink.getLineString() != null) {
                existingPathLink.setLineString(incomingPathLink.getLineString());
                changed = true;
            }
            if(incomingPathLink.getAllowedUse() != null) {
                existingPathLink.setAllowedUse(incomingPathLink.getAllowedUse());
                changed = true;
            }
            if(incomingPathLink.getTransferDuration() != null) {
                TransferDuration incomingTransferDuration = incomingPathLink.getTransferDuration();

                if(existingPathLink.getTransferDuration() != null) {
                    if (incomingTransferDuration.getDefaultDuration() != null) {
                        existingPathLink.getTransferDuration().setDefaultDuration(incomingTransferDuration.getDefaultDuration());
                        changed = true;
                    }
                    if (incomingTransferDuration.getFrequentTravellerDuration() != null) {
                        existingPathLink.getTransferDuration().setFrequentTravellerDuration(incomingTransferDuration.getFrequentTravellerDuration());
                        changed = true;
                    }
                    if (incomingTransferDuration.getOccasionalTravellerDuration() != null) {
                        existingPathLink.getTransferDuration().setOccasionalTravellerDuration(incomingTransferDuration.getOccasionalTravellerDuration());
                        changed = true;
                    }
                    if (incomingTransferDuration.getMobilityRestrictedTravellerDuration() != null) {
                        existingPathLink.getTransferDuration().setMobilityRestrictedTravellerDuration(incomingTransferDuration.getMobilityRestrictedTravellerDuration());
                        changed = true;
                    }
                } else {
                    existingPathLink.setTransferDuration(incomingTransferDuration);
                    changed = true;
                }

                if(changed) {
                    logger.debug("Existing pathLink changed");
                    existingPathLink.setChanged(ZonedDateTime.now());
                }
            }

            resultPathLink = existingPathLink;
            updatedExisting = true;

        } else {
            logger.info("New incoming path link {}", incomingPathLink);
            resultPathLink = incomingPathLink;
            updatedExisting = false;
        }


        if(incomingPathLink.getFrom() != null) {
            PathLinkEnd from = loadPathLinkEndReference(incomingPathLink.getFrom());
            resultPathLink.setFrom(from);
        }
        if(incomingPathLink.getTo() != null) {
            PathLinkEnd to = loadPathLinkEndReference(incomingPathLink.getTo());
            resultPathLink.setTo(to);
        }

        pathLinkRepository.save(resultPathLink);

        logger.info("{} {}", updatedExisting ? "Updated" : "Created", resultPathLink);

        return resultPathLink;

    }

    private PathLinkEnd loadPathLinkEndReference(PathLinkEnd pathLinkEnd) {

        if(pathLinkEnd.getQuay() != null) {
            // Quay is only mapped with ID. Need to look it up and replace the reference.

            Quay quay = quayRepository.findByNetexId(pathLinkEnd.getQuay().getNetexId());
            if(quay != null) {
                logger.debug("Found quay {}", quay);
                pathLinkEnd.setQuay(quay);
                return pathLinkEnd;
            }
            throw new NoSuchElementException("Cannot find path link end reference to quay " + pathLinkEnd.getQuay().getNetexId());
        }

        if(pathLinkEnd.getStopPlace() != null) {
            StopPlace stopPlace = stopPlaceRepository.findByNetexId(pathLinkEnd.getStopPlace().getNetexId());
            if(stopPlace != null) {
                logger.info("Found stop place {}", stopPlace);
                pathLinkEnd.setStopPlace(stopPlace);
                return pathLinkEnd;
            }
            throw new NoSuchElementException("Cannot find path link end reference to stop place " + pathLinkEnd.getStopPlace().getNetexId());
        }

        throw new NoSuchElementException("Cannot find path link end reference (quay/stop/..) for path link end " + pathLinkEnd);

        // TODO entrance/level
    }


}

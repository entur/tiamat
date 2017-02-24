package org.rutebanken.tiamat.service;

import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.model.PathLinkEnd;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if(incomingPathLink.getId() != null) {
            // Update?

            logger.debug("Looking for PathLink with ID: {}", incomingPathLink.getId());

            PathLink existingPathLink = pathLinkRepository.findOne(incomingPathLink.getId());

            if(existingPathLink == null) {
                logger.warn("Specified path link with ID: {} does not exist", incomingPathLink.getId());
                return null;
            }

            logger.debug("Found existing path link: {}", existingPathLink);

            existingPathLink.setLineString(incomingPathLink.getLineString());
            existingPathLink.setAllowedUse(incomingPathLink.getAllowedUse());
            existingPathLink.setTransferDuration(incomingPathLink.getTransferDuration());

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

            Quay quay = quayRepository.findOne(pathLinkEnd.getQuay().getId());
            if(quay != null) {
                logger.debug("Found quay {}", quay);
                pathLinkEnd.setQuay(quay);
                return pathLinkEnd;
            }
        }

        if(pathLinkEnd.getStopPlace() != null) {
            StopPlace stopPlace = stopPlaceRepository.findOne(pathLinkEnd.getStopPlace().getId());
            if(stopPlace != null) {
                logger.info("Found stop place {}", stopPlace);
                pathLinkEnd.setStopPlace(stopPlace);
                return pathLinkEnd;
            }
        }

        return null;
        // TODO entrance/level
    }


}

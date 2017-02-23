package org.rutebanken.tiamat.rest.graphql;

import graphql.language.Field;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.model.PathLinkEnd;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.rest.graphql.resolver.PathLinkMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.MUTATE_PATH_LINK;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_PATH_LINK;

@Service("pathLinkUpdater")
@Transactional
class PathLinkUpdater implements DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(PathLinkUpdater.class);

    @Autowired
    private PathLinkRepository pathLinkRepository;

    @Autowired
    private PathLinkMapper pathLinkMapper;

    @Autowired
    private QuayRepository quayRepository;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Override
    public Object get(DataFetchingEnvironment environment) {

        List<Field> fields = environment.getFields();

        logger.trace("Got fields {}", fields);

//        try {

            for (Field field : fields) {
                if (field.getName().equals(MUTATE_PATH_LINK)) {

                    if (environment.getArgument(OUTPUT_TYPE_PATH_LINK) != null) {
                        Map input = environment.getArgument(OUTPUT_TYPE_PATH_LINK);
                        PathLink pathLink = pathLinkMapper.map(input);
                        logger.debug("Mapped {}", pathLink);

                        createOrUpdatePathLink(pathLink);

                    } else {
                        logger.warn("Could not find argument {}", OUTPUT_TYPE_PATH_LINK);
                    }

                }
            }
//        } catch (RuntimeException e) {
//            logger.warn("Caught exception when updating path link", e);
//            throw e;
//        }


        return pathLinkRepository.findAll();
    }

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

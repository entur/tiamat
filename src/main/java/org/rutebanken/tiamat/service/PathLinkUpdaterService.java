package org.rutebanken.tiamat.service;

import graphql.GraphQLException;

import org.rutebanken.helper.organisation.AuthorizationConstants;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.PathLink;
import org.rutebanken.tiamat.model.PathLinkEnd;
import org.rutebanken.tiamat.model.TransferDuration;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.repository.ReferenceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

@Transactional
@Service
public class PathLinkUpdaterService {

    private static final Logger logger = LoggerFactory.getLogger(PathLinkUpdaterService.class);

    @Autowired
    private PathLinkRepository pathLinkRepository;

    @Autowired
    private ReferenceResolver referenceResolver;

    @Autowired
    private AuthorizationService authorizationService;

    public PathLink createOrUpdatePathLink(PathLink incomingPathLink) {
        Set<EntityInVersionStructure> entitiesRequiringAuthorization=new HashSet<>();
        PathLink resultPathLink;

        boolean updatedExisting;

        if(incomingPathLink.getNetexId() != null) {
            // Update?

            logger.debug("Looking for PathLink with ID: {} and version: {}", incomingPathLink.getNetexId(), incomingPathLink.getVersion());

            PathLink existingPathLink = pathLinkRepository.findFirstByNetexIdOrderByVersionDesc(incomingPathLink.getNetexId());

            if(existingPathLink == null) {
                throw new NoSuchElementException("Specified path link with ID: " + incomingPathLink.getNetexId() + " does not exist");
            }

            if(incomingPathLink.getVersion() > 0 && existingPathLink.getVersion() != incomingPathLink.getVersion()) {
                throw new GraphQLException("Incoming PathLink version differs from existing path link with id " + incomingPathLink.getNetexId() + ", version: " + existingPathLink.getVersion() + " != " + incomingPathLink.getVersion());
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


	    if (incomingPathLink.getFrom() != null) {
		    EntityInVersionStructure from = verifyPathLinkReferences(incomingPathLink.getFrom());
		    entitiesRequiringAuthorization.add(from);
	    }
	    if (incomingPathLink.getTo() != null) {
		    EntityInVersionStructure to = verifyPathLinkReferences(incomingPathLink.getTo());
		    entitiesRequiringAuthorization.add(to);
	    }

        authorizationService.assertAuthorized(AuthorizationConstants.ROLE_EDIT_STOPS,entitiesRequiringAuthorization);
        pathLinkRepository.save(resultPathLink);

        logger.info("{} {}", updatedExisting ? "Updated" : "Created", resultPathLink);

        return resultPathLink;

    }

    private EntityInVersionStructure verifyPathLinkReferences(PathLinkEnd pathLinkEnd) {

        if(pathLinkEnd.getPlaceRef() != null) {
            EntityInVersionStructure entityInVersionStructure = referenceResolver.resolve(pathLinkEnd.getPlaceRef());

            if(entityInVersionStructure == null) {
                throw new NoSuchElementException("Cannot find path link end reference to place " + pathLinkEnd.getPlaceRef());
            }
            return entityInVersionStructure;
        }
        throw new NoSuchElementException("Cannot find path link end reference (quay/stop/..) for path link end ref" + pathLinkEnd.getPlaceRef());
    }


}

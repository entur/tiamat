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

package org.rutebanken.tiamat.service.stopplace;

import graphql.GraphQLException;
import org.rutebanken.helper.organisation.AuthorizationConstants;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.PathLinkRepository;
import org.rutebanken.tiamat.repository.reference.ReferenceResolver;
import org.rutebanken.tiamat.lock.MutateLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A service for updating path links.
 */
@Transactional
@Service
public class PathLinkUpdaterService {

    private static final Logger logger = LoggerFactory.getLogger(PathLinkUpdaterService.class);

    @Autowired
    private PathLinkRepository pathLinkRepository;

    @Autowired
    private ReferenceResolver referenceResolver;

    @Autowired
    private ReflectionAuthorizationService authorizationService;

    @Autowired
    private MutateLock mutateLock;

    public PathLink createOrUpdatePathLink(PathLink incomingPathLink) {

        return mutateLock.executeInLock(() -> {

            final Set<EntityStructure> entitiesRequiringAuthorization = new HashSet<>();
            final PathLink resultPathLink;
            final boolean updatedExisting;

            if (incomingPathLink.getNetexId() != null) {
                // Update?

                resultPathLink = updatePathLink(incomingPathLink);
                updatedExisting = true;

            } else {
                logger.info("New incoming path link {}", incomingPathLink);
                resultPathLink = incomingPathLink;
                updatedExisting = false;
            }

            if (resultPathLink.getFrom() == null || resultPathLink.getTo() == null) {
                throw new IllegalArgumentException("PathLink (id: " + resultPathLink.getNetexId() + ") must have PathLinkEnd From and To set.");
            }

            if (resultPathLink.getFrom() != null) {
                EntityInVersionStructure from = verifyPathLinkReferences(resultPathLink.getFrom());
                entitiesRequiringAuthorization.add(from);
            }
            if (resultPathLink.getTo() != null) {
                EntityInVersionStructure to = verifyPathLinkReferences(resultPathLink.getTo());
                entitiesRequiringAuthorization.add(to);
            }

            authorizationService.assertAuthorized(AuthorizationConstants.ROLE_EDIT_STOPS, entitiesRequiringAuthorization);
            pathLinkRepository.save(resultPathLink);

            logger.info("{} {}", updatedExisting ? "Updated" : "Created", resultPathLink);

            return resultPathLink;
        });
    }

    private PathLink updatePathLink(PathLink incomingPathLink) {
        logger.debug("Looking for PathLink with ID: {} and version: {}", incomingPathLink.getNetexId(), incomingPathLink.getVersion());

        PathLink existingPathLink = pathLinkRepository.findFirstByNetexIdOrderByVersionDesc(incomingPathLink.getNetexId());

        if (existingPathLink == null) {
            throw new NoSuchElementException("Specified path link with ID: " + incomingPathLink.getNetexId() + " does not exist");
        }

        if (incomingPathLink.getVersion() > 0 && existingPathLink.getVersion() != incomingPathLink.getVersion()) {
            throw new GraphQLException("Incoming PathLink version differs from existing path link with id " + incomingPathLink.getNetexId() + ", version: " + existingPathLink.getVersion() + " != " + incomingPathLink.getVersion());
        }

        logger.debug("Found existing path link: {}", existingPathLink);

        boolean changed = false;
        if (incomingPathLink.getLineString() != null) {
            existingPathLink.setLineString(incomingPathLink.getLineString());
            changed = true;
        }
        if (incomingPathLink.getAllowedUse() != null) {
            existingPathLink.setAllowedUse(incomingPathLink.getAllowedUse());
            changed = true;
        }
        if (incomingPathLink.getTransferDuration() != null) {
            TransferDuration incomingTransferDuration = incomingPathLink.getTransferDuration();

            if (existingPathLink.getTransferDuration() != null) {
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

            if (changed) {
                logger.debug("Existing pathLink changed");
                existingPathLink.setChanged(Instant.now());
            }
        }

        return existingPathLink;
    }

    private EntityInVersionStructure verifyPathLinkReferences(PathLinkEnd pathLinkEnd) {

        if (pathLinkEnd.getPlaceRef() != null) {
            EntityInVersionStructure entityInVersionStructure = referenceResolver.resolve(pathLinkEnd.getPlaceRef());

            if (entityInVersionStructure == null) {
                throw new NoSuchElementException("Cannot find path link end reference to place " + pathLinkEnd.getPlaceRef());
            }
            return entityInVersionStructure;
        }
        throw new NoSuchElementException("Cannot find path link end reference (quay/stop/..) for path link end ref" + pathLinkEnd.getPlaceRef());
    }


}

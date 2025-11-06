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

package org.rutebanken.tiamat.versioning.save;

import com.google.api.client.util.Preconditions;
import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.PurposeOfGrouping;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.GroupOfStopPlacesRepository;
import org.rutebanken.tiamat.repository.PurposeOfGroupingRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.service.groupofstopplaces.GroupOfStopPlacesCentroidComputer;
import org.rutebanken.tiamat.service.metrics.PrometheusMetricsService;
import org.rutebanken.tiamat.versioning.VersionIncrementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * No history for group of stop places.
 * Version is incremented and changed date is updated, but the history will not be kept.
 * Valid between must not be populated
 */
@Transactional
@Service
public class GroupOfStopPlacesSaverService {

    private static final Logger logger = LoggerFactory.getLogger(GroupOfStopPlacesSaverService.class);

    @Autowired
    private GroupOfStopPlacesRepository groupOfStopPlacesRepository;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private GroupOfStopPlacesCentroidComputer groupOfStopPlacesCentroidComputer;

    @Autowired
    private UsernameFetcher usernameFetcher;

    @Autowired
    private VersionIncrementor versionIncrementor;

    @Autowired
    private PrometheusMetricsService prometheusMetricsService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private PurposeOfGroupingRepository purposeOfGroupingRepository;

    public GroupOfStopPlaces saveNewVersion(GroupOfStopPlaces newVersion) {

        GroupOfStopPlaces existingGOSP = groupOfStopPlacesRepository.findFirstByNetexIdOrderByVersionDesc(newVersion.getNetexId());
        boolean isNewGOSP = (existingGOSP == null);

        // Validate members and authorization based on whether this is a new gosp or update existing gosp
        validateMembersWithAuthorization(newVersion, existingGOSP, isNewGOSP);

        PurposeOfGrouping purposeOfGrouping = resolvePurposeOfGrouping(newVersion);
        String usernameForAuthenticatedUser = usernameFetcher.getUserNameForAuthenticatedUser();

        GroupOfStopPlaces groupOfStopPlaces = isNewGOSP ?
            prepareNewGOSP(newVersion) :
            updateExistingGOSP(newVersion, existingGOSP, purposeOfGrouping);

        finalizeGOSP(groupOfStopPlaces, usernameForAuthenticatedUser);
        groupOfStopPlaces = groupOfStopPlacesRepository.save(groupOfStopPlaces);

        prometheusMetricsService.registerEntitySaved(newVersion.getClass(), 1L);
        logger.info("Saved {}", groupOfStopPlaces);

        return groupOfStopPlaces;
    }

    /**
     * Validates members and verifies authorization.
     * For new groups, requires at least one member to ensure authorization is checked.
     * For updates where all members are removed, verifies authorization on existing members.
     *
     * @param newVersion The group being saved
     * @param existing The existing group (null if creating new)
     * @param isNewGOSP Whether this is a new group or update
     * @throws IllegalArgumentException if validation fails
     * @throws org.springframework.security.access.AccessDeniedException if authorization fails
     */
    private void validateMembersWithAuthorization(GroupOfStopPlaces newVersion,
                                                   GroupOfStopPlaces existing,
                                                   boolean isNewGOSP) {
        boolean hasMembers = newVersion.getMembers() != null && !newVersion.getMembers().isEmpty();

        if (isNewGOSP) {
            // New groups must have at least one member to ensure authorization check
            if (!hasMembers) {
                throw new IllegalArgumentException(
                    "Cannot create a new GroupOfStopPlaces without members. " +
                    "At least one member is required to verify authorization.");
            }
            validateAndAuthorizeMembers(newVersion.getMembers());
        } else {
            // Updating existing group
            if (hasMembers) {
                // Normal update with members - validate and authorize new members
                validateAndAuthorizeMembers(newVersion.getMembers());
            } else {
                // Removing all members - authorize based on existing members
                logger.info("Removing all members from GroupOfStopPlaces {}", existing.getNetexId());
                validateAuthorizationForMemberRemoval(existing);
            }
        }
    }

    /**
     * Validates that each member exists, is valid, and user has authorization to edit it.
     */
    private void validateAndAuthorizeMembers(Collection<? extends org.rutebanken.tiamat.model.SiteRefStructure> members) {
        members.forEach(member -> {
            String memberRef = member.getRef();
            StopPlace resolvedMember = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(memberRef);

            Preconditions.checkArgument(resolvedMember != null,
                    "Member with reference " + memberRef + " does not exist");
            Preconditions.checkArgument(resolvedMember.getParentSiteRef() == null,
                    "Member with reference " + memberRef + " already has a parent site ref. Use parent ref instead.");

            authorizationService.verifyCanEditEntities(List.of(resolvedMember));
        });
    }

    /**
     * When removing all members from an existing group, verify user has permission
     * by checking authorization on the current members being removed.
     */
    private void validateAuthorizationForMemberRemoval(GroupOfStopPlaces existingGroup) {
        if (existingGroup.getMembers() == null || existingGroup.getMembers().isEmpty()) {
            // Group already has no members, allow the update
            logger.debug("Group {} already has no members", existingGroup.getNetexId());
            return;
        }

        logger.info("Validating authorization for removing {} members from GroupOfStopPlaces {}",
                   existingGroup.getMembers().size(), existingGroup.getNetexId());

        existingGroup.getMembers().forEach(member -> {
            StopPlace resolvedMember = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(member.getRef());
            if (resolvedMember != null) {
                authorizationService.verifyCanEditEntities(List.of(resolvedMember));
            }
        });
    }

    private PurposeOfGrouping resolvePurposeOfGrouping(GroupOfStopPlaces newVersion) {
        if (newVersion.getPurposeOfGrouping() == null) {
            return null;
        }

        Preconditions.checkArgument(newVersion.getPurposeOfGrouping().getNetexId() != null,
                "Purpose of grouping must have a netex id when saving group of stop places " + newVersion);

        return purposeOfGroupingRepository.findFirstByNetexIdOrderByVersionDesc(
                newVersion.getPurposeOfGrouping().getNetexId());
    }

    private GroupOfStopPlaces prepareNewGOSP(GroupOfStopPlaces newVersion) {
        newVersion.setCreated(Instant.now());
        return newVersion;
    }

    private GroupOfStopPlaces updateExistingGOSP(GroupOfStopPlaces newVersion,
                                                 GroupOfStopPlaces existing,
                                                 PurposeOfGrouping purposeOfGrouping) {
        BeanUtils.copyProperties(newVersion, existing, "id", "created", "version");
        existing.getMembers().clear();
        if (newVersion.getMembers() != null) {
            existing.getMembers().addAll(newVersion.getMembers());
        }
        existing.setChanged(Instant.now());
        if (purposeOfGrouping != null) {
            existing.setPurposeOfGrouping(purposeOfGrouping);
        }
        return existing;
    }

    private void finalizeGOSP(GroupOfStopPlaces groupOfStopPlaces, String username) {
        groupOfStopPlaces.setValidBetween(null);
        groupOfStopPlaces.setChangedBy(username);

        Optional<Point> centroid = groupOfStopPlacesCentroidComputer.compute(groupOfStopPlaces);
        if (centroid.isPresent()) {
            logger.info("Setting centroid for group of stop place {} to {}", groupOfStopPlaces.getNetexId(), centroid.get());
            groupOfStopPlaces.setCentroid(centroid.get());
        }

        versionIncrementor.incrementVersion(groupOfStopPlaces);
    }


}

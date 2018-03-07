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

package org.rutebanken.tiamat.versioning;

import com.google.api.client.util.Preconditions;
import com.vividsolutions.jts.geom.Point;
import org.rutebanken.helper.organisation.AuthorizationConstants;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.repository.EntityInVersionRepository;
import org.rutebanken.tiamat.repository.GroupOfStopPlacesRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.service.groupofstopplaces.GroupOfStopPlacesCentroidComputer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

/**
 * No history for group of stop places.
 * Version is incremented and changed date is updated, but the history will not be kept.
 * Valid between must not be polulated
 */
@Transactional
@Service
public class GroupOfStopPlacesSaverService extends VersionedSaverService<GroupOfStopPlaces> {

    private static final Logger logger = LoggerFactory.getLogger(GroupOfStopPlacesSaverService.class);

    @Autowired
    private GroupOfStopPlacesRepository groupOfStopPlacesRepository;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private GroupOfStopPlacesCentroidComputer groupOfStopPlacesCentroidComputer;


    @Override
    public GroupOfStopPlaces saveNewVersion(GroupOfStopPlaces existingVersion, GroupOfStopPlaces newVersion) {
        return saveNewVersion(newVersion);
    }

    @Override
    public GroupOfStopPlaces saveNewVersion(GroupOfStopPlaces newVersion) {

        validateMembers(newVersion);

        GroupOfStopPlaces existing = groupOfStopPlacesRepository.findFirstByNetexIdOrderByVersionDesc(newVersion.getNetexId());
        String usernameForAuthenticatedUser = usernameFetcher.getUserNameForAuthenticatedUser();

        GroupOfStopPlaces result;
        if(existing != null) {
            BeanUtils.copyProperties(newVersion, existing, "id", "created", "version");
            existing.getMembers().clear();
            existing.getMembers().addAll(newVersion.getMembers());
            existing.setChanged(Instant.now());
            result = existing;

        } else {
            newVersion.setCreated(Instant.now());
            result = newVersion;
        }
        result.setValidBetween(null);
        result.setChangedBy(usernameForAuthenticatedUser);
        Optional<Point> point = groupOfStopPlacesCentroidComputer.compute(result);
        if(point.isPresent()) {
            logger.info("Setting centroid for group of stop place {} to {}", result.getNetexId(), point.get());
            result.setCentroid(point.get());
        }

        versionIncrementor.incrementVersion(result);
        result = groupOfStopPlacesRepository.save(result);

        metricsService.registerEntitySaved(newVersion.getClass());
        logger.info("Saved {}", result);

        return result;
    }

    private void validateMembers(GroupOfStopPlaces groupOfStopPlaces) {
        groupOfStopPlaces.getMembers().forEach(member -> {
            StopPlace resolvedMember = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(member.getRef());
            Preconditions.checkArgument(resolvedMember != null,
                    "Member with reference " + member.getRef() + " does not exist when saving group of stop places " + groupOfStopPlaces);
            Preconditions.checkArgument(resolvedMember.getParentSiteRef() == null,
                    "Member with reference " + member.getRef() + " Has a parent site ref. Use parent ref instead. " + groupOfStopPlaces);

            authorizationService.assertAuthorized(AuthorizationConstants.ROLE_EDIT_STOPS, Arrays.asList(resolvedMember));
        });
    }

    @Override
    public EntityInVersionRepository<GroupOfStopPlaces> getRepository() {
        return groupOfStopPlacesRepository;
    }


}

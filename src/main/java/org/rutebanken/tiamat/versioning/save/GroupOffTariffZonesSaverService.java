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


import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.model.GroupOfTariffZones;
import org.rutebanken.tiamat.repository.GroupOfTariffZonesRepository;
import org.rutebanken.tiamat.versioning.validate.VersionValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupOffTariffZonesSaverService {

    private static final Logger logger = LoggerFactory.getLogger(GroupOffTariffZonesSaverService.class);

    private final GroupOfTariffZonesRepository groupOfTariffZonesRepository;
    private final DefaultVersionedSaverService defaultVersionedSaverService;
    private final VersionValidator versionValidator;
    private final UsernameFetcher usernameFetcher;
    private final AuthorizationService authorizationService;

    @Autowired
    public GroupOffTariffZonesSaverService(GroupOfTariffZonesRepository groupOfTariffZonesRepository,
                                           DefaultVersionedSaverService defaultVersionedSaverService,
                                           VersionValidator versionValidator,
                                           UsernameFetcher usernameFetcher,
                                           AuthorizationService authorizationService) {
        this.groupOfTariffZonesRepository = groupOfTariffZonesRepository;
        this.defaultVersionedSaverService = defaultVersionedSaverService;
        this.versionValidator = versionValidator;
        this.usernameFetcher = usernameFetcher;
        this.authorizationService = authorizationService;
    }

    public GroupOfTariffZones saveNewVersion(GroupOfTariffZones newVersion) {
        GroupOfTariffZones existingGroupOfTariffZone;
        if (newVersion.getNetexId() != null) {
            existingGroupOfTariffZone = groupOfTariffZonesRepository.findFirstByNetexIdOrderByVersionDesc(newVersion.getNetexId());
        } else {
            existingGroupOfTariffZone = null;
        }
        return defaultVersionedSaverService.saveNewVersion(existingGroupOfTariffZone, newVersion, groupOfTariffZonesRepository);
    }

    public GroupOfTariffZones saveNewVersion(GroupOfTariffZones existingVersion, GroupOfTariffZones newVersion) {
        versionValidator.validate(existingVersion, newVersion);
        return defaultVersionedSaverService.saveNewVersion(existingVersion, newVersion, groupOfTariffZonesRepository);
    }

    /**
     * Save GroupOfTariffZones with external versioning - versions are managed externally.
     * Updates the existing GroupOfTariffZones by netexId (regardless of version) in place, or creates a
     * new one if not found. Only a single version per netexId is kept, and the version number is taken
     * from the incoming data. Used when Tiamat acts as a replica of a master register.
     *
     * @param incomingGroupOfTariffZones the group to save/update
     * @return the saved group
     */
    public GroupOfTariffZones saveWithExternalVersioning(GroupOfTariffZones incomingGroupOfTariffZones) {
        GroupOfTariffZones existingGroupOfTariffZones = null;

        if (incomingGroupOfTariffZones.getNetexId() != null) {
            existingGroupOfTariffZones = groupOfTariffZonesRepository.findFirstByNetexIdOrderByVersionDesc(incomingGroupOfTariffZones.getNetexId());
        }

        authorizationService.verifyCanEditEntities(Arrays.asList(existingGroupOfTariffZones, incomingGroupOfTariffZones));

        String username = usernameFetcher.getUserNameForAuthenticatedUser();
        Instant now = Instant.now();

        GroupOfTariffZones groupToSave;

        if (existingGroupOfTariffZones != null) {
            logger.info("Updating existing GroupOfTariffZones {} from version {} to version {} with external versioning",
                    incomingGroupOfTariffZones.getNetexId(), existingGroupOfTariffZones.getVersion(), incomingGroupOfTariffZones.getVersion());

            copyFields(incomingGroupOfTariffZones, existingGroupOfTariffZones);
            existingGroupOfTariffZones.setChanged(now);
            existingGroupOfTariffZones.setChangedBy(username);
            groupToSave = existingGroupOfTariffZones;
        } else {
            logger.info("Creating new GroupOfTariffZones {} version {} with external versioning",
                    incomingGroupOfTariffZones.getNetexId(), incomingGroupOfTariffZones.getVersion());

            incomingGroupOfTariffZones.setCreated(now);
            incomingGroupOfTariffZones.setChangedBy(username);
            groupToSave = incomingGroupOfTariffZones;
        }

        GroupOfTariffZones saved = groupOfTariffZonesRepository.save(groupToSave);

        logger.info("Saved GroupOfTariffZones {} version {} with external versioning by user {}",
                saved.getNetexId(), saved.getVersion(), username);

        return saved;
    }

    /**
     * Copy all relevant fields from source to target GroupOfTariffZones, preserving the target's database ID.
     */
    private void copyFields(GroupOfTariffZones source, GroupOfTariffZones target) {
        target.setNetexId(source.getNetexId());
        target.setVersion(source.getVersion());
        target.setName(source.getName());
        target.setValidBetween(source.getValidBetween());

        target.getMembers().clear();
        target.getMembers().addAll(source.getMembers());
    }

    /**
     * Delete all GroupOfTariffZones NOT in the provided set of netexIds.
     * Used for cleanup after an external versioning import to remove orphaned groups.
     * Respects user permissions and logs all deleted netexIds.
     *
     * @param netexIdsToKeep set of netexIds to preserve
     * @return number of groups deleted
     */
    public int deleteAllExcept(Set<String> netexIdsToKeep) {
        List<GroupOfTariffZones> allGroups = groupOfTariffZonesRepository.findAll();

        List<GroupOfTariffZones> toDelete = allGroups.stream()
                .filter(group -> !netexIdsToKeep.contains(group.getNetexId()))
                .toList();

        if (toDelete.isEmpty()) {
            logger.info("No orphaned GroupOfTariffZones to delete");
            return 0;
        }

        authorizationService.verifyCanEditEntities(toDelete);

        String deletedIds = toDelete.stream()
                .map(GroupOfTariffZones::getNetexId)
                .collect(Collectors.joining(", "));

        logger.info("Deleting {} orphaned GroupOfTariffZones: {}", toDelete.size(), deletedIds);

        groupOfTariffZonesRepository.deleteAll(toDelete);

        return toDelete.size();
    }

}

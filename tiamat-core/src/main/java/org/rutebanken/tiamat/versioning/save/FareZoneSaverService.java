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
import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.repository.FareZoneRepository;
import org.rutebanken.tiamat.service.TariffZonesLookupService;
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
public class FareZoneSaverService {

    private static final Logger logger = LoggerFactory.getLogger(FareZoneSaverService.class);

    private final FareZoneRepository fareZoneRepository;
    private final TariffZonesLookupService tariffZonesLookupService;
    private final DefaultVersionedSaverService defaultVersionedSaverService;
    private final VersionValidator versionValidator;
    private final UsernameFetcher usernameFetcher;
    private final AuthorizationService authorizationService;

    @Autowired
    public FareZoneSaverService(FareZoneRepository fareZoneRepository,
                                TariffZonesLookupService tariffZonesLookupService,
                                DefaultVersionedSaverService defaultVersionedSaverService,
                                VersionValidator versionValidator,
                                UsernameFetcher usernameFetcher,
                                AuthorizationService authorizationService) {
        this.fareZoneRepository = fareZoneRepository;
        this.tariffZonesLookupService = tariffZonesLookupService;
        this.defaultVersionedSaverService = defaultVersionedSaverService;
        this.versionValidator = versionValidator;
        this.usernameFetcher = usernameFetcher;
        this.authorizationService = authorizationService;
    }

    public FareZone saveNewVersion(FareZone newVersion) {
        FareZone existingFareZone;
        if (newVersion.getNetexId() != null) {
            existingFareZone = fareZoneRepository.findFirstByNetexIdOrderByVersionDesc(newVersion.getNetexId());
        } else {
            existingFareZone = null;
        }
        FareZone  saved = defaultVersionedSaverService.saveNewVersion(existingFareZone, newVersion, fareZoneRepository);
        tariffZonesLookupService.resetFareZone();
        return saved;
    }

    public FareZone saveNewVersion(FareZone existingVersion, FareZone newVersion) {
        versionValidator.validate(existingVersion, newVersion);
        FareZone  saved = defaultVersionedSaverService.saveNewVersion(existingVersion, newVersion, fareZoneRepository);
        tariffZonesLookupService.resetFareZone();
        return saved;
    }

    /**
     * Save FareZone with external versioning - versions are managed externally.
     * Updates existing FareZone by netexId (regardless of version), or creates new if not found.
     * Used when Tiamat acts as a replica of a master FareZone register.
     *
     * @param incomingFareZone The fare zone to save/update
     * @return The saved fare zone, or null if validation fails
     */
    public FareZone saveWithExternalVersioning(FareZone incomingFareZone) {
        FareZone existingFareZone = null;

        if (incomingFareZone.getNetexId() != null) {
            existingFareZone = fareZoneRepository.findFirstByNetexIdOrderByVersionDesc(incomingFareZone.getNetexId());
        }

        authorizationService.verifyCanEditEntities(Arrays.asList(existingFareZone, incomingFareZone));

        // Validate ValidBetween constraints
        if (!validateValidBetween(incomingFareZone)) {
            logger.warn("Ignoring FareZone {} version {} - invalid ValidBetween: fromDate is after toDate",
                    incomingFareZone.getNetexId(), incomingFareZone.getVersion());
            return null;
        }

        String username = usernameFetcher.getUserNameForAuthenticatedUser();
        Instant now = Instant.now();

        FareZone fareZoneToSave;

        if (existingFareZone != null) {
            logger.info("Updating existing FareZone {} from version {} to version {} with external versioning",
                    incomingFareZone.getNetexId(), existingFareZone.getVersion(), incomingFareZone.getVersion());

            copyFareZoneFields(incomingFareZone, existingFareZone);
            existingFareZone.setChanged(now);
            existingFareZone.setChangedBy(username);
            fareZoneToSave = existingFareZone;
        } else {
            logger.info("Creating new FareZone {} version {} with external versioning",
                    incomingFareZone.getNetexId(), incomingFareZone.getVersion());

            incomingFareZone.setCreated(now);
            incomingFareZone.setChangedBy(username);
            fareZoneToSave = incomingFareZone;
        }

        FareZone saved = fareZoneRepository.save(fareZoneToSave);
        tariffZonesLookupService.resetFareZone();

        logger.info("Saved FareZone {} version {} with external versioning by user {}",
                saved.getNetexId(), saved.getVersion(), username);

        return saved;
    }

    /**
     * Copy all relevant fields from source to target FareZone.
     * Preserves the target's database ID.
     */
    private void copyFareZoneFields(FareZone source, FareZone target) {
        target.setNetexId(source.getNetexId());
        target.setVersion(source.getVersion());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setPrivateCode(source.getPrivateCode());
        target.setPolygon(source.getPolygon());
        target.setValidBetween(source.getValidBetween());
        target.setScopingMethod(source.getScopingMethod());
        target.setZoneTopology(source.getZoneTopology());
        target.setTransportOrganisationRef(source.getTransportOrganisationRef());

        if (source.getNeighbours() != null) {
            target.getNeighbours().clear();
            target.getNeighbours().addAll(source.getNeighbours());
        }

        if (source.getFareZoneMembers() != null) {
            target.getFareZoneMembers().clear();
            target.getFareZoneMembers().addAll(source.getFareZoneMembers());
        }
    }

    /**
     * Validate ValidBetween constraints for a FareZone.
     * If both fromDate and toDate are present, fromDate must not be after toDate.
     *
     * @param fareZone The fare zone to validate
     * @return true if validation passes, false if validation fails
     */
    private boolean validateValidBetween(FareZone fareZone) {
        if (fareZone.getValidBetween() == null) {
            return true; // No ValidBetween is acceptable
        }

        Instant fromDate = fareZone.getValidBetween().getFromDate();
        Instant toDate = fareZone.getValidBetween().getToDate();

        // If both dates are present, fromDate must not be after toDate
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            logger.warn("FareZone {} has invalid ValidBetween: fromDate {} is after toDate {}",
                    fareZone.getNetexId(), fromDate, toDate);
            return false;
        }

        return true;
    }

    /**
     * Delete all FareZones NOT in the provided set of netexIds.
     * Used for cleanup after external versioning import to remove orphaned FareZones.
     * Respects user permissions and logs all deleted netexIds.
     *
     * @param netexIdsToKeep Set of netexIds to preserve
     * @return Number of FareZones deleted
     */
    public int deleteAllExcept(Set<String> netexIdsToKeep) {
        List<FareZone> allFareZones = fareZoneRepository.findAll();

        List<FareZone> toDelete = allFareZones.stream()
                .filter(fz -> !netexIdsToKeep.contains(fz.getNetexId()))
                .toList();

        if (toDelete.isEmpty()) {
            logger.info("No orphaned FareZones to delete");
            return 0;
        }

        authorizationService.verifyCanEditEntities(toDelete);

        String deletedIds = toDelete.stream()
                .map(FareZone::getNetexId)
                .collect(Collectors.joining(", "));

        logger.info("Deleting {} orphaned FareZones: {}", toDelete.size(), deletedIds);

        fareZoneRepository.deleteAll(toDelete);
        tariffZonesLookupService.resetFareZone();

        return toDelete.size();
    }

}

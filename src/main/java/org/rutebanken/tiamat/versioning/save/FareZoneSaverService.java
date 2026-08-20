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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
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

    /**
     * Minimum fraction of currently stored FareZones that must survive a full-replace prune.
     * Guards against a truncated or partially generated feed wiping the register. A value of
     * 0.5 means the incoming delivery must retain at least half of the existing zones.
     */
    private final double pruneMinRetainRatio;

    @Autowired
    public FareZoneSaverService(FareZoneRepository fareZoneRepository,
                                TariffZonesLookupService tariffZonesLookupService,
                                DefaultVersionedSaverService defaultVersionedSaverService,
                                VersionValidator versionValidator,
                                UsernameFetcher usernameFetcher,
                                AuthorizationService authorizationService,
                                @Value("${fareZone.prune.minRetainRatio:0.5}") double pruneMinRetainRatio) {
        this.fareZoneRepository = fareZoneRepository;
        this.tariffZonesLookupService = tariffZonesLookupService;
        this.defaultVersionedSaverService = defaultVersionedSaverService;
        this.versionValidator = versionValidator;
        this.usernameFetcher = usernameFetcher;
        this.authorizationService = authorizationService;
        this.pruneMinRetainRatio = validateRetainRatio(pruneMinRetainRatio);
    }

    /**
     * Fail startup on a ratio that cannot express a retain floor. A non-finite or negative value
     * makes the comparison permanently false, silently disabling the guard; above 1 rejects every
     * prune. Both are worse than a wrong-but-usable number, because neither is visible in operation.
     */
    private static double validateRetainRatio(double pruneMinRetainRatio) {
        if (!Double.isFinite(pruneMinRetainRatio) || pruneMinRetainRatio < 0 || pruneMinRetainRatio > 1) {
            throw new IllegalArgumentException(
                    "fareZone.prune.minRetainRatio must be a finite fraction between 0 and 1, but was "
                            + pruneMinRetainRatio);
        }
        return pruneMinRetainRatio;
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
     * @return The saved fare zone
     * @throws IllegalArgumentException if the incoming fare zone is not valid
     */
    public FareZone saveWithExternalVersioning(FareZone incomingFareZone) {
        // Before anything is touched, so a rejected zone leaves no trace.
        validateForExternalVersioning(incomingFareZone);

        FareZone existingFareZone = null;

        if (incomingFareZone.getNetexId() != null) {
            existingFareZone = findExistingAndDropSupersededVersions(incomingFareZone.getNetexId());
        }

        authorizationService.verifyCanEditEntities(Arrays.asList(existingFareZone, incomingFareZone));

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
     * Return the row to update in place under external versioning, deleting any other rows for the
     * same netexId first.
     *
     * <p>External versioning keeps a single row per netexId, holding whatever version the master
     * sent. Rows left behind by earlier default-versioned saves break that: reads resolve a FareZone
     * by {@code MAX(version)}, so once the surviving row is set to a lower master version an older
     * sibling becomes the answer. There is also no unique constraint on (netex_id, version), so a
     * sibling that happens to carry the incoming version would silently duplicate it.
     *
     * <p>This makes the first external-versioning save for a netexId the cutover point from
     * versioned history to a single mastered row.
     */
    private FareZone findExistingAndDropSupersededVersions(String netexId) {
        List<FareZone> existingVersions = fareZoneRepository.findByNetexId(netexId);
        if (existingVersions.isEmpty()) {
            return null;
        }

        FareZone latest = existingVersions.stream()
                .max(Comparator.comparingLong(FareZone::getVersion))
                .orElseThrow();

        List<FareZone> superseded = existingVersions.stream()
                .filter(fareZone -> !fareZone.getId().equals(latest.getId()))
                .toList();

        if (!superseded.isEmpty()) {
            String supersededVersions = superseded.stream()
                    .map(fareZone -> String.valueOf(fareZone.getVersion()))
                    .collect(Collectors.joining(", "));
            logger.info("Dropping {} superseded version(s) of FareZone {} on external versioning: {}. Keeping version {}.",
                    superseded.size(), netexId, supersededVersions, latest.getVersion());
            fareZoneRepository.deleteAll(superseded);
            // Flush before the surviving row is renumbered, so the deletes cannot be ordered after
            // an update that lands on a version one of them still holds.
            fareZoneRepository.flush();
        }

        return latest;
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
        target.setMultiSurface(source.getMultiSurface());
        target.setValidBetween(source.getValidBetween());
        target.setScopingMethod(source.getScopingMethod());
        target.setZoneTopology(source.getZoneTopology());
        target.setTransportOrganisationRef(source.getTransportOrganisationRef());

        // keyValues carries the tzMapping bridge to the legacy TariffZone ids. Without this
        // the DB keeps stale mappings on every update after create.
        target.getKeyValues().clear();
        target.getKeyValues().putAll(source.getKeyValues());

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
     * Validate an incoming FareZone against the constraints external versioning requires: if both
     * fromDate and toDate are present, fromDate must not be after toDate.
     *
     * <p>Rejects the whole save rather than skipping the zone. Under external versioning the import
     * is an authoritative replacement, and a skipped zone is absent from the set of ids to keep - so
     * silently ignoring it deletes the copy already stored, on the strength of the very record that
     * failed validation.
     *
     * @param fareZone The fare zone to validate
     * @throws IllegalArgumentException if the fare zone is not valid
     */
    public void validateForExternalVersioning(FareZone fareZone) {
        if (fareZone.getValidBetween() == null) {
            return; // No ValidBetween is acceptable
        }

        Instant fromDate = fareZone.getValidBetween().getFromDate();
        Instant toDate = fareZone.getValidBetween().getToDate();

        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException(String.format(
                    "FareZone %s version %d has invalid ValidBetween: fromDate %s is after toDate %s",
                    fareZone.getNetexId(), fareZone.getVersion(), fromDate, toDate));
        }
    }

    /**
     * The number of distinct FareZone netexIds currently stored. Callers capture this before an
     * authoritative import so {@link #deleteAllExcept(Set, long)} can measure its retain floor
     * against the register as it was, not as the import has already left it.
     */
    public long countDistinctStoredNetexIds() {
        return fareZoneRepository.countDistinctNetexIds();
    }

    /**
     * Delete all FareZones NOT in the provided set of netexIds.
     * Used for cleanup after external versioning import to remove orphaned FareZones.
     * Respects user permissions and logs all deleted netexIds.
     *
     * <p>The retain floor is measured against {@code existingDistinctCount}, taken before the import
     * wrote anything. Counting the stored zones here instead would count the incoming ones too - they
     * have already been saved by the time the prune runs - which inflates the denominator by exactly
     * the zones the feed brought. That makes the floor unreachable in the case it exists for: a feed
     * that replaces every id retains nothing of the old register, yet lands on the threshold and
     * passes.
     *
     * @param netexIdsToKeep       Set of netexIds to preserve
     * @param existingDistinctCount distinct netexIds stored before this import began, from
     *                              {@link #countDistinctStoredNetexIds()}
     * @return Number of FareZones deleted
     */
    public int deleteAllExcept(Set<String> netexIdsToKeep, long existingDistinctCount) {
        List<FareZone> allFareZones = fareZoneRepository.findAll();

        List<FareZone> toDelete = allFareZones.stream()
                .filter(fz -> !netexIdsToKeep.contains(fz.getNetexId()))
                .toList();

        if (toDelete.isEmpty()) {
            logger.info("No orphaned FareZones to delete");
            return 0;
        }

        // Every id being deleted pre-dates the import, so the retained count follows from the baseline.
        long deletingDistinct = toDelete.stream().map(FareZone::getNetexId).distinct().count();
        long retainedDistinct = existingDistinctCount - deletingDistinct;

        if (existingDistinctCount > 0 && retainedDistinct < existingDistinctCount * pruneMinRetainRatio) {
            throw new IllegalStateException(String.format(
                    "Refusing FareZone prune: would keep %d of the %d zones stored before this import (%.1f%%), "
                            + "below the safety floor of %.1f%%. This usually indicates a truncated or partial feed. "
                            + "Aborting to avoid mass deletion.",
                    retainedDistinct, existingDistinctCount,
                    100.0 * retainedDistinct / existingDistinctCount,
                    100.0 * pruneMinRetainRatio));
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

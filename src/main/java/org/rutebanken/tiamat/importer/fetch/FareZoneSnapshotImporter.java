package org.rutebanken.tiamat.importer.fetch;

import org.rutebanken.netex.model.Common_VersionFrameStructure;
import org.rutebanken.netex.model.CompositeFrame;
import org.rutebanken.netex.model.Composite_VersionFrameStructure;
import org.rutebanken.netex.model.FareFrame;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.model.FareZonePollerState;
import org.rutebanken.tiamat.model.GroupOfTariffZones;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.FareZonePollerStateRepository;
import org.rutebanken.tiamat.versioning.save.FareZoneSaverService;
import org.rutebanken.tiamat.versioning.save.GroupOffTariffZonesSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.xml.bind.JAXBElement;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Applies an authoritative FareZone snapshot pulled by {@link FareZonePoller}, replacing the stored
 * FareZones and GroupOfTariffZones with the contents of the delivery.
 *
 * <p>Deliberately not the general {@code PublicationDeliveryImporter}. The feed is external, and the
 * poller runs it with system privileges, so the general importer would let anything the feed happens
 * to contain - stop places, parkings, path links, topographic places - be written to the register.
 * This importer only ever touches the FareZone and GroupOfTariffZones repositories, and rejects a
 * delivery carrying anything else rather than quietly ignoring it, so drift in the feed is surfaced
 * instead of silently changing scope.
 *
 * <p>The snapshot is validated in full before anything is written, and the whole replacement -
 * upserts, prunes, and the record of what was imported - is one transaction. A snapshot is applied
 * completely or not at all: a partial application would leave the register describing a state that
 * no version of the feed ever had, and would record the feed as successfully imported.
 *
 * <p>Expected delivery shape: FareZones in a FareFrame, and the GroupOfTariffZones referencing them
 * in an accompanying SiteFrame. Anything else in the FareFrame (fare products, tariffs) is inert -
 * nothing here reads it.
 */
@Service
public class FareZoneSnapshotImporter {

    private static final Logger logger = LoggerFactory.getLogger(FareZoneSnapshotImporter.class);

    private final NetexMapper netexMapper;
    private final FareZoneSaverService fareZoneSaverService;
    private final GroupOffTariffZonesSaverService groupOffTariffZonesSaverService;
    private final FareZonePollerStateRepository fareZonePollerStateRepository;

    public FareZoneSnapshotImporter(NetexMapper netexMapper,
                                    FareZoneSaverService fareZoneSaverService,
                                    GroupOffTariffZonesSaverService groupOffTariffZonesSaverService,
                                    FareZonePollerStateRepository fareZonePollerStateRepository) {
        this.netexMapper = netexMapper;
        this.fareZoneSaverService = fareZoneSaverService;
        this.groupOffTariffZonesSaverService = groupOffTariffZonesSaverService;
        this.fareZonePollerStateRepository = fareZonePollerStateRepository;
    }

    public record Result(int importedFareZones, int deletedFareZones, int importedGroups, int deletedGroups) {
    }

    /**
     * The hash of the snapshot last applied, or null if none has been. Read outside the import
     * transaction by the poller, which holds the poller lock while it does so.
     */
    @Transactional(readOnly = true)
    public String lastImportedHash() {
        return fareZonePollerStateRepository.findById(FareZonePollerState.SINGLETON_ID)
                .map(FareZonePollerState::getLastImportedHash)
                .orElse(null);
    }

    /**
     * Replace the stored FareZones and GroupOfTariffZones with the delivery's, and record
     * {@code snapshotHash} as applied.
     *
     * @throws IllegalArgumentException if the delivery is not a usable FareZone snapshot
     * @throws IllegalStateException    if the prune would breach the retain floor
     */
    @Transactional
    public Result replaceSnapshot(PublicationDeliveryStructure delivery, String snapshotHash) {
        FareFrame fareFrame = findFrame(delivery, FareFrame.class)
                .orElseThrow(() -> new IllegalArgumentException("FareZone snapshot contains no FareFrame"));
        SiteFrame siteFrame = findFrame(delivery, SiteFrame.class).orElse(null);

        rejectUnsupportedContent(siteFrame);

        List<FareZone> fareZones = mapFareZones(fareFrame);
        List<GroupOfTariffZones> groups = mapGroups(siteFrame);

        // Everything is checked before the first write, so a rejected snapshot cannot have deleted or
        // renumbered anything on its way to being rejected.
        Set<String> snapshotZoneIds = validate(fareZones, groups);

        // Captured before the saves: the prune safety floor asks how much of the stored register this
        // snapshot retains, which is unanswerable once the snapshot's own zones are in the count.
        long fareZonesBeforeImport = fareZoneSaverService.countDistinctStoredNetexIds();

        fareZones.forEach(fareZoneSaverService::saveWithExternalVersioning);
        int deletedFareZones = fareZoneSaverService.deleteAllExcept(snapshotZoneIds, fareZonesBeforeImport);

        groups.forEach(groupOffTariffZonesSaverService::saveWithExternalVersioning);
        // A snapshot that declares no groups at all is treated as "the feed said nothing about
        // groups", not as "delete every group" - the same reading the REST import path takes.
        int deletedGroups = groups.isEmpty()
                ? 0
                : groupOffTariffZonesSaverService.deleteAllExcept(netexIds(groups));

        recordImported(snapshotHash);

        Result result = new Result(fareZones.size(), deletedFareZones, groups.size(), deletedGroups);
        logger.info("Applied FareZone snapshot: {} FareZones ({} pruned), {} GroupOfTariffZones ({} pruned)",
                result.importedFareZones(), result.deletedFareZones(),
                result.importedGroups(), result.deletedGroups());
        return result;
    }

    private List<FareZone> mapFareZones(FareFrame fareFrame) {
        if (fareFrame.getFareZones() == null || fareFrame.getFareZones().getFareZone() == null) {
            return List.of();
        }
        return fareFrame.getFareZones().getFareZone().stream()
                .map(netexMapper::mapToTiamatModel)
                .collect(Collectors.toList());
    }

    private List<GroupOfTariffZones> mapGroups(SiteFrame siteFrame) {
        if (siteFrame == null
                || siteFrame.getGroupsOfTariffZones() == null
                || siteFrame.getGroupsOfTariffZones().getGroupOfTariffZones() == null) {
            return List.of();
        }
        return siteFrame.getGroupsOfTariffZones().getGroupOfTariffZones().stream()
                .map(netexMapper::mapToTiamatModel)
                .collect(Collectors.toList());
    }

    /**
     * @return the netexIds the snapshot declares, which are the FareZones to keep
     */
    private Set<String> validate(List<FareZone> fareZones, List<GroupOfTariffZones> groups) {
        if (fareZones.isEmpty()) {
            // An empty snapshot would prune every stored zone. Treat it as a broken feed, not as an
            // instruction to empty the register.
            throw new IllegalArgumentException("FareZone snapshot contains no FareZones");
        }

        List<String> missingIds = fareZones.stream()
                .filter(fareZone -> fareZone.getNetexId() == null)
                .map(fareZone -> String.valueOf(fareZone.getName()))
                .toList();
        if (!missingIds.isEmpty()) {
            throw new IllegalArgumentException("FareZone snapshot contains zones without an id: " + missingIds);
        }

        Set<String> zoneIds = netexIds(fareZones);
        if (zoneIds.size() != fareZones.size()) {
            throw new IllegalArgumentException(
                    "FareZone snapshot declares the same FareZone more than once: " + duplicates(fareZones));
        }

        fareZones.forEach(fareZoneSaverService::validateForExternalVersioning);

        // Under authoritative replacement a member not in the snapshot is about to be pruned, so
        // members must resolve within the snapshot - not merely somewhere in the database.
        List<String> unresolvedMembers = groups.stream()
                .flatMap(group -> group.getMembers().stream())
                .map(TariffZoneRef::getRef)
                .filter(ref -> !zoneIds.contains(ref))
                .distinct()
                .toList();
        if (!unresolvedMembers.isEmpty()) {
            throw new IllegalArgumentException(
                    "FareZone snapshot has GroupOfTariffZones members that it does not declare: " + unresolvedMembers);
        }

        return zoneIds;
    }

    /**
     * Fail on anything in the SiteFrame this importer does not apply. Such content would be written
     * to the register by the general importer, so accepting the delivery while ignoring it would
     * quietly widen what the feed is trusted to change.
     */
    private void rejectUnsupportedContent(SiteFrame siteFrame) {
        if (siteFrame == null) {
            return;
        }

        List<String> unsupported = new ArrayList<>();
        if (siteFrame.getStopPlaces() != null && isNotEmpty(siteFrame.getStopPlaces().getStopPlace_())) {
            unsupported.add("stopPlaces");
        }
        if (siteFrame.getParkings() != null && isNotEmpty(siteFrame.getParkings().getParking())) {
            unsupported.add("parkings");
        }
        if (siteFrame.getPathLinks() != null && isNotEmpty(siteFrame.getPathLinks().getPathLink())) {
            unsupported.add("pathLinks");
        }
        if (siteFrame.getTopographicPlaces() != null && isNotEmpty(siteFrame.getTopographicPlaces().getTopographicPlace())) {
            unsupported.add("topographicPlaces");
        }
        if (siteFrame.getTariffZones() != null && isNotEmpty(siteFrame.getTariffZones().getTariffZone())) {
            unsupported.add("tariffZones (FareZones belong in the FareFrame)");
        }

        if (!unsupported.isEmpty()) {
            throw new IllegalArgumentException(
                    "FareZone snapshot carries content this importer does not apply: " + unsupported
                            + ". Only FareZones (FareFrame) and GroupOfTariffZones (SiteFrame) are accepted.");
        }
    }

    private void recordImported(String snapshotHash) {
        FareZonePollerState state = fareZonePollerStateRepository
                .findById(FareZonePollerState.SINGLETON_ID)
                .orElseGet(FareZonePollerState::new);
        state.setLastImportedHash(snapshotHash);
        state.setLastImportedAt(Instant.now());
        fareZonePollerStateRepository.save(state);
    }

    private static boolean isNotEmpty(List<?> elements) {
        return elements != null && !elements.isEmpty();
    }

    private static Set<String> netexIds(List<? extends IdentifiedEntity> entities) {
        return entities.stream()
                .map(IdentifiedEntity::getNetexId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static List<String> duplicates(List<FareZone> fareZones) {
        Set<String> seen = new LinkedHashSet<>();
        return fareZones.stream()
                .map(FareZone::getNetexId)
                .filter(netexId -> !seen.add(netexId))
                .distinct()
                .toList();
    }

    /**
     * Find a frame of the given type, whether declared directly or inside a CompositeFrame. Unlike
     * {@code PublicationDeliveryHelper.findSiteFrame} this does not require the frame to be present.
     */
    private static <T extends Common_VersionFrameStructure> Optional<T> findFrame(
            PublicationDeliveryStructure delivery, Class<T> frameType) {

        if (delivery.getDataObjects() == null) {
            return Optional.empty();
        }
        List<JAXBElement<? extends Common_VersionFrameStructure>> frames =
                delivery.getDataObjects().getCompositeFrameOrCommonFrame();

        return frames.stream()
                .map(JAXBElement::getValue)
                .filter(frameType::isInstance)
                .map(frameType::cast)
                .findFirst()
                .or(() -> frames.stream()
                        .map(JAXBElement::getValue)
                        .filter(CompositeFrame.class::isInstance)
                        .map(CompositeFrame.class::cast)
                        .map(Composite_VersionFrameStructure::getFrames)
                        .filter(Objects::nonNull)
                        .flatMap(nested -> nested.getCommonFrame().stream())
                        .map(JAXBElement::getValue)
                        .filter(frameType::isInstance)
                        .map(frameType::cast)
                        .findFirst());
    }
}

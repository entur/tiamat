package org.rutebanken.tiamat.rest.write;

import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.rutebanken.tiamat.model.PostalAddress;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.rest.write.mapper.CreateStopPlaceMapper;
import org.rutebanken.tiamat.rest.write.mapper.UpdateStopPlaceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service("writeStopPlaceUpdater")
public class StopPlaceUpdater {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceUpdater.class);

    private final CreateStopPlaceMapper createStopPlaceMapper;
    private final UpdateStopPlaceMapper updateStopPlaceMapper;

    public StopPlaceUpdater(CreateStopPlaceMapper createStopPlaceMapper, UpdateStopPlaceMapper updateStopPlaceMapper) {
        this.createStopPlaceMapper = createStopPlaceMapper;
        this.updateStopPlaceMapper = updateStopPlaceMapper;
    }

    public StopPlace update(StopPlace originalStopPlace, StopPlace editedStopPlace) {
        Set<Quay> mergedQuays = mergeQuays(originalStopPlace, editedStopPlace);

        // Detach quays from the edited copy so the mapper does not replace the quay set
        editedStopPlace.setQuays(null);

        var updatePostalAddressVersion = postalAddressNeedsVersionUpdate(originalStopPlace, editedStopPlace);
        updateAdjacentSites(originalStopPlace, editedStopPlace);
        updateTariffZones(originalStopPlace, editedStopPlace);
        updateKeyValues(originalStopPlace, editedStopPlace);
        updateAccessibilityAssessment(originalStopPlace, editedStopPlace);
        updatePlaceEquipments(originalStopPlace, editedStopPlace);

        // Copy all other fields from editedStopPlace into originalStopPlace
        updateStopPlaceMapper.update(originalStopPlace, editedStopPlace);

        if (updatePostalAddressVersion) {
            logger.info("Postal address for stop place {} updated, incrementing version.", originalStopPlace.getNetexId());
            originalStopPlace.getPostalAddress().setVersion(originalStopPlace.getPostalAddress().getVersion() + 1);
        }

        // Put the correctly merged quays back
        originalStopPlace.setQuays(mergedQuays);

        return originalStopPlace;
    }

    private Set<Quay> mergeQuays(StopPlace originalStopPlace, StopPlace editedStopPlace) {
        Set<Quay> originalQuays = originalStopPlace.getQuays() != null
                ? originalStopPlace.getQuays()
                : new HashSet<>();

        if (editedStopPlace.getQuays() == null || editedStopPlace.getQuays().isEmpty()) {
            return originalQuays;
        }

        for (Quay editedQuay : editedStopPlace.getQuays()) {
            if (editedQuay.getNetexId() != null) {
                // Update an existing quay – find it in the original set
                Quay existingQuay = originalQuays.stream()
                        .filter(q -> editedQuay.getNetexId().equals(q.getNetexId()))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Attempting to update Quay [id = " + editedQuay.getNetexId() +
                                "] on StopPlace [id = " + originalStopPlace.getNetexId() +
                                "], but the quay does not exist on the stop place"));

                updateStopPlaceMapper.update(existingQuay, editedQuay);
                existingQuay.setChanged(Instant.now());
            } else {
                Quay newQuay = createStopPlaceMapper.createCopy(editedQuay, Quay.class);
                originalQuays.add(newQuay);
                logger.info("Added new quay to stop place {}", originalStopPlace.getNetexId());
            }
        }

        return originalQuays;
    }

    private boolean postalAddressNeedsVersionUpdate(StopPlace originalStopPlace, StopPlace editedStopPlace) {
        PostalAddress originalPostalAddress = originalStopPlace.getPostalAddress();
        PostalAddress editedPostalAddress = editedStopPlace.getPostalAddress();
        if (originalPostalAddress == null || editedPostalAddress == null) {
            return false;
        }
        return !Objects.equals(originalPostalAddress.getPostCode(), editedPostalAddress.getPostCode()) ||
            !Objects.equals(originalPostalAddress.getAddressLine1(), editedPostalAddress.getAddressLine1()) ||
            !Objects.equals(originalPostalAddress.getTown(), editedPostalAddress.getTown());
    }

    private void updateAdjacentSites(StopPlace originalStopPlace, StopPlace editedStopPlace) {
        originalStopPlace.getAdjacentSites().clear();
        if (editedStopPlace.getAdjacentSites() != null) {
            originalStopPlace.getAdjacentSites().addAll(editedStopPlace.getAdjacentSites());
        }
    }

    private void updateTariffZones(StopPlace originalStopPlace, StopPlace editedStopPlace) {
        originalStopPlace.getTariffZones().clear();
        if (editedStopPlace.getTariffZones() != null) {
            originalStopPlace.getTariffZones().addAll(editedStopPlace.getTariffZones());
        }
    }

    private void updateKeyValues(StopPlace originalStopPlace, StopPlace editedStopPlace) {
        originalStopPlace.getKeyValues().clear();
        if (editedStopPlace.getKeyValues() != null) {
            originalStopPlace.getKeyValues().putAll(editedStopPlace.getKeyValues());
        }
    }

    private void updateAccessibilityAssessment(StopPlace originalStopPlace, StopPlace editedStopPlace) {
        if (editedStopPlace.getAccessibilityAssessment() != null) {
            if (originalStopPlace.getAccessibilityAssessment() == null) {
                originalStopPlace.setAccessibilityAssessment(createStopPlaceMapper.createCopy(editedStopPlace.getAccessibilityAssessment(), AccessibilityAssessment.class));
            } else {
                var editedAccessibilityAssessmentNetexId = editedStopPlace.getAccessibilityAssessment().getNetexId();
                var originalAccessibilityAssessmentNetexId = originalStopPlace.getAccessibilityAssessment().getNetexId();
                if (!Objects.equals(editedAccessibilityAssessmentNetexId, originalAccessibilityAssessmentNetexId)) {
                    throw new IllegalArgumentException("Cannot update accessibility assessment with netex id " + editedAccessibilityAssessmentNetexId +
                            " on stop place " + originalStopPlace.getNetexId() +
                            " because it does not match the existing accessibility assessment netex id " + originalAccessibilityAssessmentNetexId);
                }
                updateStopPlaceMapper.update(originalStopPlace.getAccessibilityAssessment(), editedStopPlace.getAccessibilityAssessment());
            }
        } else {
            originalStopPlace.setAccessibilityAssessment(null);
        }
    }

    private void updatePlaceEquipments(StopPlace originalStopPlace, StopPlace editedStopPlace) {
        if (editedStopPlace.getPlaceEquipments() != null) {
            if (originalStopPlace.getPlaceEquipments() == null) {
                originalStopPlace.setPlaceEquipments(createStopPlaceMapper.createCopy(editedStopPlace.getPlaceEquipments(), PlaceEquipment.class));
            } else {
                var editedPlaceEquipmentNetexId = editedStopPlace.getPlaceEquipments().getNetexId();
                var originalPlaceEquipmentNetexId = originalStopPlace.getPlaceEquipments().getNetexId();
                if (!Objects.equals(editedPlaceEquipmentNetexId, originalPlaceEquipmentNetexId)) {
                    throw new IllegalArgumentException("Cannot update place equipment with netex id " + editedPlaceEquipmentNetexId +
                            " on stop place " + originalStopPlace.getNetexId() +
                            " because it does not match the existing place equipment netex id " + originalPlaceEquipmentNetexId);
                }
                updateStopPlaceMapper.update(originalStopPlace.getPlaceEquipments(), editedStopPlace.getPlaceEquipments());
            }
        } else {
            originalStopPlace.setPlaceEquipments(null);
        }
    }
}

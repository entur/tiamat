package org.rutebanken.tiamat.ext.fintraffic.api;

import jakarta.xml.bind.JAXBElement;
import org.rutebanken.netex.model.EntityInVersionStructure;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.SiteElement_VersionStructure;
import org.rutebanken.netex.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FintrafficNetexEntityEnricher implements NetexEntityEnricher {

    private static final Logger logger = LoggerFactory.getLogger(FintrafficNetexEntityEnricher.class);

    private static final long STOP_PLACE_OFFSET = 1_000_000L;
    private static final long QUAY_OFFSET = 5_000_000L;
    private static final String NUMERIC_ID_KEY = "peti_numeric_id";

    @Override
    public void enrich(EntityInVersionStructure entity) {
        try {
            if (entity instanceof StopPlace stopPlace) {
                addNumericId(stopPlace, STOP_PLACE_OFFSET);
                enrichQuays(stopPlace);
            }
        } catch (Exception e) {
            logger.warn("Failed to enrich NeTEx entity {}: {}", entity.getId(), e.getMessage(), e);
        }
    }

    private void enrichQuays(StopPlace stopPlace) {
        try {
            if (stopPlace.getQuays() == null || stopPlace.getQuays().getQuayRefOrQuay() == null) {
                return;
            }
            for (JAXBElement<?> element : stopPlace.getQuays().getQuayRefOrQuay()) {
                try {
                    if (element.getValue() instanceof Quay quay) {
                        addNumericId(quay, QUAY_OFFSET);
                    }
                } catch (Exception e) {
                    logger.warn("Failed to enrich Quay in StopPlace {}: {}", stopPlace.getId(), e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to enrich Quays for StopPlace {}: {}", stopPlace.getId(), e.getMessage(), e);
        }
    }

    private void addNumericId(SiteElement_VersionStructure entity, long offset) {
        String id = entity.getId();
        if (id == null || !id.contains(":")) {
            logger.warn("Entity has invalid or missing ID: {}", id);
            return;
        }
        String numericPart = id.substring(id.lastIndexOf(':') + 1);
        long numericId = Long.parseLong(numericPart) + offset;

        if (entity instanceof StopPlace) {
            checkNumericIdIsLessThanMaxValue(id, numericId, QUAY_OFFSET);
        } else if (entity instanceof Quay) {
            checkNumericIdIsLessThanMaxValue(id, numericId, 10_000_000L);
        }

        if (entity.getKeyList() == null) {
            entity.withKeyList(new KeyListStructure());
        }
        entity.getKeyList().getKeyValue().removeIf(kv -> NUMERIC_ID_KEY.equals(kv.getKey()));
        entity.getKeyList()
                .withKeyValue(new KeyValueStructure()
                        .withKey(NUMERIC_ID_KEY)
                        .withValue(String.valueOf(numericId)));
    }

    private void checkNumericIdIsLessThanMaxValue(String id, long numericId, long maxValue) {
        if (numericId > maxValue) {
            logger.warn("Numeric ID for entity {} exceeds maximum value: {}", id, numericId);
        }
    }
}

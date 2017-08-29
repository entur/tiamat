package org.rutebanken.tiamat.versioning;

import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.ValidBetween;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ValidityUpdater {

    private static final Logger logger = LoggerFactory.getLogger(ValidityUpdater.class);

    /**
     * Updates the validity of the new version.
     * If validity or validbetween from date not set, a default value will be set.
     *
     * @param existingVersion
     * @param newVersion
     * @return the new version's valid from date
     */
    protected <T extends EntityInVersionStructure> Instant updateValidBetween(T newVersion, Instant now) {
        Instant newVersionValidFrom;

        if (newVersion.getValidBetween() == null) {
            newVersionValidFrom = now;
            logger.warn("Validity not set for new version of {}. Setting default from date: {}", newVersion.getNetexId(), newVersionValidFrom);
            // Open to date is default.
            newVersion.setValidBetween(new ValidBetween(newVersionValidFrom));
        } else {
            if (newVersion.getValidBetween().getFromDate() == null) {
                logger.warn("From date is not set for the new version of {}. Using now: {}", newVersion.getNetexId(), now);
                newVersionValidFrom = now;
                newVersion.getValidBetween().setFromDate(newVersionValidFrom);
            } else {
                newVersionValidFrom = newVersion.getValidBetween().getFromDate();
            }
        }
        return newVersionValidFrom;
    }

    /**
     * Terminate valid between for entity.
     *
     * @param versionToTerminate the old version of entity to terminate
     * @param newVersionValidFrom the instant when the new version should be valid from
     * @param <T> Versioned entity type
     * @return the updated entity
     */
    public <T extends EntityInVersionStructure> T terminateVersion(T versionToTerminate, Instant newVersionValidFrom) {
        if(versionToTerminate == null) {
            throw new IllegalArgumentException("Cannot terminate version for null object");
        }

        logger.debug("New version valid from {}", newVersionValidFrom);
        if (versionToTerminate.getValidBetween() != null ) {
            versionToTerminate.getValidBetween().setToDate(newVersionValidFrom);
        } else {
            logger.warn("Entity {} does not have valid between from before. Setting only toDate", versionToTerminate.getNetexId());
            versionToTerminate.setValidBetween(new ValidBetween(null, newVersionValidFrom));
        }
        return versionToTerminate;
    }
}

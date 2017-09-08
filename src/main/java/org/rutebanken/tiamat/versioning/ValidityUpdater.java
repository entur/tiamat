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
     * @param newVersion the version to update valid between on
     * @param defaultFromTime
     * @return the new version's valid from date
     */
    protected <T extends EntityInVersionStructure> Instant updateValidBetween(T newVersion, Instant defaultFromTime) {

        instantiateValidBetween(newVersion);;
        if(newVersion.getValidBetween().getFromDate() != null
                && newVersion.getValidBetween().getToDate() != null
                && newVersion.getValidBetween().getFromDate().isAfter(newVersion.getValidBetween().getToDate())) {
            throw new IllegalArgumentException("Entity " + newVersion.getNetexId() + " has from date " + newVersion.getValidBetween().getFromDate() + " after to date " + newVersion.getValidBetween().getToDate());
        }

        if(newVersion.getValidBetween().getFromDate() == null) {
            logger.info("Validity not set, or from date equal to old version for new version of entity with ID: {}. Setting default from time: {}", newVersion.getNetexId(), defaultFromTime);
            newVersion.getValidBetween().setFromDate(defaultFromTime);
        }

        return newVersion.getValidBetween().getFromDate();
    }

    private void instantiateValidBetween(EntityInVersionStructure entity) {
        if(entity != null && entity.getValidBetween() == null) {
            entity.setValidBetween(new ValidBetween());
        }
    }

    /**
     * Terminate valid between for entity. Typically the old version, or when the entity should be terminated for "good".
     *
     * @param versionToTerminate the old version of entity to terminate
     * @param terminateAt the instant when this version should be terminated
     * @param <T> Versioned entity type
     * @return the updated entity
     */
    public <T extends EntityInVersionStructure> void terminateVersion(T versionToTerminate, Instant terminateAt) {
        if(versionToTerminate == null) {
            throw new IllegalArgumentException("Cannot terminate version for null object");
        }

        logger.info("Version {} of {} will be invalid at {}", versionToTerminate.getVersion(), versionToTerminate.getNetexId(), terminateAt);
        if (versionToTerminate.getValidBetween() != null ) {
            versionToTerminate.getValidBetween().setToDate(terminateAt);
        } else {
            logger.warn("Entity {} does not have valid between. Setting toDate.", versionToTerminate.getNetexId());
            versionToTerminate.setValidBetween(new ValidBetween(null, terminateAt));
        }
    }
}
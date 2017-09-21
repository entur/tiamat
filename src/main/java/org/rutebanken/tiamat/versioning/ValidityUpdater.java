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

import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.ValidBetween;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ValidityUpdater {

    private static final Logger logger = LoggerFactory.getLogger(ValidityUpdater.class);

    protected <T extends EntityInVersionStructure> Instant updateValidBetween(T newVersion, Instant defaultFromTime) {
        return updateValidBetween(null, newVersion, defaultFromTime);
    }

    /**
     * Updates the validity of the new version.
     * If validity or validbetween from date not set, a default value will be set.
     *
     * @param newVersion the version to update valid between on
     * @param defaultFromTime
     * @return the new version's valid from date
     */
    protected <T extends EntityInVersionStructure> Instant updateValidBetween(T existingVersion, T newVersion, Instant defaultFromTime) {

        instantiateValidBetween(newVersion);

        if(newVersion.getValidBetween().getFromDate() != null
                && newVersion.getValidBetween().getToDate() != null
                && newVersion.getValidBetween().getFromDate().isAfter(newVersion.getValidBetween().getToDate())) {
            throw new IllegalArgumentException("Entity " + newVersion.getNetexId() + " has from date " + newVersion.getValidBetween().getFromDate() + " after to date " + newVersion.getValidBetween().getToDate());
        }

        if(newVersion.getValidBetween().getFromDate() == null) {
            logger.info("Validity not set, or from date equal to old version for new version of entity with ID: {}. Setting default from time: {}", newVersion.getNetexId(), defaultFromTime);
            newVersion.getValidBetween().setFromDate(defaultFromTime);
        }

        if(existingVersion != null) {
            String entityString = existingVersion.getNetexId() + " " +existingVersion.getVersion();
            validateNewVersionDateAfter("Existing version " + entityString + " to date", existingVersion.getValidBetween().getToDate(), newVersion.getValidBetween().getFromDate());
            validateNewVersionDateAfter("Existing version " + entityString + " from date", existingVersion.getValidBetween().getFromDate(), newVersion.getValidBetween().getFromDate());
        }

        return newVersion.getValidBetween().getFromDate();
    }

    private void validateNewVersionDateAfter(String description, Instant previousVersionDate, Instant newVersionFromDate) {
        if(previousVersionDate != null && newVersionFromDate.isBefore(previousVersionDate)) {
            throw new IllegalArgumentException(description + previousVersionDate + " is after to fromdate" + newVersionFromDate);
        }
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

        if(versionToTerminate.getValidBetween() == null) {
            logger.warn("Entity {} does not have valid between. Setting toDate.", versionToTerminate.getNetexId());
            versionToTerminate.setValidBetween(new ValidBetween(null, terminateAt));
        } else if (versionToTerminate.getValidBetween().getToDate() == null) {
            // Only terminate if to date not already set.
            versionToTerminate.getValidBetween().setToDate(terminateAt);
        }

        logger.info("Version {} of {} will be invalid at {}", versionToTerminate.getVersion(), versionToTerminate.getNetexId(), terminateAt);
    }
}

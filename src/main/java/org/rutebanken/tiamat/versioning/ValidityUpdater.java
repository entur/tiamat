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

import org.rutebanken.tiamat.config.Messages;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.time.ExportTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.rutebanken.tiamat.config.Messages.*;

@Service
public class ValidityUpdater {

    private static final Logger logger = LoggerFactory.getLogger(ValidityUpdater.class);

    @Autowired
    private Messages messages;

    @Autowired
    private ExportTimeZone defaultTimeZone;

    protected <T extends EntityInVersionStructure> Instant updateValidBetween(T newVersion, Instant defaultFromTime) {
        return updateValidBetween(null, newVersion, defaultFromTime);
    }

    /**
     * Updates the validity of the new version.
     * If validity or validbetween from date not set, a default value will be set.
     *
     * @param newVersion      the version to update valid between on
     * @param defaultFromTime
     * @return the new version's valid from date
     */
    protected <T extends EntityInVersionStructure> Instant updateValidBetween(T existingVersion, T newVersion, Instant defaultFromTime) {

        instantiateValidBetween(newVersion);

        if (newVersion.getValidBetween().getFromDate() != null
                && newVersion.getValidBetween().getToDate() != null
                && newVersion.getValidBetween().getFromDate().isAfter(newVersion.getValidBetween().getToDate())) {
            throw new IllegalArgumentException(messages.get(VALIDATION_FROM_DATE_AFTER_TO_DATE, newVersion.getNetexId(), formatToLocalDateTime(newVersion.getValidBetween().getFromDate()), formatToLocalDateTime(newVersion.getValidBetween().getToDate())));
        }

        if (newVersion.getValidBetween().getFromDate() == null) {
            logger.info(messages.get(VALIDATION_FROM_DATE_NOT_SET, newVersion.getNetexId(), formatToLocalDateTime(defaultFromTime)));
            newVersion.getValidBetween().setFromDate(defaultFromTime);
        }

        if (existingVersion != null && existingVersion.getValidBetween() != null) {
            String entityString = existingVersion.getNetexId() + " " + existingVersion.getVersion();
            validateNewVersionDateAfter(VALIDATION_TO_DATE_AFTER_NEXT_VERSION_FROM_DATE, entityString, existingVersion.getValidBetween().getToDate(), newVersion.getValidBetween().getFromDate());
            validateNewVersionDateAfter(VALIDATION_FROM_DATE_AFTER_NEXT_VERSION_FROM_DATE, entityString, existingVersion.getValidBetween().getFromDate(), newVersion.getValidBetween().getFromDate());
        }

        return newVersion.getValidBetween().getFromDate();
    }

    /**
     * FIXME : any way to have incoming TZ instead of relying on default TZ ?
     *
     * Format an instant to human readable string. Used in error messages.
     *
     * @param dateToConvert instant to convert, UTC.
     * @return instant converte to LocalDateTime using default time zone.
     */
    private String formatToLocalDateTime(Instant dateToConvert) {
        return LocalDateTime.from(dateToConvert.atZone(defaultTimeZone.getDefaultTimeZoneId())).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Resolve from-date from prevous version, or use default.
     * If previous version has from- or to-date after defaultFromTime.
     */
    private <T extends EntityInVersionStructure> Instant resolveFromDate(T existingVersion, String netexVersion, Instant defaultFromTime) {
        Instant fromDate = null;

        if (existingVersion != null && existingVersion.getValidBetween() != null) {
            if (existingVersion.getValidBetween().getToDate() != null) {
                logger.info("From date not set for new version of entity with ID: {}. Using existing version to date: {}",
                        netexVersion,
                        existingVersion.getValidBetween().getToDate());
                fromDate = existingVersion.getValidBetween().getToDate().plusMillis(1);
            } else if (existingVersion != null && existingVersion.getValidBetween().getFromDate() != null) {
                logger.info("From date not set for new version of entity with ID: {}. Using existing version from date: {}",
                        netexVersion,
                        existingVersion.getValidBetween().getFromDate());
                fromDate = existingVersion.getValidBetween().getFromDate().plusMillis(1);
            }
        }

        if (fromDate == null) {
            logger.info("From date not set and cannot be detected from previous version for new version of entity with ID: {}. Setting default from time: {}",
                    netexVersion, defaultFromTime);
            fromDate = defaultFromTime;
        } else if (fromDate.isBefore(defaultFromTime)) {
            logger.info("Detected from date (from previous version) is before default from time for entity with ID: {}. Setting default from time: {}",
                    netexVersion, defaultFromTime);
            fromDate = defaultFromTime;
        }
        return fromDate;
    }

    private void validateNewVersionDateAfter(String messageKey, String entityId, Instant previousVersionDate, Instant newVersionFromDate) {
        if (previousVersionDate != null && previousVersionDate.isAfter(newVersionFromDate)) {
            throw new IllegalArgumentException(messages.get(messageKey, entityId, formatToLocalDateTime(previousVersionDate), formatToLocalDateTime(newVersionFromDate)));
        }
    }

    private void instantiateValidBetween(EntityInVersionStructure entity) {
        if (entity != null && entity.getValidBetween() == null) {
            entity.setValidBetween(new ValidBetween());
        }
    }

    /**
     * Terminate valid between for entity. Typically the old version, or when the entity should be terminated for "good".
     *
     * @param versionToTerminate the old version of entity to terminate
     * @param terminateAt        the instant when this version should be terminated
     * @param <T>                Versioned entity type
     * @return the updated entity
     */
    public <T extends EntityInVersionStructure> void terminateVersion(T versionToTerminate, Instant terminateAt) {
        if (versionToTerminate == null) {
            throw new IllegalArgumentException(messages.get(VALIDATION_CANNOT_TERMINATE_FOR_NULL));
        }

        if (versionToTerminate.getValidBetween() == null) {
            logger.warn("Entity {} does not have valid between. Setting toDate.", versionToTerminate.getNetexId());
            versionToTerminate.setValidBetween(new ValidBetween(null, terminateAt));
        } else if (versionToTerminate.getValidBetween().getToDate() == null) {
            // Only terminate if to date not already set.
            versionToTerminate.getValidBetween().setToDate(terminateAt);
        }

        logger.info("Version {} of {} will be invalid at {}", versionToTerminate.getVersion(), versionToTerminate.getNetexId(), terminateAt);
    }
}

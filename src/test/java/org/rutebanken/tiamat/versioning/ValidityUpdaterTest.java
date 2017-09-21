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

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.ValidBetween;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Version;
import javax.persistence.criteria.CriteriaBuilder;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class ValidityUpdaterTest extends TiamatIntegrationTest {

    @Autowired
    private ValidityUpdater validityUpdater;

    @Test
    public void terminateVersionsWithoutValidBetween() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);

        Instant now = Instant.now();
        validityUpdater.terminateVersion(stopPlace, now);

        assertThat(stopPlace.getValidBetween()).isNotNull();
        assertThat(stopPlace.getValidBetween().getToDate()).isEqualTo(now);
    }

    @Test
    public void updateValidBetweenWhenNotPreviouslySet() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);

        Instant now = Instant.now();
        validityUpdater.updateValidBetween(stopPlace, now);

        assertThat(stopPlace.getValidBetween()).isNotNull();
        assertThat(stopPlace.getValidBetween().getFromDate()).as("from date").isEqualTo(now);
        assertThat(stopPlace.getValidBetween().getToDate()).as("to date").isNull();
    }

    @Test
    public void updateValidBetweenWhenFromDateNotPreviouslySet() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        stopPlace.setValidBetween(new ValidBetween(null));

        Instant now = Instant.now();
        validityUpdater.updateValidBetween(stopPlace, now);

        assertThat(stopPlace.getValidBetween()).isNotNull();
        assertThat(stopPlace.getValidBetween().getFromDate()).as("from date").isEqualTo(now);
        assertThat(stopPlace.getValidBetween().getToDate()).as("to date").isNull();
    }

    @Test
    public void useValidBetweenFromDateIfAlreadySet() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        Instant now = Instant.now();
        stopPlace.setValidBetween(new ValidBetween(now));

        Instant actual = validityUpdater.updateValidBetween(stopPlace, now);

        assertThat(stopPlace.getValidBetween()).isNotNull();
        assertThat(stopPlace.getValidBetween().getFromDate()).as("from date").isEqualTo(now);
        assertThat(stopPlace.getValidBetween().getToDate()).as("to date").isNull();
        assertThat(actual).as("new version valid from").isEqualTo(now);
    }


    @Test(expected = IllegalArgumentException.class)
    public void doNotAcceptFromDateAfterToDate() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);
        Instant now = Instant.now();
        stopPlace.setValidBetween(new ValidBetween(now, now.minusSeconds(10)));

        validityUpdater.updateValidBetween(stopPlace, now);
    }

    @Test(expected = IllegalArgumentException.class)
    public void doNotAcceptFromDateBeforePreviousVersionEndDate() {
        StopPlace previousVersion = new StopPlace();
        previousVersion.setVersion(1L);
        Instant now = Instant.now();
        previousVersion.setValidBetween(new ValidBetween(now.minusSeconds(1000), now));

        StopPlace newVersion = new StopPlace();
        newVersion.setVersion(2L);
        newVersion.setValidBetween(new ValidBetween(previousVersion.getValidBetween().getToDate().minusSeconds(10)));

        validityUpdater.updateValidBetween(previousVersion, newVersion, now);
    }

    @Test(expected = IllegalArgumentException.class)
    public void doNotAcceptFromDateBeforePreviousVersionFromDate() {
        StopPlace previousVersion = new StopPlace();
        previousVersion.setVersion(1L);
        Instant now = Instant.now();

        // No to date
        previousVersion.setValidBetween(new ValidBetween(now.minusSeconds(1000), null));

        StopPlace newVersion = new StopPlace();
        newVersion.setVersion(2L);
        newVersion.setValidBetween(new ValidBetween(previousVersion.getValidBetween().getFromDate().minusSeconds(10)));

        validityUpdater.updateValidBetween(previousVersion, newVersion, now);
    }

    @Test
    public void doNotSetEndDateOnPreviousVersionIfAlreadySet() {
        StopPlace oldVersion = new StopPlace();
        oldVersion.setVersion(1L);
        oldVersion.setValidBetween(new ValidBetween(Instant.EPOCH, Instant.EPOCH));

        validityUpdater.terminateVersion(oldVersion, Instant.now());

        assertThat(oldVersion.getValidBetween().getToDate()).isEqualTo(Instant.EPOCH);
    }

}
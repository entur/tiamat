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

}
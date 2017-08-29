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

    @Autowired
    private VersionCreator versionCreator;

    @Test
    public void terminateVersionsWithoutValidBetween() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setVersion(1L);

        Instant now = Instant.now();
        stopPlace = validityUpdater.terminateVersion(stopPlace, now);

        assertThat(stopPlace.getValidBetween()).isNotNull();
        assertThat(stopPlace.getValidBetween().getToDate()).isEqualTo(now);
    }

    @Test
    public void newTerminatedVersionShouldHaveValidBetween() {
        StopPlace oldVersion = new StopPlace();
        oldVersion.setVersion(1L);

        Quay quay = new Quay();
        quay.setVersion(1L);

        oldVersion.getQuays().add(quay);

        oldVersion.setValidBetween(new ValidBetween(Instant.now().minus(2, ChronoUnit.DAYS)));

        oldVersion = stopPlaceRepository.save(oldVersion);

        Instant beforeCreated = Instant.now();
        System.out.println(beforeCreated);

        StopPlace newVersion = versionCreator.createCopy(oldVersion, StopPlace.class);

        oldVersion = validityUpdater.terminateVersion(oldVersion, Instant.now());

        assertThat(newVersion.getValidBetween())
                .isNotNull();

        System.out.println(oldVersion.getValidBetween().getToDate());
        assertThat(oldVersion.getValidBetween().getToDate()).isAfterOrEqualTo(beforeCreated);


        ValidBetween validBetween = newVersion.getValidBetween();
        assertThat(validBetween.getFromDate()).isAfterOrEqualTo(beforeCreated);
        assertThat(validBetween.getToDate()).isNull();

    }

}
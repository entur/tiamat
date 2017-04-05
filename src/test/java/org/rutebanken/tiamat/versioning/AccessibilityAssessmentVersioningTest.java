package org.rutebanken.tiamat.versioning;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.model.LimitationStatusEnumeration.*;

public class AccessibilityAssessmentVersioningTest extends TiamatIntegrationTest {

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Test
    public void testAccessibilityVersioning() {

        /**
         * Create StopPlace with two quays with different AccessibilityAssessment
         */
        Quay quay1 = new Quay();
        quay1.setName(new EmbeddableMultilingualString("quay1"));
        quay1.setAccessibilityAssessment(createAccessibilityAssessment(FALSE));

        Quay quay2 = new Quay();
        quay2.setName(new EmbeddableMultilingualString("quay2"));
        quay2.setAccessibilityAssessment(createAccessibilityAssessment(TRUE));

        StopPlace stopPlace = new StopPlace();
        stopPlace.getQuays().add(quay1);
        stopPlace.getQuays().add(quay2);

        StopPlace stopPlace_v1 = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        assertThat(stopPlace_v1.getAccessibilityAssessment()).isNull();
        assertThat(stopPlace_v1.getQuays()).isNotNull();
        stopPlace_v1.getQuays()
                .stream()
                .forEach(quay -> {

                    assertThat(quay.getAccessibilityAssessment()).isNotNull();
                    assertThat(quay.getAccessibilityAssessment().getVersion()).isEqualTo(1);

                    AccessibilityLimitation actualAccessibilityLimitation = quay.getAccessibilityAssessment().getLimitations().get(0);
                    assertThat(actualAccessibilityLimitation).isNotNull();
                    assertThat(actualAccessibilityLimitation.getVersion()).isEqualTo(1);
                });

        /**
         * One of the quays updates AccessibilityAssessment
         */
        stopPlace = stopPlaceVersionedSaverService.createCopy(stopPlace_v1);

        stopPlace.getQuays().forEach(quay -> {
            if (quay.getName().getValue().equals(quay1.getName().getValue())) {
                AccessibilityLimitation limitation = quay.getAccessibilityAssessment().getLimitations().get(0);
                limitation.setWheelchairAccess(PARTIAL);
            }
        });

        StopPlace stopPlace_v2 = stopPlaceVersionedSaverService.saveNewVersion(stopPlace_v1, stopPlace);

        assertThat(stopPlace_v2.getAccessibilityAssessment()).isNull();
        assertThat(stopPlace_v2.getQuays()).isNotNull();
        assertThat(stopPlace_v2.getVersion()).isEqualTo((long) 2);
        stopPlace_v2.getQuays()
                .stream()
                .forEach(quay -> {
                    assertThat(quay.getVersion()).isEqualTo((long) 2);

                    assertThat(quay.getAccessibilityAssessment()).isNotNull();
                    assertThat(quay.getAccessibilityAssessment().getVersion()).isEqualTo((long) 2);

                    AccessibilityLimitation actualAccessibilityLimitation = quay.getAccessibilityAssessment().getLimitations().get(0);
                    assertThat(actualAccessibilityLimitation).isNotNull();
                    assertThat(actualAccessibilityLimitation.getVersion()).isEqualTo((long) 2);
                });


        /**
         * Update existing assessments, and add a new quay.
         *
         * All AccessibilityAssessments are different
         */
        stopPlace = stopPlaceVersionedSaverService.createCopy(stopPlace_v2);

        stopPlace.getQuays().forEach(quay -> {
            AccessibilityLimitation limitation = quay.getAccessibilityAssessment().getLimitations().get(0);
            limitation.setWheelchairAccess(UNKNOWN);
        });
        Quay quay3 = new Quay();
        quay3.setName(new EmbeddableMultilingualString("quay3"));
        quay3.setAccessibilityAssessment(createAccessibilityAssessment(PARTIAL));
        stopPlace.getQuays().add(quay3);


        StopPlace stopPlace_v3 = stopPlaceVersionedSaverService.saveNewVersion(stopPlace_v2, stopPlace);

        assertThat(stopPlace_v3.getAccessibilityAssessment()).isNull();
        assertThat(stopPlace_v3.getQuays()).isNotNull();
        assertThat(stopPlace_v3.getVersion()).isEqualTo((long) 3);
        stopPlace_v3.getQuays()
                .stream()
                .forEach(quay -> {
                    long version = 3;
                    if (quay.getName().getValue().equals(quay3.getName().getValue())) {
                        version = 1;
                    }
                    assertThat(quay.getVersion()).isEqualTo(version);

                    assertThat(quay.getAccessibilityAssessment()).isNotNull();
                    assertThat(quay.getAccessibilityAssessment().getVersion()).isEqualTo(version);

                    AccessibilityLimitation actualAccessibilityLimitation = quay.getAccessibilityAssessment().getLimitations().get(0);
                    assertThat(actualAccessibilityLimitation).isNotNull();
                    assertThat(actualAccessibilityLimitation.getVersion()).isEqualTo(version);
                });

        /**
         * All Quays are given the same limitations
         */
        stopPlace = stopPlaceVersionedSaverService.createCopy(stopPlace_v3);

        stopPlace.getQuays().forEach(quay -> {
            AccessibilityLimitation limitation = quay.getAccessibilityAssessment().getLimitations().get(0);
            limitation.setWheelchairAccess(UNKNOWN);
            limitation.setLiftFreeAccess(UNKNOWN);
            limitation.setEscalatorFreeAccess(UNKNOWN);
            limitation.setAudibleSignalsAvailable(UNKNOWN);
            limitation.setStepFreeAccess(UNKNOWN);
        });


        StopPlace stopPlace_v4 = stopPlaceVersionedSaverService.saveNewVersion(stopPlace_v3, stopPlace);

        assertThat(stopPlace_v4.getAccessibilityAssessment()).isNotNull();
        assertThat(stopPlace_v4.getAccessibilityAssessment().getVersion()).isEqualTo(1);

        assertThat(stopPlace_v4.getQuays()).isNotNull();
        assertThat(stopPlace_v4.getVersion()).isEqualTo(4);
        stopPlace_v4.getQuays()
                .stream()
                .forEach(quay -> {
                    long version = 4;
                    if (quay.getName().getValue().equals(quay3.getName().getValue())) {
                        version = 2;
                    }
                    assertThat(quay.getVersion()).isEqualTo(version);
                    assertThat(quay.getAccessibilityAssessment()).isNull();
                });

    }




    protected AccessibilityAssessment createAccessibilityAssessment(LimitationStatusEnumeration limitation) {
        return createAccessibilityAssessment(limitation,limitation,limitation,limitation,limitation);
    }

    protected AccessibilityAssessment createAccessibilityAssessment(LimitationStatusEnumeration wheelchairAccess, LimitationStatusEnumeration liftFreeAccess, LimitationStatusEnumeration escalatorFreeAccess, LimitationStatusEnumeration audibleSignalsAvailable, LimitationStatusEnumeration stepFreeAccess) {
        AccessibilityAssessment accessibilityAssessment = new AccessibilityAssessment();

        AccessibilityLimitation accessibilityLimitation = new AccessibilityLimitation();
        accessibilityLimitation.setWheelchairAccess(wheelchairAccess);
        accessibilityLimitation.setLiftFreeAccess(liftFreeAccess);
        accessibilityLimitation.setEscalatorFreeAccess(escalatorFreeAccess);
        accessibilityLimitation.setAudibleSignalsAvailable(audibleSignalsAvailable);
        accessibilityLimitation.setStepFreeAccess(stepFreeAccess);
        List<AccessibilityLimitation> limitations = new ArrayList<>();
        limitations.add(accessibilityLimitation);
        accessibilityAssessment.setLimitations(limitations);
        return accessibilityAssessment;
    }
}

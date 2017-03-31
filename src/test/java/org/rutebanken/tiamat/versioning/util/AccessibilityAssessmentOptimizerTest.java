package org.rutebanken.tiamat.versioning.util;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.QuayRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.rutebanken.tiamat.model.LimitationStatusEnumeration.*;


@Transactional
@Commit
public class AccessibilityAssessmentOptimizerTest extends TiamatIntegrationTest {

    @Autowired
    public QuayRepository quayRepository;

    @Autowired
    public StopPlaceRepository stopPlaceRepository;

    @Autowired
    public StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    /*
     * Testcase:
     * - StopPlace and two quays with equal AccessibilityAssessment
     * - Quays should have values set to null
     * - StopPlace should hold AccessibilityAssessment
     */
    @Test
    public void persistStopPlaceWithAllEqualAccessibilityAssessmentLimitation() {
        Quay quay1 = new Quay();
        quay1.setName(createEmbeddableString("quay1"));
        quay1.setAccessibilityAssessment(createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, TRUE));

        Quay quay2 = new Quay();
        quay2.setName(createEmbeddableString("quay2"));
        quay2.setAccessibilityAssessment(createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, TRUE));

        quayRepository.save(quay1);
        quayRepository.save(quay2);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setAccessibilityAssessment(createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, TRUE));

        stopPlace.getQuays().add(quay1);
        stopPlace.getQuays().add(quay2);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        assertThat(actualStopPlace.getAccessibilityAssessment()).isNotNull();
        for (Quay quay : stopPlace.getQuays()) {
            assertThat(quay.getAccessibilityAssessment()).isNull();
        }

        AccessibilityLimitation actualAccessibilityLimitation = actualStopPlace.getAccessibilityAssessment().getLimitations().get(0);

        assertThat(actualAccessibilityLimitation).isNotNull();
        assertThat(actualAccessibilityLimitation.getWheelchairAccess()).isEqualTo(TRUE);
        assertThat(actualAccessibilityLimitation.getLiftFreeAccess()).isEqualTo(TRUE);
        assertThat(actualAccessibilityLimitation.getEscalatorFreeAccess()).isEqualTo(TRUE);
        assertThat(actualAccessibilityLimitation.getAudibleSignalsAvailable()).isEqualTo(TRUE);
        assertThat(actualAccessibilityLimitation.getStepFreeAccess()).isEqualTo(TRUE);

        assertThat(actualStopPlace.getAccessibilityAssessment().getMobilityImpairedAccess()).isEqualTo(TRUE);
    }

    /*
     * Testcase:
     * - Two quays with equal AccessibilityAssessment
     * - Quays hould have values set to null
     * - StopPlace should hold AccessibilityAssessment
     */
    @Test
    public void persistStopPlaceWithAllQuaysEqualAccessibilityAssessmentLimitation() {
        Quay quay1 = new Quay();
        quay1.setName(createEmbeddableString("quay1"));
        quay1.setAccessibilityAssessment(createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, TRUE));

        Quay quay2 = new Quay();
        quay2.setName(createEmbeddableString("quay2"));
        quay2.setAccessibilityAssessment(createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, TRUE));

        StopPlace stopPlace = new StopPlace();
        stopPlace.setAccessibilityAssessment(null);

        stopPlace.getQuays().add(quay1);
        stopPlace.getQuays().add(quay2);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        assertThat(actualStopPlace.getAccessibilityAssessment()).isNotNull();
        for (Quay quay : stopPlace.getQuays()) {
            assertThat(quay.getAccessibilityAssessment()).isNull();
        }

        AccessibilityLimitation actualAccessibilityLimitation = actualStopPlace.getAccessibilityAssessment().getLimitations().get(0);

        assertThat(actualAccessibilityLimitation).isNotNull();
        assertThat(actualAccessibilityLimitation.getWheelchairAccess()).isEqualTo(TRUE);
        assertThat(actualAccessibilityLimitation.getLiftFreeAccess()).isEqualTo(TRUE);
        assertThat(actualAccessibilityLimitation.getEscalatorFreeAccess()).isEqualTo(TRUE);
        assertThat(actualAccessibilityLimitation.getAudibleSignalsAvailable()).isEqualTo(TRUE);
        assertThat(actualAccessibilityLimitation.getStepFreeAccess()).isEqualTo(TRUE);

        assertThat(actualStopPlace.getAccessibilityAssessment().getMobilityImpairedAccess()).isEqualTo(TRUE);
    }

    /*
     * Testcase:
     * - Two quays with equal AccessibilityAssessment
     * - Should have values set to null, StopPlace should hold AccessibilityAssessment
     * - Add quay to StopPlace with different AccessibilityAssessment
     * - Quays should now hold AccessibilityAssessment
     * - StopPlace should have null
     */
    @Test
    public void persistAndUpdateStopPlaceWithAccessibilityAssessment() {

        Quay quay1 = new Quay();
        quay1.setName(createEmbeddableString("quay1"));
        quay1.setAccessibilityAssessment(createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, TRUE));

        Quay quay2 = new Quay();
        quay2.setName(createEmbeddableString("quay2"));
        quay2.setAccessibilityAssessment(createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, TRUE));

        StopPlace stopPlace = new StopPlace();

        AccessibilityAssessment accessibilityAssessment = createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, TRUE);
        AccessibilityLimitation limitation = accessibilityAssessment.getLimitations().get(0);

        stopPlace.setAccessibilityAssessment(createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, TRUE));

        stopPlace.getQuays().add(quay1);
        stopPlace.getQuays().add(quay2);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        StopPlace actualStopPlace = stopPlaceVersionedSaverService.createNewVersion(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId()));

        assertThat(actualStopPlace.getAccessibilityAssessment()).isNotNull();
        assertThat(actualStopPlace.getAccessibilityAssessment().getLimitations()).isNotNull();
        assertThat(actualStopPlace.getAccessibilityAssessment().getLimitations()).isNotEmpty();
        assertThat(actualStopPlace.getAccessibilityAssessment().getLimitations().get(0).getWheelchairAccess()).isEqualTo(limitation.getWheelchairAccess());

        Quay quay3 = new Quay();
        quay3.setName(createEmbeddableString("quay3"));
        quay3.setAccessibilityAssessment(createAccessibilityAssessment(PARTIAL, TRUE, TRUE, TRUE, TRUE));

        actualStopPlace.getQuays().add(quay3);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace, actualStopPlace);

        StopPlace actualStopPlace2 = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        assertThat(actualStopPlace2.getAccessibilityAssessment()).isNull();


        actualStopPlace2.getQuays()
                .stream()
                .forEach(quay -> {
                    AccessibilityLimitation actualAccessibilityLimitation = quay.getAccessibilityAssessment().getLimitations().get(0);

                    assertThat(actualAccessibilityLimitation).isNotNull();

                    if (quay.getName().getValue().equals(quay3.getName().getValue())) {
                        assertThat(actualAccessibilityLimitation.getWheelchairAccess()).isEqualTo(PARTIAL);
                        assertThat(actualAccessibilityLimitation.getLiftFreeAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getEscalatorFreeAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getAudibleSignalsAvailable()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getStepFreeAccess()).isEqualTo(TRUE);
                        assertThat(quay.getAccessibilityAssessment().getMobilityImpairedAccess()).isEqualTo(PARTIAL);
                    } else {
                        assertThat(actualAccessibilityLimitation.getWheelchairAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getLiftFreeAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getEscalatorFreeAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getAudibleSignalsAvailable()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getStepFreeAccess()).isEqualTo(TRUE);
                        assertThat(quay.getAccessibilityAssessment().getMobilityImpairedAccess()).isEqualTo(TRUE);
                    }
                });
    }

    /*
     * Testcase:
     * - two existing quays have different AccessibilityAssessment
     * - StopPlace thus has none
     * - A quay is added - should be populated with all UNKNOWN
     */
    @Test
    public void persistAndUpdateStopPlaceWithQuayWithNullAccessibilityAssessment() {

        Quay quay1 = new Quay();
        quay1.setName(createEmbeddableString("quay1"));
        quay1.setAccessibilityAssessment(createAccessibilityAssessment(FALSE, TRUE, TRUE, TRUE, TRUE));

        Quay quay2 = new Quay();
        quay2.setName(createEmbeddableString("quay2"));
        quay2.setAccessibilityAssessment(createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, TRUE));

        StopPlace stopPlace = new StopPlace();
        stopPlace.setAccessibilityAssessment(createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, TRUE));

        stopPlace.getQuays().add(quay1);
        stopPlace.getQuays().add(quay2);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        StopPlace actualStopPlace = stopPlaceVersionedSaverService.createNewVersion(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId()));

        assertThat(actualStopPlace.getAccessibilityAssessment()).isNull();

        Quay quay3 = new Quay();
        quay3.setName(createEmbeddableString("quay3"));
        quay3.setAccessibilityAssessment(null);

        actualStopPlace.getQuays().add(quay3);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace, actualStopPlace);

        StopPlace actualStopPlace2 = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        assertThat(actualStopPlace2.getAccessibilityAssessment()).isNull();


        actualStopPlace2.getQuays()
                .stream()
                .forEach(quay -> {
                    AccessibilityLimitation actualAccessibilityLimitation = quay.getAccessibilityAssessment().getLimitations().get(0);

                    assertThat(actualAccessibilityLimitation).isNotNull();

                    if (quay.getName().getValue().equals(quay3.getName().getValue())) {
                        assertThat(actualAccessibilityLimitation.getWheelchairAccess()).isEqualTo(UNKNOWN);
                        assertThat(actualAccessibilityLimitation.getLiftFreeAccess()).isEqualTo(UNKNOWN);
                        assertThat(actualAccessibilityLimitation.getEscalatorFreeAccess()).isEqualTo(UNKNOWN);
                        assertThat(actualAccessibilityLimitation.getAudibleSignalsAvailable()).isEqualTo(UNKNOWN);
                        assertThat(actualAccessibilityLimitation.getStepFreeAccess()).isEqualTo(UNKNOWN);
                        assertThat(quay.getAccessibilityAssessment().getMobilityImpairedAccess()).isEqualTo(UNKNOWN);
                    } else if (quay.getName().getValue().equals(quay1.getName().getValue())) {
                        assertThat(actualAccessibilityLimitation.getWheelchairAccess()).isEqualTo(FALSE);
                        assertThat(actualAccessibilityLimitation.getLiftFreeAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getEscalatorFreeAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getAudibleSignalsAvailable()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getStepFreeAccess()).isEqualTo(TRUE);
                        assertThat(quay.getAccessibilityAssessment().getMobilityImpairedAccess()).isEqualTo(PARTIAL);
                    } else {
                        assertThat(actualAccessibilityLimitation.getWheelchairAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getLiftFreeAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getEscalatorFreeAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getAudibleSignalsAvailable()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getStepFreeAccess()).isEqualTo(TRUE);
                        assertThat(quay.getAccessibilityAssessment().getMobilityImpairedAccess()).isEqualTo(TRUE);
                    }
                });
    }


    /*
     * Testcase:
     * - Two quays - no AccessibilityAssessment set
     * - StopPlace thus has none
     * - One quay gets AccessibilityAssessment, other quay should be set to all UNKNOWN
     */
    @Test
    public void persistAndUpdateStopPlaceWithQuayWithAllNullAccessibilityAssessment() {

        Quay quay1 = new Quay();
        quay1.setName(createEmbeddableString("quay1"));

        Quay quay2 = new Quay();
        quay2.setName(createEmbeddableString("quay2"));

        StopPlace stopPlace = new StopPlace();

        stopPlace.getQuays().add(quay1);
        stopPlace.getQuays().add(quay2);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        StopPlace actualStopPlace = stopPlaceVersionedSaverService.createNewVersion(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId()));

        assertThat(actualStopPlace.getAccessibilityAssessment()).isNotNull();
        AccessibilityLimitation limitation = actualStopPlace.getAccessibilityAssessment().getLimitations().get(0);
        assertThat(limitation.getWheelchairAccess()).isEqualTo(UNKNOWN);
        assertThat(limitation.getLiftFreeAccess()).isEqualTo(UNKNOWN);
        assertThat(limitation.getEscalatorFreeAccess()).isEqualTo(UNKNOWN);
        assertThat(limitation.getAudibleSignalsAvailable()).isEqualTo(UNKNOWN);
        assertThat(limitation.getStepFreeAccess()).isEqualTo(UNKNOWN);
        assertThat(stopPlace.getAccessibilityAssessment().getMobilityImpairedAccess()).isEqualTo(UNKNOWN);

        actualStopPlace.getQuays().stream().forEach(quay -> assertThat(quay.getAccessibilityAssessment()).isNull());

        Set<Quay> quays = actualStopPlace.getQuays();
        for (Quay quay : quays) {
            if (quay.getName().getValue().equals(quay1.getName().getValue())) {
                quay.setAccessibilityAssessment(createAccessibilityAssessment(FALSE, TRUE, TRUE, TRUE, TRUE));
            }
        }

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace, actualStopPlace);

        StopPlace actualStopPlace2 = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        assertThat(actualStopPlace2.getAccessibilityAssessment()).isNull();

        actualStopPlace2.getQuays()
                .stream()
                .forEach(quay -> {
                    AccessibilityLimitation actualAccessibilityLimitation = quay.getAccessibilityAssessment().getLimitations().get(0);

                    assertThat(actualAccessibilityLimitation).isNotNull();

                    if (quay.getName().getValue().equals(quay1.getName().getValue())) {
                        assertThat(actualAccessibilityLimitation.getWheelchairAccess()).isEqualTo(FALSE);
                        assertThat(actualAccessibilityLimitation.getLiftFreeAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getEscalatorFreeAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getAudibleSignalsAvailable()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getStepFreeAccess()).isEqualTo(TRUE);
                        assertThat(quay.getAccessibilityAssessment().getMobilityImpairedAccess()).isEqualTo(PARTIAL);
                    } else {
                        assertThat(actualAccessibilityLimitation.getWheelchairAccess()).isEqualTo(UNKNOWN);
                        assertThat(actualAccessibilityLimitation.getLiftFreeAccess()).isEqualTo(UNKNOWN);
                        assertThat(actualAccessibilityLimitation.getEscalatorFreeAccess()).isEqualTo(UNKNOWN);
                        assertThat(actualAccessibilityLimitation.getAudibleSignalsAvailable()).isEqualTo(UNKNOWN);
                        assertThat(actualAccessibilityLimitation.getStepFreeAccess()).isEqualTo(UNKNOWN);
                        assertThat(quay.getAccessibilityAssessment().getMobilityImpairedAccess()).isEqualTo(UNKNOWN);
                    }
                });
    }

    /*
     * Testcase:
     * - StopPlace and two quays with different AccessibilityAssessment
     * - Quays should have values set, StopPlace should have null
     */
    @Test
    public void persistStopPlaceWithDifferentAccessibilityAssessmentLimitation() {

        Quay quay1 = new Quay();
        quay1.setName(createEmbeddableString("quay1"));
        quay1.setAccessibilityAssessment(createAccessibilityAssessment(PARTIAL, TRUE, TRUE, TRUE, TRUE));

        Quay quay2 = new Quay();
        quay2.setName(createEmbeddableString("quay2"));
        quay2.setAccessibilityAssessment(createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, TRUE));

        quayRepository.save(quay1);
        quayRepository.save(quay2);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setAccessibilityAssessment(createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, TRUE));
        stopPlace.getQuays().add(quay1);
        stopPlace.getQuays().add(quay2);

        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        assertThat(actualStopPlace.getAccessibilityAssessment()).isNull();
        for (Quay quay : stopPlace.getQuays()) {
            assertThat(quay.getAccessibilityAssessment()).isNotNull();
            assertThat(quay.getAccessibilityAssessment().getMobilityImpairedAccess()).isNotNull();
        }

        actualStopPlace.getQuays()
                .stream()
                .forEach(quay -> {
                    AccessibilityLimitation actualAccessibilityLimitation = quay.getAccessibilityAssessment().getLimitations().get(0);

                    assertThat(actualAccessibilityLimitation).isNotNull();

                    if (quay.getNetexId().equals(quay1.getNetexId())) {
                        assertThat(actualAccessibilityLimitation.getWheelchairAccess()).isEqualTo(PARTIAL);
                        assertThat(actualAccessibilityLimitation.getLiftFreeAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getEscalatorFreeAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getAudibleSignalsAvailable()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getStepFreeAccess()).isEqualTo(TRUE);
                        assertThat(quay.getAccessibilityAssessment().getMobilityImpairedAccess()).isEqualTo(PARTIAL);
                    } else if (quay.getNetexId().equals(quay2.getNetexId())) {
                        assertThat(actualAccessibilityLimitation.getWheelchairAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getLiftFreeAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getEscalatorFreeAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getAudibleSignalsAvailable()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getStepFreeAccess()).isEqualTo(TRUE);
                        assertThat(quay.getAccessibilityAssessment().getMobilityImpairedAccess()).isEqualTo(TRUE);
                    } else {
                        fail("StopPlace contains unknown Quay");
                    }
                });
    }


    @Test
    public void persistStopPlaceWithDifferentAccessibilityAssessmentLimitationOneNull() {

        Quay quay1 = new Quay();
        quay1.setName(createEmbeddableString("quay1"));
        quay1.setAccessibilityAssessment(createAccessibilityAssessment(PARTIAL, TRUE, TRUE, TRUE, TRUE));

        Quay quay2 = new Quay();
        quay2.setName(createEmbeddableString("quay2"));
        quay2.setAccessibilityAssessment(null);

        quayRepository.save(quay1);
        quayRepository.save(quay2);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setAccessibilityAssessment(createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, TRUE));
        stopPlace.getQuays().add(quay1);
        stopPlace.getQuays().add(quay2);

        stopPlace = stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        assertThat(actualStopPlace.getAccessibilityAssessment()).isNull();
        for (Quay quay : stopPlace.getQuays()) {
            assertThat(quay.getAccessibilityAssessment()).isNotNull();
            assertThat(quay.getAccessibilityAssessment().getMobilityImpairedAccess()).isNotNull();
        }

        actualStopPlace.getQuays()
                .stream()
                .forEach(quay -> {
                    AccessibilityLimitation actualAccessibilityLimitation = quay.getAccessibilityAssessment().getLimitations().get(0);

                    assertThat(actualAccessibilityLimitation).isNotNull();

                    if (quay.getNetexId().equals(quay1.getNetexId())) {
                        assertThat(actualAccessibilityLimitation.getWheelchairAccess()).isEqualTo(PARTIAL);
                        assertThat(actualAccessibilityLimitation.getLiftFreeAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getEscalatorFreeAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getAudibleSignalsAvailable()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getStepFreeAccess()).isEqualTo(TRUE);
                        assertThat(quay.getAccessibilityAssessment().getMobilityImpairedAccess()).isEqualTo(PARTIAL);
                    } else if (quay.getNetexId().equals(quay2.getNetexId())) {
                        assertThat(actualAccessibilityLimitation.getWheelchairAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getLiftFreeAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getEscalatorFreeAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getAudibleSignalsAvailable()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getStepFreeAccess()).isEqualTo(TRUE);
                        assertThat(quay.getAccessibilityAssessment().getMobilityImpairedAccess()).isEqualTo(TRUE);
                    } else {
                        fail("StopPlace contains unknown Quay");
                    }
                });
    }


    @Test
    public void persistStopPlaceWithUnknownAccessibilityAssessmentLimitation() {

        Quay quay1 = new Quay();
        quay1.setName(createEmbeddableString("quay1"));
        quay1.setAccessibilityAssessment(createAccessibilityAssessment(TRUE, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN));

        Quay quay2 = new Quay();
        quay2.setName(createEmbeddableString("quay2"));
        quay2.setAccessibilityAssessment(createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, TRUE));

        quayRepository.save(quay1);
        quayRepository.save(quay2);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setAccessibilityAssessment(createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, TRUE));

        stopPlace.getQuays().add(quay1);
        stopPlace.getQuays().add(quay2);

        stopPlaceVersionedSaverService.saveNewVersion(stopPlace);

        StopPlace actualStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(stopPlace.getNetexId());

        assertThat(actualStopPlace.getAccessibilityAssessment()).isNull();
        for (Quay quay : stopPlace.getQuays()) {
            assertThat(quay.getAccessibilityAssessment()).isNotNull();
            assertThat(quay.getAccessibilityAssessment().getMobilityImpairedAccess()).isNotNull();
        }

        actualStopPlace.getQuays()
                .stream()
                .forEach(quay -> {
                    AccessibilityLimitation actualAccessibilityLimitation = quay.getAccessibilityAssessment().getLimitations().get(0);

                    assertThat(actualAccessibilityLimitation).isNotNull();

                    if (quay.getNetexId().equals(quay1.getNetexId())) {
                        assertThat(actualAccessibilityLimitation.getWheelchairAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getLiftFreeAccess()).isEqualTo(UNKNOWN);
                        assertThat(actualAccessibilityLimitation.getEscalatorFreeAccess()).isEqualTo(UNKNOWN);
                        assertThat(actualAccessibilityLimitation.getAudibleSignalsAvailable()).isEqualTo(UNKNOWN);
                        assertThat(actualAccessibilityLimitation.getStepFreeAccess()).isEqualTo(UNKNOWN);
                        assertThat(quay.getAccessibilityAssessment().getMobilityImpairedAccess()).isEqualTo(UNKNOWN);
                    } else if (quay.getNetexId().equals(quay2.getNetexId())) {
                        assertThat(actualAccessibilityLimitation.getWheelchairAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getLiftFreeAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getEscalatorFreeAccess()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getAudibleSignalsAvailable()).isEqualTo(TRUE);
                        assertThat(actualAccessibilityLimitation.getStepFreeAccess()).isEqualTo(TRUE);
                        assertThat(quay.getAccessibilityAssessment().getMobilityImpairedAccess()).isEqualTo(TRUE);
                    } else {
                        fail("StopPlace contains unknown Quay");
                    }
                });
    }

    private EmbeddableMultilingualString createEmbeddableString(String s) {
        return new EmbeddableMultilingualString(s);
    }


    protected static AccessibilityAssessment createAccessibilityAssessment(LimitationStatusEnumeration wheelchairAccess, LimitationStatusEnumeration liftFreeAccess, LimitationStatusEnumeration escalatorFreeAccess, LimitationStatusEnumeration audibleSignalsAvailable, LimitationStatusEnumeration stepFreeAccess) {
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

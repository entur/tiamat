package org.rutebanken.tiamat.model.listeners;

import org.junit.Test;
import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.service.MobilityImpairedAccessCalculator;

import static junit.framework.TestCase.assertEquals;
import static org.rutebanken.tiamat.model.LimitationStatusEnumeration.*;
import static org.rutebanken.tiamat.model.listeners.AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment;

public class MobilityImpairedAccessCalculatorTest {

    @Test
    public void testAllTrue() {
        AccessibilityAssessment accessibilityAssessment = createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(TRUE, accessibilityAssessment.getMobilityImpairedAccess());
    }

    @Test
    public void testAllFalse() {
        AccessibilityAssessment accessibilityAssessment = createAccessibilityAssessment(FALSE, FALSE, FALSE, FALSE, FALSE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(FALSE, accessibilityAssessment.getMobilityImpairedAccess());
    }

    @Test
    public void testAllUnknown() {
        AccessibilityAssessment accessibilityAssessment = createAccessibilityAssessment(UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(UNKNOWN, accessibilityAssessment.getMobilityImpairedAccess());
    }

    @Test
    public void testAllPartial() {
        AccessibilityAssessment accessibilityAssessment = createAccessibilityAssessment(PARTIAL, PARTIAL, PARTIAL, PARTIAL, PARTIAL);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());
    }

    @Test
    public void testOneTrue() {
        AccessibilityAssessment accessibilityAssessment;

        accessibilityAssessment = createAccessibilityAssessment(TRUE, FALSE, FALSE, FALSE, FALSE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = createAccessibilityAssessment(FALSE, TRUE, FALSE, FALSE, FALSE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = createAccessibilityAssessment(FALSE, FALSE, TRUE, FALSE, FALSE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = createAccessibilityAssessment(FALSE, FALSE, FALSE, TRUE, FALSE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = createAccessibilityAssessment(FALSE, FALSE, FALSE, FALSE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());
    }


    @Test
    public void testOneFalse() {
        AccessibilityAssessment accessibilityAssessment;

        accessibilityAssessment = createAccessibilityAssessment(FALSE, TRUE, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = createAccessibilityAssessment(TRUE, FALSE, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = createAccessibilityAssessment(TRUE, TRUE, FALSE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = createAccessibilityAssessment(TRUE, TRUE, TRUE, FALSE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, FALSE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());
    }

    @Test
    public void testOneUnknown() {
        AccessibilityAssessment accessibilityAssessment;

        accessibilityAssessment = createAccessibilityAssessment(UNKNOWN, TRUE, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(UNKNOWN, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = createAccessibilityAssessment(TRUE, UNKNOWN, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(UNKNOWN, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = createAccessibilityAssessment(TRUE, TRUE, UNKNOWN, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(UNKNOWN, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = createAccessibilityAssessment(TRUE, TRUE, TRUE, UNKNOWN, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(UNKNOWN, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, UNKNOWN);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(UNKNOWN, accessibilityAssessment.getMobilityImpairedAccess());
    }

    @Test
    public void testOnePartial() {
        AccessibilityAssessment accessibilityAssessment;

        accessibilityAssessment = createAccessibilityAssessment(PARTIAL, TRUE, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = createAccessibilityAssessment(TRUE, PARTIAL, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = createAccessibilityAssessment(TRUE, TRUE, PARTIAL, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = createAccessibilityAssessment(TRUE, TRUE, TRUE, PARTIAL, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, PARTIAL);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());
    }
}

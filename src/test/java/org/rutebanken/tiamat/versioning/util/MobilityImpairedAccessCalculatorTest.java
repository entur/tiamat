package org.rutebanken.tiamat.versioning.util;

import org.junit.Test;
import org.rutebanken.tiamat.model.AccessibilityAssessment;

import static junit.framework.TestCase.assertEquals;
import static org.rutebanken.tiamat.model.LimitationStatusEnumeration.*;

public class MobilityImpairedAccessCalculatorTest {

    @Test
    public void testAllTrue() {
        AccessibilityAssessment accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(TRUE, accessibilityAssessment.getMobilityImpairedAccess());
    }

    @Test
    public void testAllFalse() {
        AccessibilityAssessment accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(FALSE, FALSE, FALSE, FALSE, FALSE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(FALSE, accessibilityAssessment.getMobilityImpairedAccess());
    }

    @Test
    public void testAllUnknown() {
        AccessibilityAssessment accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(UNKNOWN, accessibilityAssessment.getMobilityImpairedAccess());
    }

    @Test
    public void testAllPartial() {
        AccessibilityAssessment accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(PARTIAL, PARTIAL, PARTIAL, PARTIAL, PARTIAL);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());
    }

    @Test
    public void testOneTrue() {
        AccessibilityAssessment accessibilityAssessment;

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, FALSE, FALSE, FALSE, FALSE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(FALSE, TRUE, FALSE, FALSE, FALSE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(FALSE, FALSE, TRUE, FALSE, FALSE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(FALSE, FALSE, FALSE, TRUE, FALSE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(FALSE, FALSE, FALSE, FALSE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());
    }


    @Test
    public void testOneFalse() {
        AccessibilityAssessment accessibilityAssessment;

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(FALSE, TRUE, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, FALSE, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, FALSE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, TRUE, FALSE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, FALSE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());
    }

    @Test
    public void testOneUnknown() {
        AccessibilityAssessment accessibilityAssessment;

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(UNKNOWN, TRUE, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(UNKNOWN, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, UNKNOWN, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(UNKNOWN, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, UNKNOWN, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(UNKNOWN, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, TRUE, UNKNOWN, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(UNKNOWN, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, UNKNOWN);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(UNKNOWN, accessibilityAssessment.getMobilityImpairedAccess());
    }

    @Test
    public void testOnePartial() {
        AccessibilityAssessment accessibilityAssessment;

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(PARTIAL, TRUE, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, PARTIAL, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, PARTIAL, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, TRUE, PARTIAL, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, PARTIAL);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());
    }
}

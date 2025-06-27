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

package org.rutebanken.tiamat.versioning.util;

import org.junit.Test;
import org.rutebanken.tiamat.model.AccessibilityAssessment;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.model.LimitationStatusEnumeration.FALSE;
import static org.rutebanken.tiamat.model.LimitationStatusEnumeration.PARTIAL;
import static org.rutebanken.tiamat.model.LimitationStatusEnumeration.TRUE;
import static org.rutebanken.tiamat.model.LimitationStatusEnumeration.UNKNOWN;

public class MobilityImpairedAccessCalculatorTest {

    @Test
    public void testAllTrue() {
        AccessibilityAssessment accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(TRUE, accessibilityAssessment.getMobilityImpairedAccess());
    }

    @Test
    public void testAllFalse() {
        AccessibilityAssessment accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(FALSE, FALSE, FALSE, FALSE, FALSE, FALSE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(FALSE, accessibilityAssessment.getMobilityImpairedAccess());
    }

    @Test
    public void testAllUnknown() {
        AccessibilityAssessment accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(UNKNOWN, accessibilityAssessment.getMobilityImpairedAccess());
    }

    @Test
    public void testAllPartial() {
        AccessibilityAssessment accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(PARTIAL, PARTIAL, PARTIAL, PARTIAL, PARTIAL, PARTIAL);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());
    }

    @Test
    public void testOneTrue() {
        AccessibilityAssessment accessibilityAssessment;

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, FALSE, FALSE, FALSE, FALSE, FALSE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(FALSE, TRUE, FALSE, FALSE, FALSE, FALSE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(FALSE, FALSE, TRUE, FALSE, FALSE, FALSE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(FALSE, FALSE, FALSE, TRUE, FALSE, FALSE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(FALSE, FALSE, FALSE, FALSE, TRUE, FALSE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(FALSE, FALSE, FALSE, FALSE, FALSE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());
    }


    @Test
    public void testOneFalse() {
        AccessibilityAssessment accessibilityAssessment;

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(FALSE, TRUE, TRUE, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, FALSE, TRUE, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, FALSE, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, TRUE, FALSE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, FALSE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, TRUE, FALSE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());
    }

    @Test
    public void testOneUnknown() {
        AccessibilityAssessment accessibilityAssessment;

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(UNKNOWN, TRUE, TRUE, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(UNKNOWN, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, UNKNOWN, TRUE, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(UNKNOWN, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, UNKNOWN, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(UNKNOWN, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, TRUE, UNKNOWN, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(UNKNOWN, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, UNKNOWN, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(UNKNOWN, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, TRUE, UNKNOWN);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(UNKNOWN, accessibilityAssessment.getMobilityImpairedAccess());
    }

    @Test
    public void testOnePartial() {
        AccessibilityAssessment accessibilityAssessment;

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(PARTIAL, TRUE, TRUE, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, PARTIAL, TRUE, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, PARTIAL, TRUE, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, TRUE, PARTIAL, TRUE, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, PARTIAL, TRUE);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());

        accessibilityAssessment = AccessibilityAssessmentOptimizerTest.createAccessibilityAssessment(TRUE, TRUE, TRUE, TRUE, TRUE, PARTIAL);
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertEquals(PARTIAL, accessibilityAssessment.getMobilityImpairedAccess());
    }

    @Test
    public void testDefaultUnknown() {
        AccessibilityAssessment accessibilityAssessment = new AccessibilityAssessment();
        accessibilityAssessment.setLimitations(new ArrayList<>());
        MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess(accessibilityAssessment);
        assertThat(accessibilityAssessment.getMobilityImpairedAccess()).isNotNull();
    }
}

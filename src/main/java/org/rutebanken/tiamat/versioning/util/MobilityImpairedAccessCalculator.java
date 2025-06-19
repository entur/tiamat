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

import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.AccessibilityLimitation;
import org.rutebanken.tiamat.model.LimitationStatusEnumeration;

import java.util.List;


public class MobilityImpairedAccessCalculator {
    public static void calculateAndSetMobilityImpairedAccess(AccessibilityAssessment assessment) {

        List<AccessibilityLimitation> limitations = assessment.getLimitations();
        if (limitations.size() > 0) {
            AccessibilityLimitation limitation = limitations.getFirst();

            // Partial is default unless criteria below are matched
            LimitationStatusEnumeration mobilityImpairedStatus = LimitationStatusEnumeration.PARTIAL;

            if (limitation.getWheelchairAccess() == LimitationStatusEnumeration.TRUE &&
                    limitation.getLiftFreeAccess() == LimitationStatusEnumeration.TRUE &&
                    limitation.getStepFreeAccess() == LimitationStatusEnumeration.TRUE &&
                    limitation.getAudibleSignalsAvailable() == LimitationStatusEnumeration.TRUE &&
                    limitation.getVisualSignsAvailable() == LimitationStatusEnumeration.TRUE &&
                    limitation.getEscalatorFreeAccess() == LimitationStatusEnumeration.TRUE) {
                // All true - return true
                mobilityImpairedStatus = LimitationStatusEnumeration.TRUE;
            } else if (limitation.getWheelchairAccess() == LimitationStatusEnumeration.FALSE &&
                    limitation.getLiftFreeAccess() == LimitationStatusEnumeration.FALSE &&
                    limitation.getStepFreeAccess() == LimitationStatusEnumeration.FALSE &&
                    limitation.getAudibleSignalsAvailable() == LimitationStatusEnumeration.FALSE &&
                    limitation.getVisualSignsAvailable() == LimitationStatusEnumeration.FALSE &&
                    limitation.getEscalatorFreeAccess() == LimitationStatusEnumeration.FALSE) {
                // All false - return false
                mobilityImpairedStatus = LimitationStatusEnumeration.FALSE;
            } else if (limitation.getWheelchairAccess() == LimitationStatusEnumeration.UNKNOWN |
                    limitation.getLiftFreeAccess() == LimitationStatusEnumeration.UNKNOWN |
                    limitation.getStepFreeAccess() == LimitationStatusEnumeration.UNKNOWN |
                    limitation.getAudibleSignalsAvailable() == LimitationStatusEnumeration.UNKNOWN |
                    limitation.getVisualSignsAvailable() == LimitationStatusEnumeration.UNKNOWN |
                    limitation.getEscalatorFreeAccess() == LimitationStatusEnumeration.UNKNOWN) {
                // At least one unknown - return unknown
                mobilityImpairedStatus = LimitationStatusEnumeration.UNKNOWN;
            } else if (limitation.getWheelchairAccess() == LimitationStatusEnumeration.PARTIAL |
                    limitation.getLiftFreeAccess() == LimitationStatusEnumeration.PARTIAL |
                    limitation.getStepFreeAccess() == LimitationStatusEnumeration.PARTIAL |
                    limitation.getAudibleSignalsAvailable() == LimitationStatusEnumeration.PARTIAL |
                    limitation.getVisualSignsAvailable() == LimitationStatusEnumeration.PARTIAL |
                    limitation.getEscalatorFreeAccess() == LimitationStatusEnumeration.PARTIAL) {
                // At least one partial - return partial
                mobilityImpairedStatus = LimitationStatusEnumeration.PARTIAL;
            }

            assessment.setMobilityImpairedAccess(mobilityImpairedStatus);
        } else {
            /**
             * Do not accept null values for mobility impaired access. This breaks netex export.
             */
            assessment.setMobilityImpairedAccess(LimitationStatusEnumeration.UNKNOWN);
        }
    }
}

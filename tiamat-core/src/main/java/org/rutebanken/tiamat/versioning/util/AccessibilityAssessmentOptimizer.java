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
import org.rutebanken.tiamat.model.StopPlace;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.rutebanken.tiamat.versioning.util.MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess;

@Service
public class AccessibilityAssessmentOptimizer {
    public void optimizeAccessibilityAssessments(StopPlace stopPlace) {

        List<AccessibilityAssessment> allQuayAccessibilityAssessments = new ArrayList<>();

        // Populate assessments on all quays that do not have assessments set
        if(stopPlace.getQuays() != null) {
            stopPlace.getQuays()
                    .stream()
                    .filter(quay -> quay.getAccessibilityAssessment() == null)
                    .forEach(quay -> quay.setAccessibilityAssessment(deepCopyAccessibilityAssessment(stopPlace.getAccessibilityAssessment())));

            // Collect all assessments
            stopPlace.getQuays()
                    .stream()
                    .forEach(quay -> allQuayAccessibilityAssessments.add(quay.getAccessibilityAssessment()));
        }

        if (!allQuayAccessibilityAssessments.isEmpty()) {
            //Assessments are set

            if (allAccessibilityAssessmentsAreEqual(allQuayAccessibilityAssessments)) {
                //All quays are equal
                //Set Assessment on StopPlace

                AccessibilityAssessment firstAccessibilityAssessment = deepCopyAccessibilityAssessment(allQuayAccessibilityAssessments.getFirst());

                if (stopPlace.getAccessibilityAssessment() != null) {
                    // Use existing Assessment instead, but update limitations
                    AccessibilityAssessment nextVersion = stopPlace.getAccessibilityAssessment();
                    nextVersion.setLimitations(firstAccessibilityAssessment.getLimitations());
                    firstAccessibilityAssessment = nextVersion;
                }

                stopPlace.setAccessibilityAssessment(firstAccessibilityAssessment);

                //Remove Assessment from Quays
                stopPlace.getQuays().forEach(quay -> quay.setAccessibilityAssessment(null));
            } else {
                // Assessments are different - remove from StopPlace
                stopPlace.setAccessibilityAssessment(null);
            }
        }

        if (stopPlace.getAccessibilityAssessment() != null) {
            calculateAndSetMobilityImpairedAccess(stopPlace.getAccessibilityAssessment());
        }
        if (stopPlace.getQuays() != null) {
            stopPlace.getQuays()
                    .stream()
                    .filter(quay -> quay.getAccessibilityAssessment() != null)
                    .forEach(quay -> calculateAndSetMobilityImpairedAccess(quay.getAccessibilityAssessment()));
        }
    }

    private AccessibilityAssessment deepCopyAccessibilityAssessment(AccessibilityAssessment accessibilityAssessment) {
        if (accessibilityAssessment == null) {
            return createDefaultAccessibilityAssessment();
        }
        final AccessibilityLimitation limitation = getAccessibilityLimitation(accessibilityAssessment);

        AccessibilityAssessment quayAssessment = new AccessibilityAssessment();
        quayAssessment.setMobilityImpairedAccess(accessibilityAssessment.getMobilityImpairedAccess());

        List<AccessibilityLimitation> limitations = new ArrayList<>();
        limitations.add(limitation);

        quayAssessment.setLimitations(limitations);
        return quayAssessment;
    }

    private static AccessibilityLimitation getAccessibilityLimitation(AccessibilityAssessment accessibilityAssessment) {
        AccessibilityLimitation stopLimitation = accessibilityAssessment.getLimitations().getFirst();

        AccessibilityLimitation limitation = new AccessibilityLimitation();
        limitation.setWheelchairAccess(stopLimitation.getWheelchairAccess());
        limitation.setAudibleSignalsAvailable(stopLimitation.getAudibleSignalsAvailable());
        limitation.setVisualSignsAvailable(stopLimitation.getVisualSignsAvailable());
        limitation.setLiftFreeAccess(stopLimitation.getLiftFreeAccess());
        limitation.setEscalatorFreeAccess(stopLimitation.getEscalatorFreeAccess());
        limitation.setStepFreeAccess(stopLimitation.getStepFreeAccess());
        return limitation;
    }

    private AccessibilityAssessment createDefaultAccessibilityAssessment() {
        AccessibilityAssessment assessment = new AccessibilityAssessment();
        List<AccessibilityLimitation> limitations = new ArrayList<>();

        AccessibilityLimitation limitation = new AccessibilityLimitation();
        limitation.setWheelchairAccess(LimitationStatusEnumeration.UNKNOWN);
        limitation.setAudibleSignalsAvailable(LimitationStatusEnumeration.UNKNOWN);
        limitation.setVisualSignsAvailable(LimitationStatusEnumeration.UNKNOWN);
        limitation.setLiftFreeAccess(LimitationStatusEnumeration.UNKNOWN);
        limitation.setEscalatorFreeAccess(LimitationStatusEnumeration.UNKNOWN);
        limitation.setStepFreeAccess(LimitationStatusEnumeration.UNKNOWN);
        limitations.add(limitation);

        assessment.setLimitations(limitations);

        return assessment;
    }

    private boolean allAccessibilityAssessmentsAreEqual(List<AccessibilityAssessment> quayAccessibilityAssessments) {
        if (quayAccessibilityAssessments.size() == 1) {
            return true;
        }

        AccessibilityAssessment first = quayAccessibilityAssessments.getFirst();
        List<AccessibilityLimitation> limitations = first.getLimitations();
        if (limitations != null && !limitations.isEmpty()) {
            AccessibilityLimitation limitation = limitations.getFirst();

            LimitationStatusEnumeration wheelchairAccess = limitation.getWheelchairAccess();
            LimitationStatusEnumeration audibleSignalsAvailable = limitation.getAudibleSignalsAvailable();
            LimitationStatusEnumeration visualSignsAvailable = limitation.getVisualSignsAvailable();
            LimitationStatusEnumeration liftFreeAccess = limitation.getLiftFreeAccess();
            LimitationStatusEnumeration escalatorFreeAccess = limitation.getEscalatorFreeAccess();
            LimitationStatusEnumeration stepFreeAccess = limitation.getStepFreeAccess();

            for (int i = 1; i < quayAccessibilityAssessments.size(); i++) {
                AccessibilityAssessment aa = quayAccessibilityAssessments.get(i);
                for (AccessibilityLimitation accessibilityLimitation : aa.getLimitations()) {
                    if (accessibilityLimitation.getWheelchairAccess() != wheelchairAccess |
                            accessibilityLimitation.getAudibleSignalsAvailable() != audibleSignalsAvailable |
                            accessibilityLimitation.getVisualSignsAvailable() != visualSignsAvailable |
                            accessibilityLimitation.getLiftFreeAccess() != liftFreeAccess |
                            accessibilityLimitation.getEscalatorFreeAccess() != escalatorFreeAccess |
                            accessibilityLimitation.getStepFreeAccess() != stepFreeAccess) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}

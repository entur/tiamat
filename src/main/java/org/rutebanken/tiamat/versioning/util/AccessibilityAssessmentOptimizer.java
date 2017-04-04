package org.rutebanken.tiamat.versioning.util;

import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.AccessibilityLimitation;
import org.rutebanken.tiamat.model.LimitationStatusEnumeration;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.rutebanken.tiamat.versioning.util.MobilityImpairedAccessCalculator.calculateAndSetMobilityImpairedAccess;

@Service
public class AccessibilityAssessmentOptimizer {

    @Autowired
    private VersionCreator versionCreator;

    public void optimizeAccessibilityAssessments(StopPlace stopPlace) {

        List<AccessibilityAssessment> allQuayAccessibilityAssessments = new ArrayList<>();

        // Populate assessments on all quays that do not have assessments set
        stopPlace.getQuays()
                .stream()
                .filter(quay -> quay.getAccessibilityAssessment() == null)
                .forEach(quay -> quay.setAccessibilityAssessment(deepCopyAccessibilityAssessment(stopPlace.getAccessibilityAssessment())));

        // Collect all assessments
        stopPlace.getQuays()
                .stream()
                .forEach(quay -> allQuayAccessibilityAssessments.add(quay.getAccessibilityAssessment()));

        if (!allQuayAccessibilityAssessments.isEmpty()) {
            //Assessments are set

            if (allAccessibilityAssessmentsAreEqual(allQuayAccessibilityAssessments)) {
                //All quays are equal
                //Set Assessment on StopPlace

                AccessibilityAssessment firstAccessibilityAssessment = deepCopyAccessibilityAssessment(allQuayAccessibilityAssessments.get(0));

                if (stopPlace.getAccessibilityAssessment() != null) {
                    // Use existing Assessment instead, but update limitations
                    AccessibilityAssessment nextVersion = stopPlace.getAccessibilityAssessment();
                    nextVersion.setLimitations(firstAccessibilityAssessment.getLimitations());
                    firstAccessibilityAssessment = versionCreator.createNextVersion(nextVersion, AccessibilityAssessment.class);
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
        AccessibilityLimitation stopLimitation = accessibilityAssessment.getLimitations().get(0);

        AccessibilityLimitation limitation = versionCreator.initiateFirstVersion(new AccessibilityLimitation(), AccessibilityLimitation.class);
        limitation.setWheelchairAccess(stopLimitation.getWheelchairAccess());
        limitation.setAudibleSignalsAvailable(stopLimitation.getAudibleSignalsAvailable());
        limitation.setLiftFreeAccess(stopLimitation.getLiftFreeAccess());
        limitation.setEscalatorFreeAccess(stopLimitation.getEscalatorFreeAccess());
        limitation.setStepFreeAccess(stopLimitation.getStepFreeAccess());

        AccessibilityAssessment quayAssessment = versionCreator.initiateFirstVersion(new AccessibilityAssessment(), AccessibilityAssessment.class);
        quayAssessment.setMobilityImpairedAccess(accessibilityAssessment.getMobilityImpairedAccess());

        List<AccessibilityLimitation> limitations = new ArrayList<>();
        limitations.add(limitation);

        quayAssessment.setLimitations(limitations);
        return quayAssessment;
    }

    private AccessibilityAssessment createDefaultAccessibilityAssessment() {
        AccessibilityAssessment assessment = new AccessibilityAssessment();
        List<AccessibilityLimitation> limitations = new ArrayList<>();

        AccessibilityLimitation limitation = new AccessibilityLimitation();
        limitation.setWheelchairAccess(LimitationStatusEnumeration.UNKNOWN);
        limitation.setAudibleSignalsAvailable(LimitationStatusEnumeration.UNKNOWN);
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

        AccessibilityAssessment first = quayAccessibilityAssessments.get(0);
        List<AccessibilityLimitation> limitations = first.getLimitations();
        if (limitations != null && !limitations.isEmpty()) {
            AccessibilityLimitation limitation = limitations.get(0);

            LimitationStatusEnumeration wheelchairAccess = limitation.getWheelchairAccess();
            LimitationStatusEnumeration audibleSignalsAvailable = limitation.getAudibleSignalsAvailable();
            LimitationStatusEnumeration liftFreeAccess = limitation.getLiftFreeAccess();
            LimitationStatusEnumeration escalatorFreeAccess = limitation.getEscalatorFreeAccess();
            LimitationStatusEnumeration stepFreeAccess = limitation.getStepFreeAccess();

            for (int i = 1; i < quayAccessibilityAssessments.size(); i++) {
                AccessibilityAssessment aa = quayAccessibilityAssessments.get(i);
                for (AccessibilityLimitation accessibilityLimitation : aa.getLimitations()) {
                    if (accessibilityLimitation.getWheelchairAccess() != wheelchairAccess |
                            accessibilityLimitation.getAudibleSignalsAvailable() != audibleSignalsAvailable |
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

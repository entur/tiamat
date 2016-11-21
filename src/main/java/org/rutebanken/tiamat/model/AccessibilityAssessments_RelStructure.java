package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class AccessibilityAssessments_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<AccessibilityAssessment> accessibilityAssessment;

    public List<AccessibilityAssessment> getAccessibilityAssessment() {
        if (accessibilityAssessment == null) {
            accessibilityAssessment = new ArrayList<AccessibilityAssessment>();
        }
        return this.accessibilityAssessment;
    }

}

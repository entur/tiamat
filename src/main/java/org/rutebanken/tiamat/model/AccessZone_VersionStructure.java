package org.rutebanken.tiamat.model;

public class AccessZone_VersionStructure
        extends Zone_VersionStructure {

    protected AccessibilityAssessment_VersionedChildStructure accessibilityAssessment;
    protected Boolean allAreasWheelchairAccessible;

    public AccessibilityAssessment_VersionedChildStructure getAccessibilityAssessment() {
        return accessibilityAssessment;
    }

    public void setAccessibilityAssessment(AccessibilityAssessment_VersionedChildStructure value) {
        this.accessibilityAssessment = value;
    }

    public Boolean isAllAreasWheelchairAccessible() {
        return allAreasWheelchairAccessible;
    }

    public void setAllAreasWheelchairAccessible(Boolean value) {
        this.allAreasWheelchairAccessible = value;
    }

}

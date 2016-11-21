

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class AccessibilityAssessments_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<AccessibilityAssessment> accessibilityAssessment;

    public List<AccessibilityAssessment> getAccessibilityAssessment() {
        if (accessibilityAssessment == null) {
            accessibilityAssessment = new ArrayList<AccessibilityAssessment>();
        }
        return this.accessibilityAssessment;
    }

}

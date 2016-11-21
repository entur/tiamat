

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class AssistanceService_VersionStructure
    extends LocalService_VersionStructure
{

    protected List<AssistanceFacilityEnumeration> assistanceFacilityList;
    protected AssistanceAvailabilityEnumeration assistanceAvailability;
    protected StaffingEnumeration staffing;
    protected List<AccessibilityToolEnumeration> accessibilityToolList;
    protected List<String> languages;
    protected Boolean accessibilityTrainedStaff;
    protected List<EmergencyServiceEnumeration> emergencyServiceList;
    protected List<SafetyFacilityEnumeration> safetyFacilityList;

    public List<AssistanceFacilityEnumeration> getAssistanceFacilityList() {
        if (assistanceFacilityList == null) {
            assistanceFacilityList = new ArrayList<AssistanceFacilityEnumeration>();
        }
        return this.assistanceFacilityList;
    }

    public AssistanceAvailabilityEnumeration getAssistanceAvailability() {
        return assistanceAvailability;
    }

    public void setAssistanceAvailability(AssistanceAvailabilityEnumeration value) {
        this.assistanceAvailability = value;
    }

    public StaffingEnumeration getStaffing() {
        return staffing;
    }

    public void setStaffing(StaffingEnumeration value) {
        this.staffing = value;
    }

    public List<AccessibilityToolEnumeration> getAccessibilityToolList() {
        if (accessibilityToolList == null) {
            accessibilityToolList = new ArrayList<AccessibilityToolEnumeration>();
        }
        return this.accessibilityToolList;
    }

    public List<String> getLanguages() {
        if (languages == null) {
            languages = new ArrayList<String>();
        }
        return this.languages;
    }

    public Boolean isAccessibilityTrainedStaff() {
        return accessibilityTrainedStaff;
    }

    public void setAccessibilityTrainedStaff(Boolean value) {
        this.accessibilityTrainedStaff = value;
    }

    public List<EmergencyServiceEnumeration> getEmergencyServiceList() {
        if (emergencyServiceList == null) {
            emergencyServiceList = new ArrayList<EmergencyServiceEnumeration>();
        }
        return this.emergencyServiceList;
    }

    public List<SafetyFacilityEnumeration> getSafetyFacilityList() {
        if (safetyFacilityList == null) {
            safetyFacilityList = new ArrayList<SafetyFacilityEnumeration>();
        }
        return this.safetyFacilityList;
    }

}

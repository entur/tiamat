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

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class AssistanceService_VersionStructure
        extends LocalService_VersionStructure {

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
            assistanceFacilityList = new ArrayList<>();
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
            accessibilityToolList = new ArrayList<>();
        }
        return this.accessibilityToolList;
    }

    public List<String> getLanguages() {
        if (languages == null) {
            languages = new ArrayList<>();
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
            emergencyServiceList = new ArrayList<>();
        }
        return this.emergencyServiceList;
    }

    public List<SafetyFacilityEnumeration> getSafetyFacilityList() {
        if (safetyFacilityList == null) {
            safetyFacilityList = new ArrayList<>();
        }
        return this.safetyFacilityList;
    }

}

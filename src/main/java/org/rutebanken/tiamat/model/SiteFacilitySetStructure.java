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


public class SiteFacilitySetStructure
        extends FacilitySet_VersionStructure {

    protected List<EmergencyServiceEnumeration> emergencyServiceList;
    protected List<HireFacilityEnumeration> hireFacilityList;
    protected List<LuggageLockerFacilityEnumeration> luggageLockerFacilityList;
    protected List<LuggageServiceFacilityEnumeration> luggageServiceFacilityList;
    protected List<MoneyFacilityEnumeration> moneyFacilityList;
    protected List<ParkingFacilityEnumeration> parkingFacilityList;
    protected StaffingEnumeration staffing;

    public List<EmergencyServiceEnumeration> getEmergencyServiceList() {
        if (emergencyServiceList == null) {
            emergencyServiceList = new ArrayList<EmergencyServiceEnumeration>();
        }
        return this.emergencyServiceList;
    }

    public List<HireFacilityEnumeration> getHireFacilityList() {
        if (hireFacilityList == null) {
            hireFacilityList = new ArrayList<HireFacilityEnumeration>();
        }
        return this.hireFacilityList;
    }

    public List<LuggageLockerFacilityEnumeration> getLuggageLockerFacilityList() {
        if (luggageLockerFacilityList == null) {
            luggageLockerFacilityList = new ArrayList<LuggageLockerFacilityEnumeration>();
        }
        return this.luggageLockerFacilityList;
    }

    public List<LuggageServiceFacilityEnumeration> getLuggageServiceFacilityList() {
        if (luggageServiceFacilityList == null) {
            luggageServiceFacilityList = new ArrayList<LuggageServiceFacilityEnumeration>();
        }
        return this.luggageServiceFacilityList;
    }

    public List<MoneyFacilityEnumeration> getMoneyFacilityList() {
        if (moneyFacilityList == null) {
            moneyFacilityList = new ArrayList<MoneyFacilityEnumeration>();
        }
        return this.moneyFacilityList;
    }

    public List<ParkingFacilityEnumeration> getParkingFacilityList() {
        if (parkingFacilityList == null) {
            parkingFacilityList = new ArrayList<ParkingFacilityEnumeration>();
        }
        return this.parkingFacilityList;
    }

    public StaffingEnumeration getStaffing() {
        return staffing;
    }

    public void setStaffing(StaffingEnumeration value) {
        this.staffing = value;
    }

}



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


public class SiteFacilitySetStructure
    extends FacilitySet_VersionStructure
{

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

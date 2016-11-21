package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class PassengerInformationEquipment_VersionStructure
        extends PassengerEquipment_VersionStructure {

    protected LogicalDisplayRefStructure logicalDisplayRef;
    protected StopPlaceReference stopPlaceRef;
    protected JAXBElement<? extends SiteComponentRefStructure> siteComponentRef;
    protected TypeOfPassengerInformationEquipmentRefStructure typeOfPassengerInformationEquipmentRef;
    protected List<PassengerInformationFacilityEnumeration> passengerInformationFacilityList;
    protected List<AccessibilityInfoFacilityEnumeration> accessibilityInfoFacilityList;

    public LogicalDisplayRefStructure getLogicalDisplayRef() {
        return logicalDisplayRef;
    }

    public void setLogicalDisplayRef(LogicalDisplayRefStructure value) {
        this.logicalDisplayRef = value;
    }

    public StopPlaceReference getStopPlaceRef() {
        return stopPlaceRef;
    }

    public void setStopPlaceRef(StopPlaceReference value) {
        this.stopPlaceRef = value;
    }

    public JAXBElement<? extends SiteComponentRefStructure> getSiteComponentRef() {
        return siteComponentRef;
    }

    public void setSiteComponentRef(JAXBElement<? extends SiteComponentRefStructure> value) {
        this.siteComponentRef = value;
    }

    public TypeOfPassengerInformationEquipmentRefStructure getTypeOfPassengerInformationEquipmentRef() {
        return typeOfPassengerInformationEquipmentRef;
    }

    public void setTypeOfPassengerInformationEquipmentRef(TypeOfPassengerInformationEquipmentRefStructure value) {
        this.typeOfPassengerInformationEquipmentRef = value;
    }

    public List<PassengerInformationFacilityEnumeration> getPassengerInformationFacilityList() {
        if (passengerInformationFacilityList == null) {
            passengerInformationFacilityList = new ArrayList<PassengerInformationFacilityEnumeration>();
        }
        return this.passengerInformationFacilityList;
    }

    public List<AccessibilityInfoFacilityEnumeration> getAccessibilityInfoFacilityList() {
        if (accessibilityInfoFacilityList == null) {
            accessibilityInfoFacilityList = new ArrayList<AccessibilityInfoFacilityEnumeration>();
        }
        return this.accessibilityInfoFacilityList;
    }

}

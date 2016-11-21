

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "logicalDisplayRef",
    "stopPlaceRef",
    "siteComponentRef",
    "typeOfPassengerInformationEquipmentRef",
    "passengerInformationFacilityList",
public class PassengerInformationEquipment_VersionStructure
    extends PassengerEquipment_VersionStructure
{

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



package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "units",
    "vehicleTypeRef",
    "equipmentRef",
public abstract class ActualVehicleEquipment_VersionStructure
    extends PassengerEquipment_VersionStructure
{

    protected BigInteger units;
    protected JAXBElement<? extends VehicleTypeRefStructure> vehicleTypeRef;
    protected JAXBElement<? extends EquipmentRefStructure> equipmentRef;
    protected AccessibilityAssessment accessibilityAssessment;

    public BigInteger getUnits() {
        return units;
    }

    public void setUnits(BigInteger value) {
        this.units = value;
    }

    public JAXBElement<? extends VehicleTypeRefStructure> getVehicleTypeRef() {
        return vehicleTypeRef;
    }

    public void setVehicleTypeRef(JAXBElement<? extends VehicleTypeRefStructure> value) {
        this.vehicleTypeRef = value;
    }

    public JAXBElement<? extends EquipmentRefStructure> getEquipmentRef() {
        return equipmentRef;
    }

    public void setEquipmentRef(JAXBElement<? extends EquipmentRefStructure> value) {
        this.equipmentRef = value;
    }

    public AccessibilityAssessment getAccessibilityAssessment() {
        return accessibilityAssessment;
    }

    public void setAccessibilityAssessment(AccessibilityAssessment value) {
        this.accessibilityAssessment = value;
    }

}

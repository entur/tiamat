

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "name",
    "equipmentRef",
    "units",
    "manufacturer",
public class VehicleEquipmentProfile_VersionStructure
    extends DataManagedObjectStructure
{

    protected MultilingualStringEntity name;
    protected JAXBElement<? extends EquipmentRefStructure> equipmentRef;
    protected BigInteger units;
    protected MultilingualStringEntity manufacturer;
    protected PurposeOfEquipmentProfileRefStructure purposeOfEquipmentProfileRef;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public JAXBElement<? extends EquipmentRefStructure> getEquipmentRef() {
        return equipmentRef;
    }

    public void setEquipmentRef(JAXBElement<? extends EquipmentRefStructure> value) {
        this.equipmentRef = value;
    }

    public BigInteger getUnits() {
        return units;
    }

    public void setUnits(BigInteger value) {
        this.units = value;
    }

    public MultilingualStringEntity getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(MultilingualStringEntity value) {
        this.manufacturer = value;
    }

    public PurposeOfEquipmentProfileRefStructure getPurposeOfEquipmentProfileRef() {
        return purposeOfEquipmentProfileRef;
    }

    public void setPurposeOfEquipmentProfileRef(PurposeOfEquipmentProfileRefStructure value) {
        this.purposeOfEquipmentProfileRef = value;
    }

}



package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class CycleStorageEquipment_VersionStructure
    extends PlaceEquipment_VersionStructure
{

    protected BigInteger numberOfSpaces;
    protected CycleStorageEnumeration cycleStorageType;
    protected Boolean cage;
    protected Boolean covered;

    public BigInteger getNumberOfSpaces() {
        return numberOfSpaces;
    }

    public void setNumberOfSpaces(BigInteger value) {
        this.numberOfSpaces = value;
    }

    public CycleStorageEnumeration getCycleStorageType() {
        return cycleStorageType;
    }

    public void setCycleStorageType(CycleStorageEnumeration value) {
        this.cycleStorageType = value;
    }

    public Boolean isCage() {
        return cage;
    }

    public void setCage(Boolean value) {
        this.cage = value;
    }

    public Boolean isCovered() {
        return covered;
    }

    public void setCovered(Boolean value) {
        this.covered = value;
    }

}

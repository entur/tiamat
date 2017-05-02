package org.rutebanken.tiamat.model;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.math.BigInteger;

@MappedSuperclass
public class CycleStorageEquipment_VersionStructure
        extends PlaceEquipment_VersionStructure {

    protected BigInteger numberOfSpaces;
    protected CycleStorageEnumeration cycleStorageType;
    @Transient
    protected Boolean cage;
    @Transient
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

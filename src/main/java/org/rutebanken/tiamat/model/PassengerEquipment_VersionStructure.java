package org.rutebanken.tiamat.model;

public abstract class PassengerEquipment_VersionStructure
        extends InstalledEquipment_VersionStructure {

    protected Boolean fixed;

    public Boolean isFixed() {
        return fixed;
    }

    public void setFixed(Boolean value) {
        this.fixed = value;
    }

}

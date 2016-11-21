package org.rutebanken.tiamat.model;

public class PlaceLighting_VersionStructure
        extends AccessEquipment_VersionStructure {

    protected LightingEnumeration lighting;
    protected Boolean alwaysLit;

    public LightingEnumeration getLighting() {
        return lighting;
    }

    public void setLighting(LightingEnumeration value) {
        this.lighting = value;
    }

    public Boolean isAlwaysLit() {
        return alwaysLit;
    }

    public void setAlwaysLit(Boolean value) {
        this.alwaysLit = value;
    }

}

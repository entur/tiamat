package org.rutebanken.tiamat.model;

public class RoughSurfaceStructure
        extends AccessEquipment_VersionStructure {

    protected SurfaceTypeEnumeration surfaceType;
    protected Boolean suitableForCycles;

    public SurfaceTypeEnumeration getSurfaceType() {
        return surfaceType;
    }

    public void setSurfaceType(SurfaceTypeEnumeration value) {
        this.surfaceType = value;
    }

    public Boolean isSuitableForCycles() {
        return suitableForCycles;
    }

    public void setSuitableForCycles(Boolean value) {
        this.suitableForCycles = value;
    }

}

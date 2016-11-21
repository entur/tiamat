package org.rutebanken.tiamat.model;

public class CompoundTrain_VersionStructure
        extends VehicleType_VersionStructure {

    protected TrainsInCompoundTrain_RelStructure components;

    public TrainsInCompoundTrain_RelStructure getComponents() {
        return components;
    }

    public void setComponents(TrainsInCompoundTrain_RelStructure value) {
        this.components = value;
    }

}

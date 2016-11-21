package org.rutebanken.tiamat.model;

public abstract class VehicleRequirement_VersionStructure
        extends DataManagedObjectStructure {

    protected MultilingualStringEntity name;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

}

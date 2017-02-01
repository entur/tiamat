package org.rutebanken.tiamat.model;

public class FacilityRequirement_VersionStructure
        extends VehicleRequirement_VersionStructure {

    protected ServiceFacilitySets_RelStructure facilitySets;

    public ServiceFacilitySets_RelStructure getFacilitySets() {
        return facilitySets;
    }

    public void setFacilitySets(ServiceFacilitySets_RelStructure value) {
        this.facilitySets = value;
    }

}

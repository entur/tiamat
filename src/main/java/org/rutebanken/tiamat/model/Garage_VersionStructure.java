package org.rutebanken.tiamat.model;

public class Garage_VersionStructure
        extends AddressablePlace_VersionStructure {

    protected GaragePoints_RelStructure garagePoints;
    protected CrewBaseRefs_RelStructure crewBases;

    public GaragePoints_RelStructure getGaragePoints() {
        return garagePoints;
    }

    public void setGaragePoints(GaragePoints_RelStructure value) {
        this.garagePoints = value;
    }

    public CrewBaseRefs_RelStructure getCrewBases() {
        return crewBases;
    }

    public void setCrewBases(CrewBaseRefs_RelStructure value) {
        this.crewBases = value;
    }

}

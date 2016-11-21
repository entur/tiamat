package org.rutebanken.tiamat.model;

public class ReliefPoint_VersionStructure
        extends TimingPoint_VersionStructure {

    protected CrewBaseRefStructure crewBaseRef;

    public CrewBaseRefStructure getCrewBaseRef() {
        return crewBaseRef;
    }

    public void setCrewBaseRef(CrewBaseRefStructure value) {
        this.crewBaseRef = value;
    }

}

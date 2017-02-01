package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class CrewBaseRefs_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<CrewBaseRefStructure> crewBaseRef;

    public List<CrewBaseRefStructure> getCrewBaseRef() {
        if (crewBaseRef == null) {
            crewBaseRef = new ArrayList<CrewBaseRefStructure>();
        }
        return this.crewBaseRef;
    }

}

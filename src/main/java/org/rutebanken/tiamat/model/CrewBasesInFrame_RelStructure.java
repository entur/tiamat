package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class CrewBasesInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<CrewBase> crewBase;

    public List<CrewBase> getCrewBase() {
        if (crewBase == null) {
            crewBase = new ArrayList<CrewBase>();
        }
        return this.crewBase;
    }

}

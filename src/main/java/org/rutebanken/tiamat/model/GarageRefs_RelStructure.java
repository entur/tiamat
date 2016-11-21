package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class GarageRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<GarageRefStructure> garageRef;

    public List<GarageRefStructure> getGarageRef() {
        if (garageRef == null) {
            garageRef = new ArrayList<GarageRefStructure>();
        }
        return this.garageRef;
    }

}

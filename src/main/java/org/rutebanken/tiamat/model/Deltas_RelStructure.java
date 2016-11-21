package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class Deltas_RelStructure {

    protected List<DeltaStructure> delta;

    public List<DeltaStructure> getDelta() {
        if (delta == null) {
            delta = new ArrayList<DeltaStructure>();
        }
        return this.delta;
    }

}

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class StopAreaRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<StopAreaRefStructure> stopAreaRef;

    public List<StopAreaRefStructure> getStopAreaRef() {
        if (stopAreaRef == null) {
            stopAreaRef = new ArrayList<StopAreaRefStructure>();
        }
        return this.stopAreaRef;
    }

}

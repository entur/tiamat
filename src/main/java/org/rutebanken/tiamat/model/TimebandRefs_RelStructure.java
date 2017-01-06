package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TimebandRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<TimebandRefStructure> timebandRef;

    public List<TimebandRefStructure> getTimebandRef() {
        if (timebandRef == null) {
            timebandRef = new ArrayList<TimebandRefStructure>();
        }
        return this.timebandRef;
    }

}

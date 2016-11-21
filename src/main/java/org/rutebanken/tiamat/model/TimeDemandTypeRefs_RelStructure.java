package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TimeDemandTypeRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<TimeDemandTypeRefStructure> timeDemandTypeRef;

    public List<TimeDemandTypeRefStructure> getTimeDemandTypeRef() {
        if (timeDemandTypeRef == null) {
            timeDemandTypeRef = new ArrayList<TimeDemandTypeRefStructure>();
        }
        return this.timeDemandTypeRef;
    }

}

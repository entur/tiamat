package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class DayTypeRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<DayTypeRefStructure> dayTypeRef;

    public List<DayTypeRefStructure> getDayTypeRef() {
        if (dayTypeRef == null) {
            dayTypeRef = new ArrayList<DayTypeRefStructure>();
        }
        return this.dayTypeRef;
    }

}

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class FlexibleLineRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<FlexibleLineRefStructure> flexibleLineRef;

    public List<FlexibleLineRefStructure> getFlexibleLineRef() {
        if (flexibleLineRef == null) {
            flexibleLineRef = new ArrayList<FlexibleLineRefStructure>();
        }
        return this.flexibleLineRef;
    }

}

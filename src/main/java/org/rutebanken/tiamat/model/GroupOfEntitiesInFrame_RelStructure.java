package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class GroupOfEntitiesInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<GeneralGroupOfEntities> generalGroupOfEntities;

    public List<GeneralGroupOfEntities> getGeneralGroupOfEntities() {
        if (generalGroupOfEntities == null) {
            generalGroupOfEntities = new ArrayList<GeneralGroupOfEntities>();
        }
        return this.generalGroupOfEntities;
    }

}

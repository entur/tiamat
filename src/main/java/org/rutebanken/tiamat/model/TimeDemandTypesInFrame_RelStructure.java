package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TimeDemandTypesInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<TimeDemandType> timeDemandType;

    public List<TimeDemandType> getTimeDemandType() {
        if (timeDemandType == null) {
            timeDemandType = new ArrayList<TimeDemandType>();
        }
        return this.timeDemandType;
    }

}

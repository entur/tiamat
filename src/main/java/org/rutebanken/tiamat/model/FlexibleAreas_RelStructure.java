package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class FlexibleAreas_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> flexibleAreaRefOrFlexibleArea;

    public List<Object> getFlexibleAreaRefOrFlexibleArea() {
        if (flexibleAreaRefOrFlexibleArea == null) {
            flexibleAreaRefOrFlexibleArea = new ArrayList<Object>();
        }
        return this.flexibleAreaRefOrFlexibleArea;
    }

}

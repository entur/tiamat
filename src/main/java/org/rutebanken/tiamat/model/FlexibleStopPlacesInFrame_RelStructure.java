package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class FlexibleStopPlacesInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<FlexibleStopPlace> flexibleStopPlace;

    public List<FlexibleStopPlace> getFlexibleStopPlace() {
        if (flexibleStopPlace == null) {
            flexibleStopPlace = new ArrayList<FlexibleStopPlace>();
        }
        return this.flexibleStopPlace;
    }

}

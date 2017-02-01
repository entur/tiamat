package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class FlexibleStopPlaces_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> flexibleStopPlaceRefOrFlexibleStopPlace;

    public List<Object> getFlexibleStopPlaceRefOrFlexibleStopPlace() {
        if (flexibleStopPlaceRefOrFlexibleStopPlace == null) {
            flexibleStopPlaceRefOrFlexibleStopPlace = new ArrayList<Object>();
        }
        return this.flexibleStopPlaceRefOrFlexibleStopPlace;
    }

}

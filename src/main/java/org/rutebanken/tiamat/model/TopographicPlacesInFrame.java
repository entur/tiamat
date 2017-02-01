package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TopographicPlacesInFrame
        extends ContainmentAggregationStructure {

    protected List<TopographicPlace> topographicPlace;

    public List<TopographicPlace> getTopographicPlace() {
        if (topographicPlace == null) {
            topographicPlace = new ArrayList<TopographicPlace>();
        }
        return this.topographicPlace;
    }

}

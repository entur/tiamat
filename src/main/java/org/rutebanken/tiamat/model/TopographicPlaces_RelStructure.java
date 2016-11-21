package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TopographicPlaces_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> topographicPlaceRefAndTopographicPlace;

    public List<Object> getTopographicPlaceRefAndTopographicPlace() {
        if (topographicPlaceRefAndTopographicPlace == null) {
            topographicPlaceRefAndTopographicPlace = new ArrayList<Object>();
        }
        return this.topographicPlaceRefAndTopographicPlace;
    }

}

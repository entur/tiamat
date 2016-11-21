package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class Accommodations_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> accommodationRefOrAccommodation;

    public List<Object> getAccommodationRefOrAccommodation() {
        if (accommodationRefOrAccommodation == null) {
            accommodationRefOrAccommodation = new ArrayList<Object>();
        }
        return this.accommodationRefOrAccommodation;
    }

}

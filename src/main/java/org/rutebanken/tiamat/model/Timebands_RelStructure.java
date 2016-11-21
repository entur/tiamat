package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class Timebands_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> timebandRefOrTimeband;

    public List<Object> getTimebandRefOrTimeband() {
        if (timebandRefOrTimeband == null) {
            timebandRefOrTimeband = new ArrayList<Object>();
        }
        return this.timebandRefOrTimeband;
    }

}

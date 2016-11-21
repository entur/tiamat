package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class OperatingDays_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> operatingDayRefOrOperatingDay;

    public List<Object> getOperatingDayRefOrOperatingDay() {
        if (operatingDayRefOrOperatingDay == null) {
            operatingDayRefOrOperatingDay = new ArrayList<Object>();
        }
        return this.operatingDayRefOrOperatingDay;
    }

}

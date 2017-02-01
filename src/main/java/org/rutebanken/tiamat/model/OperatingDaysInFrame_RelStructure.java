package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class OperatingDaysInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<OperatingDay> operatingDay;

    public List<OperatingDay> getOperatingDay() {
        if (operatingDay == null) {
            operatingDay = new ArrayList<OperatingDay>();
        }
        return this.operatingDay;
    }

}

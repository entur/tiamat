package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class OperatingPeriods_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> operatingPeriodRefOrOperatingPeriodOrUicOperatingPeriod;

    public List<Object> getOperatingPeriodRefOrOperatingPeriodOrUicOperatingPeriod() {
        if (operatingPeriodRefOrOperatingPeriodOrUicOperatingPeriod == null) {
            operatingPeriodRefOrOperatingPeriodOrUicOperatingPeriod = new ArrayList<Object>();
        }
        return this.operatingPeriodRefOrOperatingPeriodOrUicOperatingPeriod;
    }

}

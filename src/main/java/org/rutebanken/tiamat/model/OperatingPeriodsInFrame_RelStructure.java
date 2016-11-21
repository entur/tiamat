package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class OperatingPeriodsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<OperatingPeriod_VersionStructure> operatingPeriodOrUicOperatingPeriod;

    public List<OperatingPeriod_VersionStructure> getOperatingPeriodOrUicOperatingPeriod() {
        if (operatingPeriodOrUicOperatingPeriod == null) {
            operatingPeriodOrUicOperatingPeriod = new ArrayList<OperatingPeriod_VersionStructure>();
        }
        return this.operatingPeriodOrUicOperatingPeriod;
    }

}

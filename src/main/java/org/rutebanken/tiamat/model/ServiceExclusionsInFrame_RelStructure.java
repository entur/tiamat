package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ServiceExclusionsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<ServiceExclusion> serviceExclusion;

    public List<ServiceExclusion> getServiceExclusion() {
        if (serviceExclusion == null) {
            serviceExclusion = new ArrayList<ServiceExclusion>();
        }
        return this.serviceExclusion;
    }

}

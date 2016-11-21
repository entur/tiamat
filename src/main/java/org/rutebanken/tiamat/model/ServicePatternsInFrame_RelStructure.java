package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ServicePatternsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> servicePatternOrJourneyPatternView;

    public List<Object> getServicePatternOrJourneyPatternView() {
        if (servicePatternOrJourneyPatternView == null) {
            servicePatternOrJourneyPatternView = new ArrayList<Object>();
        }
        return this.servicePatternOrJourneyPatternView;
    }

}

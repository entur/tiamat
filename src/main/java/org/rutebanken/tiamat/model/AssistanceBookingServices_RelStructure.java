package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class AssistanceBookingServices_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> assistanceBookingServiceRefOrAssistanceBookingService;

    public List<Object> getAssistanceBookingServiceRefOrAssistanceBookingService() {
        if (assistanceBookingServiceRefOrAssistanceBookingService == null) {
            assistanceBookingServiceRefOrAssistanceBookingService = new ArrayList<Object>();
        }
        return this.assistanceBookingServiceRefOrAssistanceBookingService;
    }

}

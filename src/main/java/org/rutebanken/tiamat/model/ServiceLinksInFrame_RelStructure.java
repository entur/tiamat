package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ServiceLinksInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<ServiceLink> serviceLink;

    public List<ServiceLink> getServiceLink() {
        if (serviceLink == null) {
            serviceLink = new ArrayList<ServiceLink>();
        }
        return this.serviceLink;
    }

}

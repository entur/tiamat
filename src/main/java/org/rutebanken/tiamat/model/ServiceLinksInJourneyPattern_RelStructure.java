package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ServiceLinksInJourneyPattern_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<ServiceLinkInJourneyPattern_VersionedChildStructure> serviceLinkInJourneyPattern;

    public List<ServiceLinkInJourneyPattern_VersionedChildStructure> getServiceLinkInJourneyPattern() {
        if (serviceLinkInJourneyPattern == null) {
            serviceLinkInJourneyPattern = new ArrayList<ServiceLinkInJourneyPattern_VersionedChildStructure>();
        }
        return this.serviceLinkInJourneyPattern;
    }

}

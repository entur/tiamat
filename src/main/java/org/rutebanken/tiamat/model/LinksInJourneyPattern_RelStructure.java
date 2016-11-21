package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class LinksInJourneyPattern_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<LinkInLinkSequence_VersionedChildStructure> serviceLinkInJourneyPatternOrTimingLinkInJourneyPattern;

    public List<LinkInLinkSequence_VersionedChildStructure> getServiceLinkInJourneyPatternOrTimingLinkInJourneyPattern() {
        if (serviceLinkInJourneyPatternOrTimingLinkInJourneyPattern == null) {
            serviceLinkInJourneyPatternOrTimingLinkInJourneyPattern = new ArrayList<LinkInLinkSequence_VersionedChildStructure>();
        }
        return this.serviceLinkInJourneyPatternOrTimingLinkInJourneyPattern;
    }

}

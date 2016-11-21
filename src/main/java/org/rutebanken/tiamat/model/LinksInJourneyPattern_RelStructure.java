

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class LinksInJourneyPattern_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<LinkInLinkSequence_VersionedChildStructure> serviceLinkInJourneyPatternOrTimingLinkInJourneyPattern;

    public List<LinkInLinkSequence_VersionedChildStructure> getServiceLinkInJourneyPatternOrTimingLinkInJourneyPattern() {
        if (serviceLinkInJourneyPatternOrTimingLinkInJourneyPattern == null) {
            serviceLinkInJourneyPatternOrTimingLinkInJourneyPattern = new ArrayList<LinkInLinkSequence_VersionedChildStructure>();
        }
        return this.serviceLinkInJourneyPatternOrTimingLinkInJourneyPattern;
    }

}

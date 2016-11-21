

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class ServiceLinksInJourneyPattern_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<ServiceLinkInJourneyPattern_VersionedChildStructure> serviceLinkInJourneyPattern;

    public List<ServiceLinkInJourneyPattern_VersionedChildStructure> getServiceLinkInJourneyPattern() {
        if (serviceLinkInJourneyPattern == null) {
            serviceLinkInJourneyPattern = new ArrayList<ServiceLinkInJourneyPattern_VersionedChildStructure>();
        }
        return this.serviceLinkInJourneyPattern;
    }

}

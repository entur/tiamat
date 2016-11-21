

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class PointsInJourneyPattern_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<PointInLinkSequence_VersionedChildStructure> pointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern;

    public List<PointInLinkSequence_VersionedChildStructure> getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern() {
        if (pointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern == null) {
            pointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern = new ArrayList<PointInLinkSequence_VersionedChildStructure>();
        }
        return this.pointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern;
    }

}

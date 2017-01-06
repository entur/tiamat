package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class PointsInJourneyPattern_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<PointInLinkSequence_VersionedChildStructure> pointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern;

    public List<PointInLinkSequence_VersionedChildStructure> getPointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern() {
        if (pointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern == null) {
            pointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern = new ArrayList<PointInLinkSequence_VersionedChildStructure>();
        }
        return this.pointInJourneyPatternOrStopPointInJourneyPatternOrTimingPointInJourneyPattern;
    }

}

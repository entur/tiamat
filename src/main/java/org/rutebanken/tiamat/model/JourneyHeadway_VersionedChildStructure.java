package org.rutebanken.tiamat.model;

import javax.xml.datatype.Duration;


public class JourneyHeadway_VersionedChildStructure
        extends JourneyTiming_VersionedChildStructure {

    protected Duration scheduledHeadwayInterval;
    protected Duration minimumHeadwayInterval;
    protected Duration maximumHeadwayInterval;

    public Duration getScheduledHeadwayInterval() {
        return scheduledHeadwayInterval;
    }

    public void setScheduledHeadwayInterval(Duration value) {
        this.scheduledHeadwayInterval = value;
    }

    public Duration getMinimumHeadwayInterval() {
        return minimumHeadwayInterval;
    }

    public void setMinimumHeadwayInterval(Duration value) {
        this.minimumHeadwayInterval = value;
    }

    public Duration getMaximumHeadwayInterval() {
        return maximumHeadwayInterval;
    }

    public void setMaximumHeadwayInterval(Duration value) {
        this.maximumHeadwayInterval = value;
    }

}

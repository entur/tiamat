package org.rutebanken.tiamat.model;

import javax.xml.datatype.Duration;


public class TurnaroundTimeLimitTime_VersionedChildStructure
        extends JourneyTiming_VersionedChildStructure {

    protected Duration minimumDuration;
    protected Duration maximumDuration;

    public Duration getMinimumDuration() {
        return minimumDuration;
    }

    public void setMinimumDuration(Duration value) {
        this.minimumDuration = value;
    }

    public Duration getMaximumDuration() {
        return maximumDuration;
    }

    public void setMaximumDuration(Duration value) {
        this.maximumDuration = value;
    }

}



package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;


    "minimumDuration",
public class TurnaroundTimeLimitTime_VersionedChildStructure
    extends JourneyTiming_VersionedChildStructure
{

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

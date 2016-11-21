

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class ServiceExclusion_VersionStructure
    extends Assignment_VersionStructure
{

    protected JourneyPatternRefStructure excludingJourneyPatternRef;
    protected ScheduledStopPointRefStructure startPointRef;
    protected ScheduledStopPointRefStructure endPointRef;
    protected JourneyPatternRefs_RelStructure excludedJourneyPatternRefs;

    public JourneyPatternRefStructure getExcludingJourneyPatternRef() {
        return excludingJourneyPatternRef;
    }

    public void setExcludingJourneyPatternRef(JourneyPatternRefStructure value) {
        this.excludingJourneyPatternRef = value;
    }

    public ScheduledStopPointRefStructure getStartPointRef() {
        return startPointRef;
    }

    public void setStartPointRef(ScheduledStopPointRefStructure value) {
        this.startPointRef = value;
    }

    public ScheduledStopPointRefStructure getEndPointRef() {
        return endPointRef;
    }

    public void setEndPointRef(ScheduledStopPointRefStructure value) {
        this.endPointRef = value;
    }

    public JourneyPatternRefs_RelStructure getExcludedJourneyPatternRefs() {
        return excludedJourneyPatternRefs;
    }

    public void setExcludedJourneyPatternRefs(JourneyPatternRefs_RelStructure value) {
        this.excludedJourneyPatternRefs = value;
    }

}

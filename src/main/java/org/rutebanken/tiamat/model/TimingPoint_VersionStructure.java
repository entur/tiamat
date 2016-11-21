package org.rutebanken.tiamat.model;

import javax.xml.datatype.Duration;


public class TimingPoint_VersionStructure
        extends Point_VersionStructure {

    protected TimingPointStatusEnumeration timingPointStatus;
    protected Duration allowedForWaitTime;

    public TimingPointStatusEnumeration getTimingPointStatus() {
        return timingPointStatus;
    }

    public void setTimingPointStatus(TimingPointStatusEnumeration value) {
        this.timingPointStatus = value;
    }

    public Duration getAllowedForWaitTime() {
        return allowedForWaitTime;
    }

    public void setAllowedForWaitTime(Duration value) {
        this.allowedForWaitTime = value;
    }

}

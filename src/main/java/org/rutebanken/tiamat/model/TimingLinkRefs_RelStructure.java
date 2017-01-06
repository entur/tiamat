package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TimingLinkRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<Object> timingLinkRefOrTimingLinkRefByValue;

    public List<Object> getTimingLinkRefOrTimingLinkRefByValue() {
        if (timingLinkRefOrTimingLinkRefByValue == null) {
            timingLinkRefOrTimingLinkRefByValue = new ArrayList<Object>();
        }
        return this.timingLinkRefOrTimingLinkRefByValue;
    }

}

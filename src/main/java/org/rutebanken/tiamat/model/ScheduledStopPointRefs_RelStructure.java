package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;


public class ScheduledStopPointRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected JAXBElement<? extends ScheduledStopPointRefStructure> scheduledStopPointRef;

    public JAXBElement<? extends ScheduledStopPointRefStructure> getScheduledStopPointRef() {
        return scheduledStopPointRef;
    }

    public void setScheduledStopPointRef(JAXBElement<? extends ScheduledStopPointRefStructure> value) {
        this.scheduledStopPointRef = value;
    }

}

package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;


public class PointOnRoute_VersionedChildStructure
        extends PointInLinkSequence_VersionedChildStructure {

    protected JAXBElement<? extends PointRefStructure> pointRef;
    protected RouteLinkRefStructure onwardRouteLinkRef;

    public JAXBElement<? extends PointRefStructure> getPointRef() {
        return pointRef;
    }

    public void setPointRef(JAXBElement<? extends PointRefStructure> value) {
        this.pointRef = value;
    }

    public RouteLinkRefStructure getOnwardRouteLinkRef() {
        return onwardRouteLinkRef;
    }

    public void setOnwardRouteLinkRef(RouteLinkRefStructure value) {
        this.onwardRouteLinkRef = value;
    }

}

package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;


public class PointOnRoute_VersionedChildStructure
        extends PointInLinkSequence_VersionedChildStructure {

    protected JAXBElement<? extends PointRefStructure> pointRef;

    public JAXBElement<? extends PointRefStructure> getPointRef() {
        return pointRef;
    }

    public void setPointRef(JAXBElement<? extends PointRefStructure> value) {
        this.pointRef = value;
    }

}

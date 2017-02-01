package org.rutebanken.tiamat.model;

public class RoadElement_VersionStructure
        extends InfrastructureLink_VersionStructure {

    protected RoadPointRefStructure fromPointRef;
    protected RoadPointRefStructure toPointRef;

    public RoadPointRefStructure getFromPointRef() {
        return fromPointRef;
    }

    public void setFromPointRef(RoadPointRefStructure value) {
        this.fromPointRef = value;
    }

    public RoadPointRefStructure getToPointRef() {
        return toPointRef;
    }

    public void setToPointRef(RoadPointRefStructure value) {
        this.toPointRef = value;
    }

}

package org.rutebanken.tiamat.model;

public class RailwayElement_VersionStructure
        extends InfrastructureLink_VersionStructure {

    protected RailwayPointRefStructure fromPointRef;
    protected RailwayPointRefStructure toPointRef;

    public RailwayPointRefStructure getFromPointRef() {
        return fromPointRef;
    }

    public void setFromPointRef(RailwayPointRefStructure value) {
        this.fromPointRef = value;
    }

    public RailwayPointRefStructure getToPointRef() {
        return toPointRef;
    }

    public void setToPointRef(RailwayPointRefStructure value) {
        this.toPointRef = value;
    }

}

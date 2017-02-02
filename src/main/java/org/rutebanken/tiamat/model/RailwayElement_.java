package org.rutebanken.tiamat.model;

public class RailwayElement_
        extends InfrastructureLink_ {

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

package org.rutebanken.tiamat.model;

public class WireElement_
        extends InfrastructureLink_ {

    protected WirePointRefStructure fromPointRef;
    protected WirePointRefStructure toPointRef;

    public WirePointRefStructure getFromPointRef() {
        return fromPointRef;
    }

    public void setFromPointRef(WirePointRefStructure value) {
        this.fromPointRef = value;
    }

    public WirePointRefStructure getToPointRef() {
        return toPointRef;
    }

    public void setToPointRef(WirePointRefStructure value) {
        this.toPointRef = value;
    }

}

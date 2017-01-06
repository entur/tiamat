package org.rutebanken.tiamat.model;

public class RouteLink_VersionStructure
        extends Link_VersionStructure {

    protected RoutePointRefStructure fromPointRef;
    protected RoutePointRefStructure toPointRef;
    protected OperationalContextRefStructure operationalContextRef;

    public RoutePointRefStructure getFromPointRef() {
        return fromPointRef;
    }

    public void setFromPointRef(RoutePointRefStructure value) {
        this.fromPointRef = value;
    }

    public RoutePointRefStructure getToPointRef() {
        return toPointRef;
    }

    public void setToPointRef(RoutePointRefStructure value) {
        this.toPointRef = value;
    }

    public OperationalContextRefStructure getOperationalContextRef() {
        return operationalContextRef;
    }

    public void setOperationalContextRef(OperationalContextRefStructure value) {
        this.operationalContextRef = value;
    }

}

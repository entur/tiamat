package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;


public class Route_VersionStructure
        extends LinkSequence_VersionStructure {

    protected JAXBElement<? extends LineRefStructure> lineRef;
    protected DirectionTypeEnumeration directionType;
    protected DirectionRefStructure directionRef;
    protected PointsOnRoute_RelStructure pointsInSequence;
    protected RouteRefStructure inverseRouteRef;

    public JAXBElement<? extends LineRefStructure> getLineRef() {
        return lineRef;
    }

    public void setLineRef(JAXBElement<? extends LineRefStructure> value) {
        this.lineRef = value;
    }

    public DirectionTypeEnumeration getDirectionType() {
        return directionType;
    }

    public void setDirectionType(DirectionTypeEnumeration value) {
        this.directionType = value;
    }

    public DirectionRefStructure getDirectionRef() {
        return directionRef;
    }

    public void setDirectionRef(DirectionRefStructure value) {
        this.directionRef = value;
    }

    public PointsOnRoute_RelStructure getPointsInSequence() {
        return pointsInSequence;
    }

    public void setPointsInSequence(PointsOnRoute_RelStructure value) {
        this.pointsInSequence = value;
    }

    public RouteRefStructure getInverseRouteRef() {
        return inverseRouteRef;
    }

    public void setInverseRouteRef(RouteRefStructure value) {
        this.inverseRouteRef = value;
    }

}

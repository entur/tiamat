package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.math.BigDecimal;
import java.math.BigInteger;


public class PointOnLink_VersionedChildStructure
        extends VersionedChildStructure {

    protected MultilingualStringEntity name;
    protected LinkRefStructure linkRef;
    protected BigDecimal distanceFromStart;
    protected JAXBElement<? extends PointRefStructure> pointRef;
    protected JAXBElement<? extends Point_VersionStructure> point;
    protected BigInteger order;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public LinkRefStructure getLinkRef() {
        return linkRef;
    }

    public void setLinkRef(LinkRefStructure value) {
        this.linkRef = value;
    }

    public BigDecimal getDistanceFromStart() {
        return distanceFromStart;
    }

    public void setDistanceFromStart(BigDecimal value) {
        this.distanceFromStart = value;
    }

    public JAXBElement<? extends PointRefStructure> getPointRef() {
        return pointRef;
    }

    public void setPointRef(JAXBElement<? extends PointRefStructure> value) {
        this.pointRef = value;
    }

    public JAXBElement<? extends Point_VersionStructure> getPoint() {
        return point;
    }

    public void setPoint(JAXBElement<? extends Point_VersionStructure> value) {
        this.point = value;
    }

    public BigInteger getOrder() {
        return order;
    }

    public void setOrder(BigInteger value) {
        this.order = value;
    }

}

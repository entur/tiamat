

package org.rutebanken.tiamat.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "name",
    "linkRef",
    "distanceFromStart",
    "pointRef",
public class PointOnLink_VersionedChildStructure
    extends VersionedChildStructure
{

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

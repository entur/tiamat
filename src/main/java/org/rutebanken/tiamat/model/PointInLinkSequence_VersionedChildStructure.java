

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public abstract class PointInLinkSequence_VersionedChildStructure
    extends VersionedChildStructure
{

    protected JAXBElement<? extends LinkSequenceRefStructure> linkSequenceRef;
    protected Projections_RelStructure projections;
    protected BigInteger order;

    public JAXBElement<? extends LinkSequenceRefStructure> getLinkSequenceRef() {
        return linkSequenceRef;
    }

    public void setLinkSequenceRef(JAXBElement<? extends LinkSequenceRefStructure> value) {
        this.linkSequenceRef = value;
    }

    public Projections_RelStructure getProjections() {
        return projections;
    }

    public void setProjections(Projections_RelStructure value) {
        this.projections = value;
    }

    public BigInteger getOrder() {
        return order;
    }

    public void setOrder(BigInteger value) {
        this.order = value;
    }

}

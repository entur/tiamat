

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "fromPointRef",
public class WireElement_VersionStructure
    extends InfrastructureLink_VersionStructure
{

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

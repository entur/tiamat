

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "fromPointRef",
public class RoadElement_VersionStructure
    extends InfrastructureLink_VersionStructure
{

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

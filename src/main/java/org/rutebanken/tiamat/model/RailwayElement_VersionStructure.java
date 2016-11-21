

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "fromPointRef",
public class RailwayElement_VersionStructure
    extends InfrastructureLink_VersionStructure
{

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

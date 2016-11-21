

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class RouteLink_VersionStructure
    extends Link_VersionStructure
{

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

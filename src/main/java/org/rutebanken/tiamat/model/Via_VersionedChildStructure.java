

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


    "destinationDisplayRef",
    "destinationDisplayView",
    "name",
    "routePointRef",
public class Via_VersionedChildStructure
    extends VersionedChildStructure
{

    protected DestinationDisplayRefStructure destinationDisplayRef;
    protected DestinationDisplayView destinationDisplayView;
    protected MultilingualStringEntity name;
    protected RoutePointRefStructure routePointRef;
    protected ViaTypeEnumeration viaType;

    public DestinationDisplayRefStructure getDestinationDisplayRef() {
        return destinationDisplayRef;
    }

    public void setDestinationDisplayRef(DestinationDisplayRefStructure value) {
        this.destinationDisplayRef = value;
    }

    public DestinationDisplayView getDestinationDisplayView() {
        return destinationDisplayView;
    }

    public void setDestinationDisplayView(DestinationDisplayView value) {
        this.destinationDisplayView = value;
    }

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public RoutePointRefStructure getRoutePointRef() {
        return routePointRef;
    }

    public void setRoutePointRef(RoutePointRefStructure value) {
        this.routePointRef = value;
    }

    public ViaTypeEnumeration getViaType() {
        return viaType;
    }

    public void setViaType(ViaTypeEnumeration value) {
        this.viaType = value;
    }

}

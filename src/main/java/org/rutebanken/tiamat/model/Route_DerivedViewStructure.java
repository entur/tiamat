

package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class Route_DerivedViewStructure
    extends DerivedViewStructure
{

    protected RouteRefStructure routeRef;
    protected AllVehicleModesOfTransportEnumeration vehicleMode;
    protected MultilingualStringEntity name;
    protected JAXBElement<? extends LineRefStructure> lineRef;
    protected LineView lineView;
    protected DirectionView directionView;
    protected LinkSequenceProjectionRefStructure linkSequenceProjectionRef;

    public RouteRefStructure getRouteRef() {
        return routeRef;
    }

    public void setRouteRef(RouteRefStructure value) {
        this.routeRef = value;
    }

    public AllVehicleModesOfTransportEnumeration getVehicleMode() {
        return vehicleMode;
    }

    public void setVehicleMode(AllVehicleModesOfTransportEnumeration value) {
        this.vehicleMode = value;
    }

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public JAXBElement<? extends LineRefStructure> getLineRef() {
        return lineRef;
    }

    public void setLineRef(JAXBElement<? extends LineRefStructure> value) {
        this.lineRef = value;
    }

    public LineView getLineView() {
        return lineView;
    }

    public void setLineView(LineView value) {
        this.lineView = value;
    }

    public DirectionView getDirectionView() {
        return directionView;
    }

    public void setDirectionView(DirectionView value) {
        this.directionView = value;
    }

    public LinkSequenceProjectionRefStructure getLinkSequenceProjectionRef() {
        return linkSequenceProjectionRef;
    }

    public void setLinkSequenceProjectionRef(LinkSequenceProjectionRefStructure value) {
        this.linkSequenceProjectionRef = value;
    }

}

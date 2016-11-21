

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


public class AccessEndStructure {

    protected AllVehicleModesOfTransportEnumeration transportMode;
    protected PlaceRefStructure placeRef;
    protected PointRefStructure pointRef;

    public AllVehicleModesOfTransportEnumeration getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(AllVehicleModesOfTransportEnumeration value) {
        this.transportMode = value;
    }

    public PlaceRefStructure getPlaceRef() {
        return placeRef;
    }

    public void setPlaceRef(PlaceRefStructure value) {
        this.placeRef = value;
    }

    public PointRefStructure getPointRef() {
        return pointRef;
    }

    public void setPointRef(PointRefStructure value) {
        this.pointRef = value;
    }

}

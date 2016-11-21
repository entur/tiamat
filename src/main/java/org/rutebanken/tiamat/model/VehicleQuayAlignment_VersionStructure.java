

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "vehicleStoppingPlaceRef",
public class VehicleQuayAlignment_VersionStructure
    extends VersionedChildStructure
{

    protected VehicleStoppingPlaceRefStructure vehicleStoppingPlaceRef;
    protected QuayReference quayRef;

    public VehicleStoppingPlaceRefStructure getVehicleStoppingPlaceRef() {
        return vehicleStoppingPlaceRef;
    }

    public void setVehicleStoppingPlaceRef(VehicleStoppingPlaceRefStructure value) {
        this.vehicleStoppingPlaceRef = value;
    }

    public QuayReference getQuayRef() {
        return quayRef;
    }

    public void setQuayRef(QuayReference value) {
        this.quayRef = value;
    }

}

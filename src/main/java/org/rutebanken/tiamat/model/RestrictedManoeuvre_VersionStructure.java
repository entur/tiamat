

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public abstract class RestrictedManoeuvre_VersionStructure
    extends InfrastructureLinkRestriction_VersionStructure
{

    protected VehicleTypeRefStructure vehicleTypeRef;

    public VehicleTypeRefStructure getVehicleTypeRef() {
        return vehicleTypeRef;
    }

    public void setVehicleTypeRef(VehicleTypeRefStructure value) {
        this.vehicleTypeRef = value;
    }

}

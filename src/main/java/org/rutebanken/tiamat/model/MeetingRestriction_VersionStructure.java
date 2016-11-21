

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class MeetingRestriction_VersionStructure
    extends InfrastructureLinkRestriction_VersionStructure
{

    protected VehicleTypeRefStructure forVehicleTypeRef;
    protected VehicleTypeRefStructure againstVehicleTypeRef;

    public VehicleTypeRefStructure getForVehicleTypeRef() {
        return forVehicleTypeRef;
    }

    public void setForVehicleTypeRef(VehicleTypeRefStructure value) {
        this.forVehicleTypeRef = value;
    }

    public VehicleTypeRefStructure getAgainstVehicleTypeRef() {
        return againstVehicleTypeRef;
    }

    public void setAgainstVehicleTypeRef(VehicleTypeRefStructure value) {
        this.againstVehicleTypeRef = value;
    }

}

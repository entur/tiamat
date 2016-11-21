

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class VehicleTypeAtPoint_VersionStructure
    extends NetworkRestriction_VersionStructure
{

    protected VehicleTypeRefStructure forVehicleTypeRef;
    protected BigInteger capacity;

    public VehicleTypeRefStructure getForVehicleTypeRef() {
        return forVehicleTypeRef;
    }

    public void setForVehicleTypeRef(VehicleTypeRefStructure value) {
        this.forVehicleTypeRef = value;
    }

    public BigInteger getCapacity() {
        return capacity;
    }

    public void setCapacity(BigInteger value) {
        this.capacity = value;
    }

}

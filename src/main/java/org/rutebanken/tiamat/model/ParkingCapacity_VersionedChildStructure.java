

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class ParkingCapacity_VersionedChildStructure
    extends VersionedChildStructure
{

    protected SiteElementRefStructure parentRef;
    protected ParkingUserEnumeration parkingUserType;
    protected ParkingVehicleEnumeration parkingVehicleType;
    protected ParkingStayEnumeration parkingStayType;
    protected BigInteger numberOfSpaces;

    public SiteElementRefStructure getParentRef() {
        return parentRef;
    }

    public void setParentRef(SiteElementRefStructure value) {
        this.parentRef = value;
    }

    public ParkingUserEnumeration getParkingUserType() {
        return parkingUserType;
    }

    public void setParkingUserType(ParkingUserEnumeration value) {
        this.parkingUserType = value;
    }

    public ParkingVehicleEnumeration getParkingVehicleType() {
        return parkingVehicleType;
    }

    public void setParkingVehicleType(ParkingVehicleEnumeration value) {
        this.parkingVehicleType = value;
    }

    public ParkingStayEnumeration getParkingStayType() {
        return parkingStayType;
    }

    public void setParkingStayType(ParkingStayEnumeration value) {
        this.parkingStayType = value;
    }

    public BigInteger getNumberOfSpaces() {
        return numberOfSpaces;
    }

    public void setNumberOfSpaces(BigInteger value) {
        this.numberOfSpaces = value;
    }

}

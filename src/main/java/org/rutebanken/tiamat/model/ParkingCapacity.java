package org.rutebanken.tiamat.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigInteger;

@Entity
public class ParkingCapacity
        extends VersionedChildStructure {

    protected SiteElementRefStructure parentRef;
    @Enumerated(EnumType.STRING)
    protected ParkingUserEnumeration parkingUserType;
    @Enumerated(EnumType.STRING)
    protected ParkingVehicleEnumeration parkingVehicleType;
    @Enumerated(EnumType.STRING)
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

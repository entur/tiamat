package org.rutebanken.tiamat.model;

import java.math.BigDecimal;


public class ParkingBay_VersionStructure
        extends ParkingComponent_VersionStructure {

    protected ParkingVehicleEnumeration parkingVehicleType;
    protected BigDecimal length;
    protected BigDecimal width;
    protected BigDecimal height;
    protected Boolean rechargingAvailable;

    public ParkingVehicleEnumeration getParkingVehicleType() {
        return parkingVehicleType;
    }

    public void setParkingVehicleType(ParkingVehicleEnumeration value) {
        this.parkingVehicleType = value;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal value) {
        this.length = value;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal value) {
        this.width = value;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal value) {
        this.height = value;
    }

    public Boolean isRechargingAvailable() {
        return rechargingAvailable;
    }

    public void setRechargingAvailable(Boolean value) {
        this.rechargingAvailable = value;
    }

}

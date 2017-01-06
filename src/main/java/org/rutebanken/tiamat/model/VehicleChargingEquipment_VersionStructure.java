package org.rutebanken.tiamat.model;

public class VehicleChargingEquipment_VersionStructure
        extends PlaceEquipment_VersionStructure {

    protected Boolean freeRecharging;
    protected Boolean reservationRequired;
    protected String reservationUrl;

    public Boolean isFreeRecharging() {
        return freeRecharging;
    }

    public void setFreeRecharging(Boolean value) {
        this.freeRecharging = value;
    }

    public Boolean isReservationRequired() {
        return reservationRequired;
    }

    public void setReservationRequired(Boolean value) {
        this.reservationRequired = value;
    }

    public String getReservationUrl() {
        return reservationUrl;
    }

    public void setReservationUrl(String value) {
        this.reservationUrl = value;
    }

}



package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "freeRecharging",
    "reservationRequired",
public class VehicleChargingEquipment_VersionStructure
    extends PlaceEquipment_VersionStructure
{

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

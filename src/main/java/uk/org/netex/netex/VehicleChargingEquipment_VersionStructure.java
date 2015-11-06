//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.05 at 07:41:01 PM CET 
//


package uk.org.netex.netex;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Type for a VEHICLE CHARGING EQUIPMENT.
 * 
 * <p>Java class for VehicleChargingEquipment_VersionStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VehicleChargingEquipment_VersionStructure">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.netex.org.uk/netex}PlaceEquipment_VersionStructure">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.netex.org.uk/netex}VehicleChargingEquipmentGroup"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VehicleChargingEquipment_VersionStructure", propOrder = {
    "freeRecharging",
    "reservationRequired",
    "reservationUrl"
})
@XmlSeeAlso({
    VehicleChargingEquipment.class
})
public class VehicleChargingEquipment_VersionStructure
    extends PlaceEquipment_VersionStructure
{

    @XmlElement(name = "FreeRecharging")
    protected Boolean freeRecharging;
    @XmlElement(name = "ReservationRequired")
    protected Boolean reservationRequired;
    @XmlElement(name = "ReservationUrl")
    @XmlSchemaType(name = "anyURI")
    protected String reservationUrl;

    /**
     * Gets the value of the freeRecharging property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isFreeRecharging() {
        return freeRecharging;
    }

    /**
     * Sets the value of the freeRecharging property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setFreeRecharging(Boolean value) {
        this.freeRecharging = value;
    }

    /**
     * Gets the value of the reservationRequired property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isReservationRequired() {
        return reservationRequired;
    }

    /**
     * Sets the value of the reservationRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReservationRequired(Boolean value) {
        this.reservationRequired = value;
    }

    /**
     * Gets the value of the reservationUrl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReservationUrl() {
        return reservationUrl;
    }

    /**
     * Sets the value of the reservationUrl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReservationUrl(String value) {
        this.reservationUrl = value;
    }

}

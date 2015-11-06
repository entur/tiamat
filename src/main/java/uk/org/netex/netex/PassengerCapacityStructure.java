//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.05 at 07:41:01 PM CET 
//


package uk.org.netex.netex;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Capacity for a VEHICLE TYPE and Class.
 * 
 * <p>Java class for PassengerCapacityStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PassengerCapacityStructure">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.netex.org.uk/netex}DataManagedObjectStructure">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.netex.org.uk/netex}PassengerCapacityGroup"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PassengerCapacityStructure", propOrder = {
    "fareClass",
    "totalCapacity",
    "seatingCapacity",
    "standingCapacity",
    "specialPlaceCapacity",
    "pushchairCapacity",
    "wheelchairPlaceCapacity"
})
@XmlSeeAlso({
    PassengerCapacity.class
})
public class PassengerCapacityStructure
    extends DataManagedObjectStructure
{

    @XmlElement(name = "FareClass", defaultValue = "any")
    @XmlSchemaType(name = "NMTOKEN")
    protected FareClassEnumeration fareClass;
    @XmlElement(name = "TotalCapacity")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger totalCapacity;
    @XmlElement(name = "SeatingCapacity")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger seatingCapacity;
    @XmlElement(name = "StandingCapacity")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger standingCapacity;
    @XmlElement(name = "SpecialPlaceCapacity")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger specialPlaceCapacity;
    @XmlElement(name = "PushchairCapacity")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger pushchairCapacity;
    @XmlElement(name = "WheelchairPlaceCapacity")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger wheelchairPlaceCapacity;

    /**
     * Gets the value of the fareClass property.
     * 
     * @return
     *     possible object is
     *     {@link FareClassEnumeration }
     *     
     */
    public FareClassEnumeration getFareClass() {
        return fareClass;
    }

    /**
     * Sets the value of the fareClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link FareClassEnumeration }
     *     
     */
    public void setFareClass(FareClassEnumeration value) {
        this.fareClass = value;
    }

    /**
     * Gets the value of the totalCapacity property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTotalCapacity() {
        return totalCapacity;
    }

    /**
     * Sets the value of the totalCapacity property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTotalCapacity(BigInteger value) {
        this.totalCapacity = value;
    }

    /**
     * Gets the value of the seatingCapacity property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSeatingCapacity() {
        return seatingCapacity;
    }

    /**
     * Sets the value of the seatingCapacity property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSeatingCapacity(BigInteger value) {
        this.seatingCapacity = value;
    }

    /**
     * Gets the value of the standingCapacity property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getStandingCapacity() {
        return standingCapacity;
    }

    /**
     * Sets the value of the standingCapacity property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setStandingCapacity(BigInteger value) {
        this.standingCapacity = value;
    }

    /**
     * Gets the value of the specialPlaceCapacity property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSpecialPlaceCapacity() {
        return specialPlaceCapacity;
    }

    /**
     * Sets the value of the specialPlaceCapacity property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSpecialPlaceCapacity(BigInteger value) {
        this.specialPlaceCapacity = value;
    }

    /**
     * Gets the value of the pushchairCapacity property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPushchairCapacity() {
        return pushchairCapacity;
    }

    /**
     * Sets the value of the pushchairCapacity property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPushchairCapacity(BigInteger value) {
        this.pushchairCapacity = value;
    }

    /**
     * Gets the value of the wheelchairPlaceCapacity property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getWheelchairPlaceCapacity() {
        return wheelchairPlaceCapacity;
    }

    /**
     * Sets the value of the wheelchairPlaceCapacity property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setWheelchairPlaceCapacity(BigInteger value) {
        this.wheelchairPlaceCapacity = value;
    }

}

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.05 at 07:41:01 PM CET 
//


package uk.org.netex.netex;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Type for a Luggage Locker.
 * 
 * <p>Java class for LuggageLockerEquipment_VersionStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LuggageLockerEquipment_VersionStructure">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.netex.org.uk/netex}SiteEquipment_VersionStructure">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.netex.org.uk/netex}LuggageLockerEquipmentGroup"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LuggageLockerEquipment_VersionStructure", propOrder = {
    "numberOfLockers",
    "lockerWidth",
    "lockerHeight",
    "lockerDepth",
    "lockerType"
})
@XmlSeeAlso({
    LuggageLockerEquipment.class
})
public class LuggageLockerEquipment_VersionStructure
    extends SiteEquipment_VersionStructure
{

    @XmlElement(name = "NumberOfLockers")
    protected BigInteger numberOfLockers;
    @XmlElement(name = "LockerWidth")
    protected BigDecimal lockerWidth;
    @XmlElement(name = "LockerHeight")
    protected BigDecimal lockerHeight;
    @XmlElement(name = "LockerDepth")
    protected BigDecimal lockerDepth;
    @XmlElement(name = "LockerType")
    @XmlSchemaType(name = "normalizedString")
    protected LockerTypeEnumeration lockerType;

    /**
     * Gets the value of the numberOfLockers property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumberOfLockers() {
        return numberOfLockers;
    }

    /**
     * Sets the value of the numberOfLockers property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumberOfLockers(BigInteger value) {
        this.numberOfLockers = value;
    }

    /**
     * Gets the value of the lockerWidth property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLockerWidth() {
        return lockerWidth;
    }

    /**
     * Sets the value of the lockerWidth property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLockerWidth(BigDecimal value) {
        this.lockerWidth = value;
    }

    /**
     * Gets the value of the lockerHeight property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLockerHeight() {
        return lockerHeight;
    }

    /**
     * Sets the value of the lockerHeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLockerHeight(BigDecimal value) {
        this.lockerHeight = value;
    }

    /**
     * Gets the value of the lockerDepth property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLockerDepth() {
        return lockerDepth;
    }

    /**
     * Sets the value of the lockerDepth property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLockerDepth(BigDecimal value) {
        this.lockerDepth = value;
    }

    /**
     * Gets the value of the lockerType property.
     * 
     * @return
     *     possible object is
     *     {@link LockerTypeEnumeration }
     *     
     */
    public LockerTypeEnumeration getLockerType() {
        return lockerType;
    }

    /**
     * Sets the value of the lockerType property.
     * 
     * @param value
     *     allowed object is
     *     {@link LockerTypeEnumeration }
     *     
     */
    public void setLockerType(LockerTypeEnumeration value) {
        this.lockerType = value;
    }

}

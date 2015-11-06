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
import javax.xml.bind.annotation.XmlType;


/**
 * Defines a bounding box using two corner points. GML terminology.
 * 
 * <p>Java class for BoundingBoxStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BoundingBoxStructure">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UpperLeft" type="{http://www.netex.org.uk/netex}LocationStructure"/>
 *         &lt;element name="LowerRight" type="{http://www.netex.org.uk/netex}LocationStructure"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BoundingBoxStructure", propOrder = {
    "upperLeft",
    "lowerRight"
})
public class BoundingBoxStructure {

    @XmlElement(name = "UpperLeft", required = true)
    protected LocationStructure upperLeft;
    @XmlElement(name = "LowerRight", required = true)
    protected LocationStructure lowerRight;

    /**
     * Gets the value of the upperLeft property.
     * 
     * @return
     *     possible object is
     *     {@link LocationStructure }
     *     
     */
    public LocationStructure getUpperLeft() {
        return upperLeft;
    }

    /**
     * Sets the value of the upperLeft property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocationStructure }
     *     
     */
    public void setUpperLeft(LocationStructure value) {
        this.upperLeft = value;
    }

    /**
     * Gets the value of the lowerRight property.
     * 
     * @return
     *     possible object is
     *     {@link LocationStructure }
     *     
     */
    public LocationStructure getLowerRight() {
        return lowerRight;
    }

    /**
     * Sets the value of the lowerRight property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocationStructure }
     *     
     */
    public void setLowerRight(LocationStructure value) {
        this.lowerRight = value;
    }

}

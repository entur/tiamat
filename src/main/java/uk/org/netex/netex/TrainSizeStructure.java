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
import javax.xml.bind.annotation.XmlType;


/**
 * Type for a TRAIN SIZE REQUIREMENT.
 * 
 * <p>Java class for TrainSizeStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TrainSizeStructure">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NumberOfCars" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="TrainSizeType" type="{http://www.netex.org.uk/netex}TrainSizeEnumeration" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrainSizeStructure", propOrder = {
    "numberOfCars",
    "trainSizeType"
})
public class TrainSizeStructure {

    @XmlElement(name = "NumberOfCars")
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger numberOfCars;
    @XmlElement(name = "TrainSizeType", defaultValue = "normal")
    @XmlSchemaType(name = "NMTOKEN")
    protected TrainSizeEnumeration trainSizeType;

    /**
     * Gets the value of the numberOfCars property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumberOfCars() {
        return numberOfCars;
    }

    /**
     * Sets the value of the numberOfCars property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumberOfCars(BigInteger value) {
        this.numberOfCars = value;
    }

    /**
     * Gets the value of the trainSizeType property.
     * 
     * @return
     *     possible object is
     *     {@link TrainSizeEnumeration }
     *     
     */
    public TrainSizeEnumeration getTrainSizeType() {
        return trainSizeType;
    }

    /**
     * Sets the value of the trainSizeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TrainSizeEnumeration }
     *     
     */
    public void setTrainSizeType(TrainSizeEnumeration value) {
        this.trainSizeType = value;
    }

}

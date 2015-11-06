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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;


/**
 * Type for a CHECK CONSTRAINT DELAY.
 * 
 * <p>Java class for CheckConstraintDelay_VersionStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CheckConstraintDelay_VersionStructure">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.netex.org.uk/netex}Assignment_VersionStructure">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.netex.org.uk/netex}CheckConstraintDelayGroup"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CheckConstraintDelay_VersionStructure", propOrder = {
    "checkConstraintRef",
    "classOfUseRef",
    "minimumLikelyDelay",
    "averageDelay",
    "maximumLikelyDelay"
})
@XmlSeeAlso({
    CheckConstraintDelay.class
})
public class CheckConstraintDelay_VersionStructure
    extends Assignment_VersionStructure
{

    @XmlElement(name = "CheckConstraintRef")
    protected CheckConstraintRefStructure checkConstraintRef;
    @XmlElement(name = "ClassOfUseRef")
    protected ClassOfUseRef classOfUseRef;
    @XmlElement(name = "MinimumLikelyDelay")
    protected Duration minimumLikelyDelay;
    @XmlElement(name = "AverageDelay")
    protected Duration averageDelay;
    @XmlElement(name = "MaximumLikelyDelay")
    protected Duration maximumLikelyDelay;

    /**
     * Reference to a CHECK CONSTRAINT. Can be omitted if given by context.
     * 
     * @return
     *     possible object is
     *     {@link CheckConstraintRefStructure }
     *     
     */
    public CheckConstraintRefStructure getCheckConstraintRef() {
        return checkConstraintRef;
    }

    /**
     * Sets the value of the checkConstraintRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link CheckConstraintRefStructure }
     *     
     */
    public void setCheckConstraintRef(CheckConstraintRefStructure value) {
        this.checkConstraintRef = value;
    }

    /**
     * Gets the value of the classOfUseRef property.
     * 
     * @return
     *     possible object is
     *     {@link ClassOfUseRef }
     *     
     */
    public ClassOfUseRef getClassOfUseRef() {
        return classOfUseRef;
    }

    /**
     * Sets the value of the classOfUseRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClassOfUseRef }
     *     
     */
    public void setClassOfUseRef(ClassOfUseRef value) {
        this.classOfUseRef = value;
    }

    /**
     * Gets the value of the minimumLikelyDelay property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getMinimumLikelyDelay() {
        return minimumLikelyDelay;
    }

    /**
     * Sets the value of the minimumLikelyDelay property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setMinimumLikelyDelay(Duration value) {
        this.minimumLikelyDelay = value;
    }

    /**
     * Gets the value of the averageDelay property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getAverageDelay() {
        return averageDelay;
    }

    /**
     * Sets the value of the averageDelay property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setAverageDelay(Duration value) {
        this.averageDelay = value;
    }

    /**
     * Gets the value of the maximumLikelyDelay property.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getMaximumLikelyDelay() {
        return maximumLikelyDelay;
    }

    /**
     * Sets the value of the maximumLikelyDelay property.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setMaximumLikelyDelay(Duration value) {
        this.maximumLikelyDelay = value;
    }

}

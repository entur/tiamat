//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.05 at 07:41:01 PM CET 
//


package uk.org.netex.netex;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Type for FARE SECTION.
 * 
 * <p>Java class for FareSection_VersionStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FareSection_VersionStructure">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.netex.org.uk/netex}CommonSection_VersionStructure">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.netex.org.uk/netex}FareSectionGroup"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FareSection_VersionStructure", propOrder = {
    "journeyPatternRef",
    "journeyPattern",
    "fromPointInPatternRef",
    "toPointInPatternRef"
})
@XmlSeeAlso({
    FareSection.class
})
public class FareSection_VersionStructure
    extends CommonSection_VersionStructure
{

    @XmlElementRef(name = "JourneyPatternRef", namespace = "http://www.netex.org.uk/netex", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends JourneyPatternRefStructure> journeyPatternRef;
    @XmlElement(name = "JourneyPattern")
    protected JourneyPattern journeyPattern;
    @XmlElement(name = "FromPointInPatternRef")
    protected FarePointInPatternRefStructure fromPointInPatternRef;
    @XmlElement(name = "ToPointInPatternRef")
    protected FarePointInPatternRefStructure toPointInPatternRef;

    /**
     * Gets the value of the journeyPatternRef property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link DeadRunJourneyPatternRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link ServiceJourneyPatternRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link JourneyPatternRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link ServicePatternRefStructure }{@code >}
     *     
     */
    public JAXBElement<? extends JourneyPatternRefStructure> getJourneyPatternRef() {
        return journeyPatternRef;
    }

    /**
     * Sets the value of the journeyPatternRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link DeadRunJourneyPatternRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link ServiceJourneyPatternRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link JourneyPatternRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link ServicePatternRefStructure }{@code >}
     *     
     */
    public void setJourneyPatternRef(JAXBElement<? extends JourneyPatternRefStructure> value) {
        this.journeyPatternRef = value;
    }

    /**
     * Gets the value of the journeyPattern property.
     * 
     * @return
     *     possible object is
     *     {@link JourneyPattern }
     *     
     */
    public JourneyPattern getJourneyPattern() {
        return journeyPattern;
    }

    /**
     * Sets the value of the journeyPattern property.
     * 
     * @param value
     *     allowed object is
     *     {@link JourneyPattern }
     *     
     */
    public void setJourneyPattern(JourneyPattern value) {
        this.journeyPattern = value;
    }

    /**
     * Gets the value of the fromPointInPatternRef property.
     * 
     * @return
     *     possible object is
     *     {@link FarePointInPatternRefStructure }
     *     
     */
    public FarePointInPatternRefStructure getFromPointInPatternRef() {
        return fromPointInPatternRef;
    }

    /**
     * Sets the value of the fromPointInPatternRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link FarePointInPatternRefStructure }
     *     
     */
    public void setFromPointInPatternRef(FarePointInPatternRefStructure value) {
        this.fromPointInPatternRef = value;
    }

    /**
     * Gets the value of the toPointInPatternRef property.
     * 
     * @return
     *     possible object is
     *     {@link FarePointInPatternRefStructure }
     *     
     */
    public FarePointInPatternRefStructure getToPointInPatternRef() {
        return toPointInPatternRef;
    }

    /**
     * Sets the value of the toPointInPatternRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link FarePointInPatternRefStructure }
     *     
     */
    public void setToPointInPatternRef(FarePointInPatternRefStructure value) {
        this.toPointInPatternRef = value;
    }

}

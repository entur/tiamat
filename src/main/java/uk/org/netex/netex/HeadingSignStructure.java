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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * Type for a HEADING SIGN.
 * 
 * <p>Java class for HeadingSignStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="HeadingSignStructure">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.netex.org.uk/netex}SignEquipment_VersionStructure">
 *       &lt;sequence>
 *         &lt;element name="PlaceName" type="{http://www.netex.org.uk/netex}MultilingualString"/>
 *         &lt;group ref="{http://www.netex.org.uk/netex}HeadingSignGroup"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HeadingSignStructure", propOrder = {
    "placeName",
    "lineRef",
    "lineName",
    "transportMode",
    "transportSubmode",
    "lineMap",
    "directionRef",
    "directionName",
    "destinationDisplayRef",
    "linePublicCode"
})
@XmlSeeAlso({
    HeadingSign.class
})
public class HeadingSignStructure
    extends SignEquipment_VersionStructure
{

    @XmlElement(name = "PlaceName", required = true)
    protected MultilingualString placeName;
    @XmlElementRef(name = "LineRef", namespace = "http://www.netex.org.uk/netex", type = JAXBElement.class, required = false)
    protected JAXBElement<? extends LineRefStructure> lineRef;
    @XmlElement(name = "LineName")
    protected MultilingualString lineName;
    @XmlElement(name = "TransportMode")
    @XmlSchemaType(name = "NMTOKEN")
    protected VehicleModeEnumeration transportMode;
    @XmlElement(name = "TransportSubmode")
    protected TransportSubmodeStructure transportSubmode;
    @XmlElement(name = "LineMap")
    @XmlSchemaType(name = "anyURI")
    protected String lineMap;
    @XmlElement(name = "DirectionRef")
    protected DirectionRefStructure directionRef;
    @XmlElement(name = "DirectionName")
    protected MultilingualString directionName;
    @XmlElement(name = "DestinationDisplayRef")
    protected DestinationDisplayRefStructure destinationDisplayRef;
    @XmlElement(name = "LinePublicCode")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String linePublicCode;

    /**
     * Gets the value of the placeName property.
     * 
     * @return
     *     possible object is
     *     {@link MultilingualString }
     *     
     */
    public MultilingualString getPlaceName() {
        return placeName;
    }

    /**
     * Sets the value of the placeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link MultilingualString }
     *     
     */
    public void setPlaceName(MultilingualString value) {
        this.placeName = value;
    }

    /**
     * Gets the value of the lineRef property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link FlexibleLineRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link LineRefStructure }{@code >}
     *     
     */
    public JAXBElement<? extends LineRefStructure> getLineRef() {
        return lineRef;
    }

    /**
     * Sets the value of the lineRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link FlexibleLineRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link LineRefStructure }{@code >}
     *     
     */
    public void setLineRef(JAXBElement<? extends LineRefStructure> value) {
        this.lineRef = value;
    }

    /**
     * Gets the value of the lineName property.
     * 
     * @return
     *     possible object is
     *     {@link MultilingualString }
     *     
     */
    public MultilingualString getLineName() {
        return lineName;
    }

    /**
     * Sets the value of the lineName property.
     * 
     * @param value
     *     allowed object is
     *     {@link MultilingualString }
     *     
     */
    public void setLineName(MultilingualString value) {
        this.lineName = value;
    }

    /**
     * Gets the value of the transportMode property.
     * 
     * @return
     *     possible object is
     *     {@link VehicleModeEnumeration }
     *     
     */
    public VehicleModeEnumeration getTransportMode() {
        return transportMode;
    }

    /**
     * Sets the value of the transportMode property.
     * 
     * @param value
     *     allowed object is
     *     {@link VehicleModeEnumeration }
     *     
     */
    public void setTransportMode(VehicleModeEnumeration value) {
        this.transportMode = value;
    }

    /**
     * Gets the value of the transportSubmode property.
     * 
     * @return
     *     possible object is
     *     {@link TransportSubmodeStructure }
     *     
     */
    public TransportSubmodeStructure getTransportSubmode() {
        return transportSubmode;
    }

    /**
     * Sets the value of the transportSubmode property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportSubmodeStructure }
     *     
     */
    public void setTransportSubmode(TransportSubmodeStructure value) {
        this.transportSubmode = value;
    }

    /**
     * Gets the value of the lineMap property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLineMap() {
        return lineMap;
    }

    /**
     * Sets the value of the lineMap property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLineMap(String value) {
        this.lineMap = value;
    }

    /**
     * Gets the value of the directionRef property.
     * 
     * @return
     *     possible object is
     *     {@link DirectionRefStructure }
     *     
     */
    public DirectionRefStructure getDirectionRef() {
        return directionRef;
    }

    /**
     * Sets the value of the directionRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link DirectionRefStructure }
     *     
     */
    public void setDirectionRef(DirectionRefStructure value) {
        this.directionRef = value;
    }

    /**
     * Gets the value of the directionName property.
     * 
     * @return
     *     possible object is
     *     {@link MultilingualString }
     *     
     */
    public MultilingualString getDirectionName() {
        return directionName;
    }

    /**
     * Sets the value of the directionName property.
     * 
     * @param value
     *     allowed object is
     *     {@link MultilingualString }
     *     
     */
    public void setDirectionName(MultilingualString value) {
        this.directionName = value;
    }

    /**
     * Gets the value of the destinationDisplayRef property.
     * 
     * @return
     *     possible object is
     *     {@link DestinationDisplayRefStructure }
     *     
     */
    public DestinationDisplayRefStructure getDestinationDisplayRef() {
        return destinationDisplayRef;
    }

    /**
     * Sets the value of the destinationDisplayRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link DestinationDisplayRefStructure }
     *     
     */
    public void setDestinationDisplayRef(DestinationDisplayRefStructure value) {
        this.destinationDisplayRef = value;
    }

    /**
     * Gets the value of the linePublicCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLinePublicCode() {
        return linePublicCode;
    }

    /**
     * Sets the value of the linePublicCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLinePublicCode(String value) {
        this.linePublicCode = value;
    }

}

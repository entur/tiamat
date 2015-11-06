//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.05 at 07:41:01 PM CET 
//


package uk.org.netex.netex;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Type for a reference to a LINK.
 * 
 * <p>Java class for LinkRefByValueStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LinkRefByValueStructure">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attGroup ref="{http://www.netex.org.uk/netex}ReferenceModificationDetailsGroup"/>
 *       &lt;attribute name="nameOfClass" type="{http://www.netex.org.uk/netex}NameOfClass" />
 *       &lt;attribute name="fromPointRef" use="required" type="{http://www.netex.org.uk/netex}PointIdType" />
 *       &lt;attribute name="toPointRef" use="required" type="{http://www.netex.org.uk/netex}PointIdType" />
 *       &lt;attribute name="nameOfPointRefClass" type="{http://www.netex.org.uk/netex}NameOfClass" />
 *       &lt;attribute name="typeOfLinkRef" type="{http://www.netex.org.uk/netex}TypeOfLinkIdType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LinkRefByValueStructure")
@XmlSeeAlso({
    ActivationLinkRefByValueStructure.class,
    WireLinkRefByValueStructure.class,
    ModalLinkRefByValueStructure.class,
    RouteLinkRefByValueStructure.class,
    PathLinkRefByValueStructure.class,
    RailwayLinkRefByValueStructure.class,
    RoadLinkRefByValueStructure.class,
    LineLinkRefByValueStructure.class,
    TimingLinkRefByValueStructure.class,
    ServiceLinkRefByValueStructure.class
})
public class LinkRefByValueStructure {

    @XmlAttribute(name = "nameOfClass")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String nameOfClass;
    @XmlAttribute(name = "fromPointRef", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String fromPointRef;
    @XmlAttribute(name = "toPointRef", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String toPointRef;
    @XmlAttribute(name = "nameOfPointRefClass")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String nameOfPointRefClass;
    @XmlAttribute(name = "typeOfLinkRef")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String typeOfLinkRef;
    @XmlAttribute(name = "created")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar created;
    @XmlAttribute(name = "changed")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar changed;
    @XmlAttribute(name = "version")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String version;

    /**
     * Gets the value of the nameOfClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameOfClass() {
        return nameOfClass;
    }

    /**
     * Sets the value of the nameOfClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameOfClass(String value) {
        this.nameOfClass = value;
    }

    /**
     * Gets the value of the fromPointRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromPointRef() {
        return fromPointRef;
    }

    /**
     * Sets the value of the fromPointRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromPointRef(String value) {
        this.fromPointRef = value;
    }

    /**
     * Gets the value of the toPointRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToPointRef() {
        return toPointRef;
    }

    /**
     * Sets the value of the toPointRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToPointRef(String value) {
        this.toPointRef = value;
    }

    /**
     * Gets the value of the nameOfPointRefClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameOfPointRefClass() {
        return nameOfPointRefClass;
    }

    /**
     * Sets the value of the nameOfPointRefClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameOfPointRefClass(String value) {
        this.nameOfPointRefClass = value;
    }

    /**
     * Gets the value of the typeOfLinkRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypeOfLinkRef() {
        return typeOfLinkRef;
    }

    /**
     * Sets the value of the typeOfLinkRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypeOfLinkRef(String value) {
        this.typeOfLinkRef = value;
    }

    /**
     * Gets the value of the created property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreated() {
        return created;
    }

    /**
     * Sets the value of the created property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreated(XMLGregorianCalendar value) {
        this.created = value;
    }

    /**
     * Gets the value of the changed property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getChanged() {
        return changed;
    }

    /**
     * Sets the value of the changed property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setChanged(XMLGregorianCalendar value) {
        this.changed = value;
    }

    /**
     * Gets the value of the version property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersion(String value) {
        this.version = value;
    }

}

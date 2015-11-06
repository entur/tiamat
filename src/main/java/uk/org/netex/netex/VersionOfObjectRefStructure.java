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
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Type for a versioned reference to a NeTEx Object.
 * 
 * <p>Java class for VersionOfObjectRefStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VersionOfObjectRefStructure">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.netex.org.uk/netex>ObjectIdType">
 *       &lt;attGroup ref="{http://www.netex.org.uk/netex}ReferenceModificationDetailsGroup"/>
 *       &lt;attribute name="nameOfRefClass" type="{http://www.netex.org.uk/netex}NameOfClass" />
 *       &lt;attribute name="ref" use="required" type="{http://www.netex.org.uk/netex}ObjectIdType" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VersionOfObjectRefStructure", propOrder = {
    "value"
})
@XmlSeeAlso({
    PassengerCapacityRefStructure.class,
    TrainElementRefStructure.class,
    OperatingDayRefStructure.class,
    OnboardStayRefStructure.class,
    TimebandRefStructure.class,
    TimeDemandProfileRefStructure.class,
    AlternativeNameRefStructure.class,
    AccommodationRefStructure.class,
    VehicleRefStructure.class,
    LineRefStructure.class,
    DeliveryVariantRefStructure.class,
    FacilityRefStructure.class,
    SchematicMapRefStructure.class,
    TimeDemandTypeRefStructure.class,
    SchematicMapMemberRefStructure.class,
    TrainInCompoundTrainRefStructure.class,
    LogicalDisplayRefStructure.class,
    DestinationDisplayRefStructure.class,
    VersionFrameRefStructure.class,
    NoticeRefStructure.class,
    DataSourceRefStructure.class,
    TransferRefStructure.class,
    SubmodeRefStructure.class,
    VehiclePositionAlignmentRefStructure.class,
    FlexiblePointPropertiesRefStructure.class,
    VehicleEquipmentProfileRefStructure.class,
    OperatingPeriodRefStructure.class,
    AllOrganisationsRefStructure.class,
    AccessibilityAssessmentRefStructure.class,
    DestinationDisplayVariantRefStructure.class,
    DayTypeRefStructure.class,
    AllowedLineDirectionRefStructure.class,
    OrganisationPartRefStructure.class,
    VehicleQuayAlignmentRefStructure.class,
    OpenTransportModeRefStructure.class,
    VehicleModelRefStructure.class,
    LinkSequenceRefStructure.class,
    FacilitySetRefStructure.class,
    ProjectionRefStructure.class,
    VehicleRequirementRefStructure.class,
    LevelRefStructure.class,
    VersionRefStructure.class,
    OrganisationRefStructure.class,
    FlexibleLinkPropertiesRefStructure.class,
    LineNetworkRefStructure.class,
    ServiceCalendarRefStructure.class,
    ResponsibilityRoleRefStructure.class,
    ResponsibilitySetRefStructure.class,
    ParkingPropertyRefStructure.class,
    ValidityConditionRefStructure.class,
    OperationalContextRefStructure.class,
    ParkingCapacityRefStructure.class,
    LimitationRefStructure.class,
    LinkRefStructure.class,
    JourneyTimingRefStructure.class,
    OrderedVersionOfObjectRefStructure.class,
    GroupOfEntitiesRefStructure_.class,
    PointRefStructure.class,
    EntityInVersionInFrameRefStructure.class,
    VehicleTypeRefStructure.class,
    EquipmentRefStructure.class,
    TypeOfValueRefStructure.class
})
public class VersionOfObjectRefStructure {

    @XmlValue
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String value;
    @XmlAttribute(name = "nameOfRefClass")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String nameOfRefClass;
    @XmlAttribute(name = "ref", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String ref;
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
     * Abstract Type for identifier of a NeTEx Object.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the nameOfRefClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameOfRefClass() {
        return nameOfRefClass;
    }

    /**
     * Sets the value of the nameOfRefClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameOfRefClass(String value) {
        this.nameOfRefClass = value;
    }

    /**
     * Gets the value of the ref property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRef() {
        return ref;
    }

    /**
     * Sets the value of the ref property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRef(String value) {
        this.ref = value;
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

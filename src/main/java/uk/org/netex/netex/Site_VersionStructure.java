//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.11.05 at 07:41:01 PM CET 
//


package uk.org.netex.netex;

import javax.persistence.*;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * Type for an identified and data managed element making up a STOP PLACE.
 * 
 * <p>Java class for Site_VersionStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Site_VersionStructure">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.netex.org.uk/netex}SiteElement_VersionStructure">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.netex.org.uk/netex}SiteGroup"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Site_VersionStructure", propOrder = {
    "topographicPlaceRef",
    "topographicPlaceView",
    "additionalTopographicPlaces",
    "siteType",
    "atCentre",
    "locale",
    "organisationRef",
    "operatingOrganisationView",
    "parentSiteRef",
    "adjacentSites",
    "containedInPlaceRef",
    "levels",
    "entrances",
    "equipmentPlaces",
    "placeEquipments",
    "localServices"
})
@XmlSeeAlso({
    Parking_VersionStructure.class,
    StopPlace_VersionStructure.class,
    ServiceSite_VersionStructure.class,
    PointOfInterest_VersionStructure.class
})
@MappedSuperclass
public abstract class Site_VersionStructure
    extends SiteElement_VersionStructure
{

    @XmlElement(name = "TopographicPlaceRef")
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    protected TopographicPlaceRefStructure topographicPlaceRef;

    @XmlElement(name = "TopographicPlaceView")
    @Transient
    protected TopographicPlaceView topographicPlaceView;

    @Transient
    protected TopographicPlaceRefs_RelStructure additionalTopographicPlaces;

    @XmlElement(name = "SiteType")
    @XmlSchemaType(name = "string")
    @Transient
    protected SiteTypeEnumeration siteType;

    @XmlElement(name = "AtCentre")
    @Transient
    protected Boolean atCentre;

    @XmlElement(name = "Locale")
    @Transient
    protected LocaleStructure locale;

    @XmlElementRef(name = "OrganisationRef", namespace = "http://www.netex.org.uk/netex", type = JAXBElement.class, required = false)
    @Transient
    protected JAXBElement<? extends OrganisationRefStructure> organisationRef;

    @XmlElement(name = "OperatingOrganisationView")
    @Transient
    protected Organisation_DerivedViewStructure operatingOrganisationView;

    @XmlElement(name = "ParentSiteRef")
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    protected SiteRefStructure parentSiteRef;

    @Transient
    protected SiteRefs_RelStructure adjacentSites;

    @XmlElement(name = "ContainedInPlaceRef")
    @Transient
    protected TopographicPlaceRefStructure containedInPlaceRef;

    @OneToMany(cascade = CascadeType.ALL)
    private final List<Level> levels = new ArrayList<>();

    @Transient
    protected SiteEntrances_RelStructure entrances;

    @OneToMany(cascade = CascadeType.ALL)
    private List<EquipmentPlace> equipmentPlaces;

    @Transient
    protected PlaceEquipments_RelStructure placeEquipments;

    @Transient
    protected LocalServices_RelStructure localServices;

    /**
     * Gets the value of the topographicPlaceRef property.
     * 
     * @return
     *     possible object is
     *     {@link TopographicPlaceRefStructure }
     *     
     */
    public TopographicPlaceRefStructure getTopographicPlaceRef() {
        return topographicPlaceRef;
    }

    /**
     * Sets the value of the topographicPlaceRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link TopographicPlaceRefStructure }
     *     
     */
    public void setTopographicPlaceRef(TopographicPlaceRefStructure value) {
        this.topographicPlaceRef = value;
    }

    /**
     * Gets the value of the topographicPlaceView property.
     * 
     * @return
     *     possible object is
     *     {@link TopographicPlaceView }
     *     
     */
    public TopographicPlaceView getTopographicPlaceView() {
        return topographicPlaceView;
    }

    /**
     * Sets the value of the topographicPlaceView property.
     * 
     * @param value
     *     allowed object is
     *     {@link TopographicPlaceView }
     *     
     */
    public void setTopographicPlaceView(TopographicPlaceView value) {
        this.topographicPlaceView = value;
    }

    /**
     * Gets the value of the additionalTopographicPlaces property.
     * 
     * @return
     *     possible object is
     *     {@link TopographicPlaceRefs_RelStructure }
     *     
     */
    public TopographicPlaceRefs_RelStructure getAdditionalTopographicPlaces() {
        return additionalTopographicPlaces;
    }

    /**
     * Sets the value of the additionalTopographicPlaces property.
     * 
     * @param value
     *     allowed object is
     *     {@link TopographicPlaceRefs_RelStructure }
     *     
     */
    public void setAdditionalTopographicPlaces(TopographicPlaceRefs_RelStructure value) {
        this.additionalTopographicPlaces = value;
    }

    /**
     * Gets the value of the siteType property.
     * 
     * @return
     *     possible object is
     *     {@link SiteTypeEnumeration }
     *     
     */
    public SiteTypeEnumeration getSiteType() {
        return siteType;
    }

    /**
     * Sets the value of the siteType property.
     * 
     * @param value
     *     allowed object is
     *     {@link SiteTypeEnumeration }
     *     
     */
    public void setSiteType(SiteTypeEnumeration value) {
        this.siteType = value;
    }

    /**
     * Gets the value of the atCentre property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAtCentre() {
        return atCentre;
    }

    /**
     * Sets the value of the atCentre property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAtCentre(Boolean value) {
        this.atCentre = value;
    }

    /**
     * Gets the value of the locale property.
     * 
     * @return
     *     possible object is
     *     {@link LocaleStructure }
     *     
     */
    public LocaleStructure getLocale() {
        return locale;
    }

    /**
     * Sets the value of the locale property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocaleStructure }
     *     
     */
    public void setLocale(LocaleStructure value) {
        this.locale = value;
    }

    /**
     * Gets the value of the organisationRef property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link TravelAgentRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link ManagementAgentRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link OrganisationRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link OrganisationRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link AuthorityRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link GeneralOrganisationRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link OperatorRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link ServicedOrganisationRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link OtherOrganisationRefStructure }{@code >}
     *     
     */
    public JAXBElement<? extends OrganisationRefStructure> getOrganisationRef() {
        return organisationRef;
    }

    /**
     * Sets the value of the organisationRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link TravelAgentRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link ManagementAgentRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link OrganisationRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link OrganisationRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link AuthorityRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link GeneralOrganisationRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link OperatorRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link ServicedOrganisationRefStructure }{@code >}
     *     {@link JAXBElement }{@code <}{@link OtherOrganisationRefStructure }{@code >}
     *     
     */
    public void setOrganisationRef(JAXBElement<? extends OrganisationRefStructure> value) {
        this.organisationRef = value;
    }

    /**
     * Gets the value of the operatingOrganisationView property.
     * 
     * @return
     *     possible object is
     *     {@link Organisation_DerivedViewStructure }
     *     
     */
    public Organisation_DerivedViewStructure getOperatingOrganisationView() {
        return operatingOrganisationView;
    }

    /**
     * Sets the value of the operatingOrganisationView property.
     * 
     * @param value
     *     allowed object is
     *     {@link Organisation_DerivedViewStructure }
     *     
     */
    public void setOperatingOrganisationView(Organisation_DerivedViewStructure value) {
        this.operatingOrganisationView = value;
    }

    /**
     * Gets the value of the parentSiteRef property.
     * 
     * @return
     *     possible object is
     *     {@link SiteRefStructure }
     *     
     */
    public SiteRefStructure getParentSiteRef() {
        return parentSiteRef;
    }

    /**
     * Sets the value of the parentSiteRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link SiteRefStructure }
     *     
     */
    public void setParentSiteRef(SiteRefStructure value) {
        this.parentSiteRef = value;
    }

    /**
     * Gets the value of the adjacentSites property.
     * 
     * @return
     *     possible object is
     *     {@link SiteRefs_RelStructure }
     *     
     */
    public SiteRefs_RelStructure getAdjacentSites() {
        return adjacentSites;
    }

    /**
     * Sets the value of the adjacentSites property.
     * 
     * @param value
     *     allowed object is
     *     {@link SiteRefs_RelStructure }
     *     
     */
    public void setAdjacentSites(SiteRefs_RelStructure value) {
        this.adjacentSites = value;
    }

    /**
     * Gets the value of the containedInPlaceRef property.
     * 
     * @return
     *     possible object is
     *     {@link TopographicPlaceRefStructure }
     *     
     */
    public TopographicPlaceRefStructure getContainedInPlaceRef() {
        return containedInPlaceRef;
    }

    /**
     * Sets the value of the containedInPlaceRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link TopographicPlaceRefStructure }
     *     
     */
    public void setContainedInPlaceRef(TopographicPlaceRefStructure value) {
        this.containedInPlaceRef = value;
    }


    /**
     * Gets the value of the entrances property.
     * 
     * @return
     *     possible object is
     *     {@link SiteEntrances_RelStructure }
     *     
     */
    public SiteEntrances_RelStructure getEntrances() {
        return entrances;
    }

    /**
     * Sets the value of the entrances property.
     * 
     * @param value
     *     allowed object is
     *     {@link SiteEntrances_RelStructure }
     *     
     */
    public void setEntrances(SiteEntrances_RelStructure value) {
        this.entrances = value;
    }



    /**
     * Gets the value of the placeEquipments property.
     * 
     * @return
     *     possible object is
     *     {@link PlaceEquipments_RelStructure }
     *     
     */
    public PlaceEquipments_RelStructure getPlaceEquipments() {
        return placeEquipments;
    }

    /**
     * Sets the value of the placeEquipments property.
     * 
     * @param value
     *     allowed object is
     *     {@link PlaceEquipments_RelStructure }
     *     
     */
    public void setPlaceEquipments(PlaceEquipments_RelStructure value) {
        this.placeEquipments = value;
    }

    /**
     * Gets the value of the localServices property.
     * 
     * @return
     *     possible object is
     *     {@link LocalServices_RelStructure }
     *     
     */
    public LocalServices_RelStructure getLocalServices() {
        return localServices;
    }

    /**
     * Sets the value of the localServices property.
     * 
     * @param value
     *     allowed object is
     *     {@link LocalServices_RelStructure }
     *     
     */
    public void setLocalServices(LocalServices_RelStructure value) {
        this.localServices = value;
    }

    public List<Level> getLevels() {
        return levels;
    }

    public List<EquipmentPlace> getEquipmentPlaces() {
        return equipmentPlaces;
    }

    public void setEquipmentPlaces(List<EquipmentPlace> equipmentPlaces) {
        this.equipmentPlaces = equipmentPlaces;
    }
}

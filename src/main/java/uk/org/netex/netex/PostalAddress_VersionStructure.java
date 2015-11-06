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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * Type for a POSTAL ADDRESS.
 * 
 * <p>Java class for PostalAddress_VersionStructure complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PostalAddress_VersionStructure">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.netex.org.uk/netex}Address_VersionStructure">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.netex.org.uk/netex}PostalAddressGroup"/>
 *         &lt;element name="RoadAddressRef" type="{http://www.netex.org.uk/netex}AddressRefStructure" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PostalAddress_VersionStructure", propOrder = {
    "houseNumber",
    "buildingName",
    "addressLine1",
    "addressLine2",
    "street",
    "town",
    "suburb",
    "postCode",
    "postCodeExtension",
    "postalRegion",
    "province",
    "roadAddressRef"
})
@XmlSeeAlso({
    PostalAddress.class
})
public class PostalAddress_VersionStructure
    extends Address_VersionStructure
{

    @XmlElement(name = "HouseNumber")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String houseNumber;
    @XmlElement(name = "BuildingName")
    protected MultilingualString buildingName;
    @XmlElement(name = "AddressLine1")
    protected MultilingualString addressLine1;
    @XmlElement(name = "AddressLine2")
    protected MultilingualString addressLine2;
    @XmlElement(name = "Street")
    protected MultilingualString street;
    @XmlElement(name = "Town")
    protected MultilingualString town;
    @XmlElement(name = "Suburb")
    protected MultilingualString suburb;
    @XmlElement(name = "PostCode")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String postCode;
    @XmlElement(name = "PostCodeExtension")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String postCodeExtension;
    @XmlElement(name = "PostalRegion")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @XmlSchemaType(name = "normalizedString")
    protected String postalRegion;
    @XmlElement(name = "Province")
    protected MultilingualString province;
    @XmlElement(name = "RoadAddressRef")
    protected AddressRefStructure roadAddressRef;

    /**
     * Gets the value of the houseNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHouseNumber() {
        return houseNumber;
    }

    /**
     * Sets the value of the houseNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHouseNumber(String value) {
        this.houseNumber = value;
    }

    /**
     * Gets the value of the buildingName property.
     * 
     * @return
     *     possible object is
     *     {@link MultilingualString }
     *     
     */
    public MultilingualString getBuildingName() {
        return buildingName;
    }

    /**
     * Sets the value of the buildingName property.
     * 
     * @param value
     *     allowed object is
     *     {@link MultilingualString }
     *     
     */
    public void setBuildingName(MultilingualString value) {
        this.buildingName = value;
    }

    /**
     * Gets the value of the addressLine1 property.
     * 
     * @return
     *     possible object is
     *     {@link MultilingualString }
     *     
     */
    public MultilingualString getAddressLine1() {
        return addressLine1;
    }

    /**
     * Sets the value of the addressLine1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link MultilingualString }
     *     
     */
    public void setAddressLine1(MultilingualString value) {
        this.addressLine1 = value;
    }

    /**
     * Gets the value of the addressLine2 property.
     * 
     * @return
     *     possible object is
     *     {@link MultilingualString }
     *     
     */
    public MultilingualString getAddressLine2() {
        return addressLine2;
    }

    /**
     * Sets the value of the addressLine2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link MultilingualString }
     *     
     */
    public void setAddressLine2(MultilingualString value) {
        this.addressLine2 = value;
    }

    /**
     * Gets the value of the street property.
     * 
     * @return
     *     possible object is
     *     {@link MultilingualString }
     *     
     */
    public MultilingualString getStreet() {
        return street;
    }

    /**
     * Sets the value of the street property.
     * 
     * @param value
     *     allowed object is
     *     {@link MultilingualString }
     *     
     */
    public void setStreet(MultilingualString value) {
        this.street = value;
    }

    /**
     * Gets the value of the town property.
     * 
     * @return
     *     possible object is
     *     {@link MultilingualString }
     *     
     */
    public MultilingualString getTown() {
        return town;
    }

    /**
     * Sets the value of the town property.
     * 
     * @param value
     *     allowed object is
     *     {@link MultilingualString }
     *     
     */
    public void setTown(MultilingualString value) {
        this.town = value;
    }

    /**
     * Gets the value of the suburb property.
     * 
     * @return
     *     possible object is
     *     {@link MultilingualString }
     *     
     */
    public MultilingualString getSuburb() {
        return suburb;
    }

    /**
     * Sets the value of the suburb property.
     * 
     * @param value
     *     allowed object is
     *     {@link MultilingualString }
     *     
     */
    public void setSuburb(MultilingualString value) {
        this.suburb = value;
    }

    /**
     * Gets the value of the postCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostCode() {
        return postCode;
    }

    /**
     * Sets the value of the postCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostCode(String value) {
        this.postCode = value;
    }

    /**
     * Gets the value of the postCodeExtension property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostCodeExtension() {
        return postCodeExtension;
    }

    /**
     * Sets the value of the postCodeExtension property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostCodeExtension(String value) {
        this.postCodeExtension = value;
    }

    /**
     * Gets the value of the postalRegion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostalRegion() {
        return postalRegion;
    }

    /**
     * Sets the value of the postalRegion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostalRegion(String value) {
        this.postalRegion = value;
    }

    /**
     * Gets the value of the province property.
     * 
     * @return
     *     possible object is
     *     {@link MultilingualString }
     *     
     */
    public MultilingualString getProvince() {
        return province;
    }

    /**
     * Sets the value of the province property.
     * 
     * @param value
     *     allowed object is
     *     {@link MultilingualString }
     *     
     */
    public void setProvince(MultilingualString value) {
        this.province = value;
    }

    /**
     * Gets the value of the roadAddressRef property.
     * 
     * @return
     *     possible object is
     *     {@link AddressRefStructure }
     *     
     */
    public AddressRefStructure getRoadAddressRef() {
        return roadAddressRef;
    }

    /**
     * Sets the value of the roadAddressRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressRefStructure }
     *     
     */
    public void setRoadAddressRef(AddressRefStructure value) {
        this.roadAddressRef = value;
    }

}

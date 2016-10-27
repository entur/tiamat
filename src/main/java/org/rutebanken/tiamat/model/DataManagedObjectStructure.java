package org.rutebanken.tiamat.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@MappedSuperclass
public abstract class DataManagedObjectStructure
    extends EntityInVersionStructure
{
    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    protected KeyListStructure keyList;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Map<String,ValueList> keyValues = new HashMap<>();

    @XmlElement(name = "Extensions")
    @Transient
    protected ExtensionsStructure extensions;

    @XmlElement(name = "BrandingRef")
    @Transient
    protected BrandingRefStructure brandingRef;

    @XmlAttribute(name = "responsibilitySetRef")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    @Transient
    protected String responsibilitySetRef;

    /**
     * A list of alternative Key values for an element.
     * 
     * @return
     *     possible object is
     *     {@link KeyListStructure }
     *     
     */
    public KeyListStructure getKeyList() {
        return keyList;
    }

    /**
     * Sets the value of the keyList property.
     * 
     * @param value
     *     allowed object is
     *     {@link KeyListStructure }
     *     
     */
    public void setKeyList(KeyListStructure value) {
        this.keyList = value;
    }

    /**
     * Gets the value of the extensions property.
     * 
     * @return
     *     possible object is
     *     {@link ExtensionsStructure }
     *     
     */
    public ExtensionsStructure getExtensions() {
        return extensions;
    }

    /**
     * Sets the value of the extensions property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtensionsStructure }
     *     
     */
    public void setExtensions(ExtensionsStructure value) {
        this.extensions = value;
    }

    /**
     * Gets the value of the brandingRef property.
     * 
     * @return
     *     possible object is
     *     {@link BrandingRefStructure }
     *     
     */
    public BrandingRefStructure getBrandingRef() {
        return brandingRef;
    }

    /**
     * Sets the value of the brandingRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link BrandingRefStructure }
     *     
     */
    public void setBrandingRef(BrandingRefStructure value) {
        this.brandingRef = value;
    }

    /**
     * Gets the value of the responsibilitySetRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponsibilitySetRef() {
        return responsibilitySetRef;
    }

    /**
     * Sets the value of the responsibilitySetRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponsibilitySetRef(String value) {
        this.responsibilitySetRef = value;
    }

    public Map<String, ValueList> getKeyValues() {
        return keyValues;
    }
}

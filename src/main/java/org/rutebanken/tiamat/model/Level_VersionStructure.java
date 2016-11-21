

package org.rutebanken.tiamat.model;

import javax.persistence.*;
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


@MappedSuperclass
public class Level_VersionStructure
    extends DataManagedObjectStructure
{

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected MultilingualStringEntity name;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected MultilingualStringEntity shortName;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected MultilingualStringEntity description;

    protected String publicCode;

    protected Boolean publicUse;

    @Transient
    protected AccessibilityAssessment_VersionedChildStructure accessibilityAssessment;

    protected Boolean allAreasWheelchairAccessible;

    @Transient
    protected JAXBElement<? extends SiteRefStructure> siteRef;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public MultilingualStringEntity getShortName() {
        return shortName;
    }

    public void setShortName(MultilingualStringEntity value) {
        this.shortName = value;
    }

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String value) {
        this.publicCode = value;
    }

    public Boolean isPublicUse() {
        return publicUse;
    }

    public void setPublicUse(Boolean value) {
        this.publicUse = value;
    }

    public AccessibilityAssessment_VersionedChildStructure getAccessibilityAssessment() {
        return accessibilityAssessment;
    }

    public void setAccessibilityAssessment(AccessibilityAssessment_VersionedChildStructure value) {
        this.accessibilityAssessment = value;
    }

    public Boolean isAllAreasWheelchairAccessible() {
        return allAreasWheelchairAccessible;
    }

    public void setAllAreasWheelchairAccessible(Boolean value) {
        this.allAreasWheelchairAccessible = value;
    }

    public JAXBElement<? extends SiteRefStructure> getSiteRef() {
        return siteRef;
    }

    public void setSiteRef(JAXBElement<? extends SiteRefStructure> value) {
        this.siteRef = value;
    }

}

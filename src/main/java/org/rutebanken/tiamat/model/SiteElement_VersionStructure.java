

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


    "accessibilityAssessment",
    "accessModes",
    "nameSuffix",
    "alternativeNames",
    "crossRoad",
    "landmark",
    "publicUse",
    "covered",
    "gated",
    "lighting",
    "allAreasWheelchairAccessible",
    "personCapacity",
@MappedSuperclass
public abstract class SiteElement_VersionStructure
    extends AddressablePlace_VersionStructure
{

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    protected AccessibilityAssessment accessibilityAssessment;

    @Transient
    protected List<AccessModeEnumeration> accessModes;

    @Transient
    protected MultilingualStringEntity nameSuffix;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<AlternativeName> alternativeNames = new ArrayList<>();

    @Transient
    protected MultilingualStringEntity crossRoad;

    @Transient
    protected MultilingualStringEntity landmark;

    @Transient
    protected PublicUseEnumeration publicUse;

    protected CoveredEnumeration covered;

    @Transient
    protected GatedEnumeration gated;

    @Transient
    protected LightingEnumeration lighting;

    protected Boolean allAreasWheelchairAccessible;

    @Transient
    protected BigInteger personCapacity;

    @Transient
    protected SiteFacilitySets_RelStructure facilities;

    public SiteElement_VersionStructure(EmbeddableMultilingualString name) {
        super(name);
    }

    public SiteElement_VersionStructure() {}

    public AccessibilityAssessment getAccessibilityAssessment() {
        return accessibilityAssessment;
    }

    public void setAccessibilityAssessment(AccessibilityAssessment value) {
        this.accessibilityAssessment = value;
    }

    public List<AccessModeEnumeration> getAccessModes() {
        if (accessModes == null) {
            accessModes = new ArrayList<AccessModeEnumeration>();
        }
        return this.accessModes;
    }

    public MultilingualStringEntity getNameSuffix() {
        return nameSuffix;
    }

    public void setNameSuffix(MultilingualStringEntity value) {
        this.nameSuffix = value;
    }

    public List<AlternativeName> getAlternativeNames() {
        return alternativeNames;
    }



    public MultilingualStringEntity getCrossRoad() {
        return crossRoad;
    }

    public void setCrossRoad(MultilingualStringEntity value) {
        this.crossRoad = value;
    }

    public MultilingualStringEntity getLandmark() {
        return landmark;
    }

    public void setLandmark(MultilingualStringEntity value) {
        this.landmark = value;
    }

    public PublicUseEnumeration getPublicUse() {
        return publicUse;
    }

    public void setPublicUse(PublicUseEnumeration value) {
        this.publicUse = value;
    }

    public CoveredEnumeration getCovered() {
        return covered;
    }

    public void setCovered(CoveredEnumeration value) {
        this.covered = value;
    }

    public GatedEnumeration getGated() {
        return gated;
    }

    public void setGated(GatedEnumeration value) {
        this.gated = value;
    }

    public LightingEnumeration getLighting() {
        return lighting;
    }

    public void setLighting(LightingEnumeration value) {
        this.lighting = value;
    }

    public Boolean isAllAreasWheelchairAccessible() {
        return allAreasWheelchairAccessible;
    }

    public void setAllAreasWheelchairAccessible(Boolean value) {
        this.allAreasWheelchairAccessible = value;
    }

    public BigInteger getPersonCapacity() {
        return personCapacity;
    }

    public void setPersonCapacity(BigInteger value) {
        this.personCapacity = value;
    }

    public SiteFacilitySets_RelStructure getFacilities() {
        return facilities;
    }

    public void setFacilities(SiteFacilitySets_RelStructure value) {
        this.facilities = value;
    }

}

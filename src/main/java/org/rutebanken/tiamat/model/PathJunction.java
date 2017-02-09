package org.rutebanken.tiamat.model;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.math.BigInteger;

@Entity
public class PathJunction extends Point {

    @Transient
    protected ZoneRefStructure parentZoneRef;

    @Transient
    protected PublicUseEnumeration publicUse;

    @Transient
    protected CoveredEnumeration covered;

    @Transient
    protected GatedEnumeration gated;

    @Transient
    protected LightingEnumeration lighting;

    @Transient
    protected Boolean allAreasWheelchairAccessible;

    @Transient
    protected BigInteger personCapacity;

    @Transient
    protected SiteFacilitySets_RelStructure facilities;

    @Transient
    protected MultilingualStringEntity label;

    @Transient
    protected SiteComponentRefStructure siteComponentRef;

    public ZoneRefStructure getParentZoneRef() {
        return parentZoneRef;
    }

    public void setParentZoneRef(ZoneRefStructure value) {
        this.parentZoneRef = value;
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

    public MultilingualStringEntity getLabel() {
        return label;
    }

    public void setLabel(MultilingualStringEntity value) {
        this.label = value;
    }

    public SiteComponentRefStructure getSiteComponentRef() {
        return siteComponentRef;
    }

    public void setSiteComponentRef(SiteComponentRefStructure value) {
        this.siteComponentRef = value;
    }

}

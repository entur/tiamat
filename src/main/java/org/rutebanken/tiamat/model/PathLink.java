package org.rutebanken.tiamat.model;

import com.google.common.base.MoreObjects;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "tiamatEntityCacheRegion")
public class PathLink extends Link {

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    protected PathLinkEnd from;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    protected PathLinkEnd to;

    @Embedded
    protected TransferDuration transferDuration;

    @Transient
    protected MultilingualStringEntity description;

    @Transient
    protected AccessibilityAssessmentRefStructure accessibilityAssessmentRef;

    @Transient
    protected AccessibilityAssessment accessibilityAssessment;

    @Transient
    protected List<AccessModeEnumeration> accessModes;

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
    protected MultilingualStringEntity towards;

    @Transient
    protected MultilingualStringEntity back;

    @Transient
    protected BigInteger numberOfSteps;

    @Transient
    protected PathDirectionEnumeration allowedUse;

    @Transient
    protected TransitionEnumeration transition;

    @Transient
    protected AccessFeatureEnumeration accessFeatureType;

    @Transient
    protected PassageTypeEnumeration passageType;

    @Transient
    protected BigInteger maximumFlowPerMinute;

    public PathLink(PathLinkEnd from, PathLinkEnd to) {
        this.from = from;
        this.to = to;
    }

    public PathLink() {
    }

    public PathLinkEnd getFrom() {
        return from;
    }

    public void setFrom(PathLinkEnd value) {
        this.from = value;
    }

    public PathLinkEnd getTo() {
        return to;
    }

    public void setTo(PathLinkEnd value) {
        this.to = value;
    }

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public AccessibilityAssessmentRefStructure getAccessibilityAssessmentRef() {
        return accessibilityAssessmentRef;
    }

    public void setAccessibilityAssessmentRef(AccessibilityAssessmentRefStructure value) {
        this.accessibilityAssessmentRef = value;
    }

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

    public MultilingualStringEntity getTowards() {
        return towards;
    }

    public void setTowards(MultilingualStringEntity value) {
        this.towards = value;
    }

    public MultilingualStringEntity getBack() {
        return back;
    }

    public void setBack(MultilingualStringEntity value) {
        this.back = value;
    }

    public BigInteger getNumberOfSteps() {
        return numberOfSteps;
    }

    public void setNumberOfSteps(BigInteger value) {
        this.numberOfSteps = value;
    }

    public PathDirectionEnumeration getAllowedUse() {
        return allowedUse;
    }

    public void setAllowedUse(PathDirectionEnumeration value) {
        this.allowedUse = value;
    }

    public TransitionEnumeration getTransition() {
        return transition;
    }

    public void setTransition(TransitionEnumeration value) {
        this.transition = value;
    }

    public AccessFeatureEnumeration getAccessFeatureType() {
        return accessFeatureType;
    }

    public void setAccessFeatureType(AccessFeatureEnumeration value) {
        this.accessFeatureType = value;
    }

    public PassageTypeEnumeration getPassageType() {
        return passageType;
    }

    public void setPassageType(PassageTypeEnumeration value) {
        this.passageType = value;
    }

    public BigInteger getMaximumFlowPerMinute() {
        return maximumFlowPerMinute;
    }

    public void setMaximumFlowPerMinute(BigInteger value) {
        this.maximumFlowPerMinute = value;
    }

    public TransferDuration getTransferDuration() {
        return transferDuration;
    }

    public void setTransferDuration(TransferDuration value) {
        this.transferDuration = value;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("id", id)
                .add("name", name)
                .add("lineString", getLineString())
                .add("from", from)
                .add("to", to)
                .toString();
    }

}

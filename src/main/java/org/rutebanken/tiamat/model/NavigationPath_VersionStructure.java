

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "from",
    "to",
    "accessibilityAssessment",
    "accessModes",
    "summaries",
    "transferDuration",
    "publicUse",
    "covered",
    "gated",
    "lighting",
    "allAreasWheelchairAccessible",
    "personCapacity",
    "facilities",
    "accessFeatureList",
    "navigationType",
    "placesInSequence",
    "pathLinksInSequence",
public class NavigationPath_VersionStructure
    extends LinkSequence_VersionStructure
{

    protected PathLinkEndStructure from;
    protected PathLinkEndStructure to;
    protected AccessibilityAssessment accessibilityAssessment;
    protected List<AccessModeEnumeration> accessModes;
    protected AccessSummaries_RelStructure summaries;
    protected TransferDurationStructure transferDuration;
    protected PublicUseEnumeration publicUse;
    protected CoveredEnumeration covered;
    protected GatedEnumeration gated;
    protected LightingEnumeration lighting;
    protected Boolean allAreasWheelchairAccessible;
    protected BigInteger personCapacity;
    protected SiteFacilitySets_RelStructure facilities;
    protected List<AccessFeatureEnumeration> accessFeatureList;
    protected NavigationTypeEnumeration navigationType;
    protected PlacesInSequence_RelStructure placesInSequence;
    protected PathLinksInSequence_RelStructure pathLinksInSequence;
    protected TransferRefs_RelStructure transfers;

    public PathLinkEndStructure getFrom() {
        return from;
    }

    public void setFrom(PathLinkEndStructure value) {
        this.from = value;
    }

    public PathLinkEndStructure getTo() {
        return to;
    }

    public void setTo(PathLinkEndStructure value) {
        this.to = value;
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

    public AccessSummaries_RelStructure getSummaries() {
        return summaries;
    }

    public void setSummaries(AccessSummaries_RelStructure value) {
        this.summaries = value;
    }

    public TransferDurationStructure getTransferDuration() {
        return transferDuration;
    }

    public void setTransferDuration(TransferDurationStructure value) {
        this.transferDuration = value;
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

    public List<AccessFeatureEnumeration> getAccessFeatureList() {
        if (accessFeatureList == null) {
            accessFeatureList = new ArrayList<AccessFeatureEnumeration>();
        }
        return this.accessFeatureList;
    }

    public NavigationTypeEnumeration getNavigationType() {
        return navigationType;
    }

    public void setNavigationType(NavigationTypeEnumeration value) {
        this.navigationType = value;
    }

    public PlacesInSequence_RelStructure getPlacesInSequence() {
        return placesInSequence;
    }

    public void setPlacesInSequence(PlacesInSequence_RelStructure value) {
        this.placesInSequence = value;
    }

    public PathLinksInSequence_RelStructure getPathLinksInSequence() {
        return pathLinksInSequence;
    }

    public void setPathLinksInSequence(PathLinksInSequence_RelStructure value) {
        this.pathLinksInSequence = value;
    }

    public TransferRefs_RelStructure getTransfers() {
        return transfers;
    }

    public void setTransfers(TransferRefs_RelStructure value) {
        this.transfers = value;
    }

}

/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


public class NavigationPath_VersionStructure
        extends LinkSequence_VersionStructure {

    protected PathLinkEnd from;
    protected PathLinkEnd to;
    protected AccessibilityAssessment accessibilityAssessment;
    protected TransferDuration transferDuration;
    protected PublicUseEnumeration publicUse;
    protected CoveredEnumeration covered;
    protected GatedEnumeration gated;
    protected LightingEnumeration lighting;
    protected Boolean allAreasWheelchairAccessible;
    protected BigInteger personCapacity;
    protected NavigationTypeEnumeration navigationType;
    protected PlacesInSequence_RelStructure placesInSequence;
    protected PathLinksInSequence_RelStructure pathLinksInSequence;

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

    public AccessibilityAssessment getAccessibilityAssessment() {
        return accessibilityAssessment;
    }

    public void setAccessibilityAssessment(AccessibilityAssessment value) {
        this.accessibilityAssessment = value;
    }

    public TransferDuration getTransferDuration() {
        return transferDuration;
    }

    public void setTransferDuration(TransferDuration value) {
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

}

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

import com.google.common.base.MoreObjects;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PathLink extends Link {

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
    protected PathLinkEnd from;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
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

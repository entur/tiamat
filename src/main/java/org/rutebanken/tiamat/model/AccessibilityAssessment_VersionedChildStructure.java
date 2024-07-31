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
import jakarta.persistence.CascadeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.rutebanken.tiamat.model.hsl.HslAccessibilityProperties;

import java.util.List;


@MappedSuperclass
public class AccessibilityAssessment_VersionedChildStructure
        extends VersionedChildStructure {

    @Enumerated(EnumType.STRING)
    protected LimitationStatusEnumeration mobilityImpairedAccess = LimitationStatusEnumeration.UNKNOWN;

    /**
     * The netex model now only support one AccessibilityLimitation.
     * This change came in NeTEx-XML 1.07.
     * So this means that we should only operate with one element in this list.
     * It could be refactored to OneToOne.
     * NRP-2076
     */
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(cascade = CascadeType.ALL)
    protected List<AccessibilityLimitation> limitations;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    protected HslAccessibilityProperties hslAccessibilityProperties;

    @Transient
    protected MultilingualStringEntity comment;

    public LimitationStatusEnumeration getMobilityImpairedAccess() {
        return mobilityImpairedAccess;
    }

    public void setMobilityImpairedAccess(LimitationStatusEnumeration value) {
        this.mobilityImpairedAccess = value;
    }

    public List<AccessibilityLimitation> getLimitations() {
        return limitations;
    }

    public void setLimitations(List<AccessibilityLimitation> value) {
        this.limitations = value;
    }

    public HslAccessibilityProperties getHslAccessibilityProperties() {
        return hslAccessibilityProperties;
    }

    public void setHslAccessibilityProperties(HslAccessibilityProperties hslAccessibilityProperties) {
        this.hslAccessibilityProperties = hslAccessibilityProperties;
    }

    public MultilingualStringEntity getComment() {
        return comment;
    }

    public void setComment(MultilingualStringEntity value) {
        this.comment = value;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("id", id)
                .add("netexId", netexId)
                .add("version", version)
                .add("mobilityImpairedAccess", mobilityImpairedAccess)
                .add("limitations", limitations)
                .toString();
    }
}

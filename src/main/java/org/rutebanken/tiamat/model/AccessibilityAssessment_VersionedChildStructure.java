package org.rutebanken.tiamat.model;

import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.util.List;


@MappedSuperclass
public class AccessibilityAssessment_VersionedChildStructure
        extends VersionedChildStructure {

    @Enumerated(EnumType.STRING)
    protected LimitationStatusEnumeration mobilityImpairedAccess;

    @OneToMany(cascade = CascadeType.ALL)
    protected List<AccessibilityLimitation> limitations;

    @Transient
    protected Suitabilities_RelStructure suitabilities;

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

    public Suitabilities_RelStructure getSuitabilities() {
        return suitabilities;
    }

    public void setSuitabilities(Suitabilities_RelStructure value) {
        this.suitabilities = value;
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

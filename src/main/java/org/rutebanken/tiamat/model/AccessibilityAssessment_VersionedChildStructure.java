

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import java.util.List;


    "mobilityImpairedAccess",
    "limitations",
    "suitabilities",
@MappedSuperclass
public class AccessibilityAssessment_VersionedChildStructure
    extends VersionedChildStructure
{

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

}

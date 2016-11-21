

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


@MappedSuperclass
public class ValidityCondition_VersionStructure
    extends DataManagedObjectStructure
{

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private MultilingualStringEntity name;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private MultilingualStringEntity description;

    @Transient
    private VersionOfObjectRefStructure conditionedObjectRef;

    @Transient
    private ValidityConditionRefStructure withConditionRef;

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity description) {
        this.description = description;
    }

    public VersionOfObjectRefStructure getConditionedObjectRef() {
        return conditionedObjectRef;
    }

    public void setConditionedObjectRef(VersionOfObjectRefStructure conditionedObjectRef) {
        this.conditionedObjectRef = conditionedObjectRef;
    }

    public ValidityConditionRefStructure getWithConditionRef() {
        return withConditionRef;
    }

    public void setWithConditionRef(ValidityConditionRefStructure withConditionRef) {
        this.withConditionRef = withConditionRef;
    }


    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity name) {
        this.name = name;
    }
}

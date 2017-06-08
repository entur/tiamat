package org.rutebanken.tiamat.model;

import javax.persistence.*;


@MappedSuperclass
public abstract class ValidityCondition extends DataManagedObjectStructure {

    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "name_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "name_lang"))
    })
    @Embedded
    private EmbeddableMultilingualString name;

    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "description_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "description_lang"))
    })
    @Embedded
    private EmbeddableMultilingualString description;

    @Transient
    private VersionOfObjectRefStructure conditionedObjectRef;

    @Transient
    private ValidityConditionRefStructure withConditionRef;

    public EmbeddableMultilingualString getDescription() {
        return description;
    }

    public void setDescription(EmbeddableMultilingualString description) {
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


    public EmbeddableMultilingualString getName() {
        return name;
    }

    public void setName(EmbeddableMultilingualString name) {
        this.name = name;
    }
}

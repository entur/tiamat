package org.rutebanken.tiamat.model;

import javax.persistence.*;

@MappedSuperclass
public abstract class GroupOfEntities_VersionStructure
        extends DataManagedObjectStructure {

    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "name_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "name_lang"))
    })
    @Embedded
    protected EmbeddableMultilingualString name;

    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "short_name_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "short_name_lang"))
    })
    @Embedded
    protected EmbeddableMultilingualString shortName;

    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "description_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "description_lang"))
    })
    @Embedded
    protected EmbeddableMultilingualString description;

    @Transient
    protected PurposeOfGroupingRefStructure purposeOfGroupingRef;

    @Transient
    protected PrivateCodeStructure privateCode;


    public GroupOfEntities_VersionStructure() {
    }

    public GroupOfEntities_VersionStructure(EmbeddableMultilingualString name) {
        this.name = name;
    }

    public EmbeddableMultilingualString getName() {
        return name;
    }

    public void setName(EmbeddableMultilingualString value) {
        this.name = value;
    }

    public EmbeddableMultilingualString getShortName() {
        return shortName;
    }

    public void setShortName(EmbeddableMultilingualString value) {
        this.shortName = value;
    }

    public EmbeddableMultilingualString getDescription() {
        return description;
    }

    public void setDescription(EmbeddableMultilingualString value) {
        this.description = value;
    }

    public PurposeOfGroupingRefStructure getPurposeOfGroupingRef() {
        return purposeOfGroupingRef;
    }

    public void setPurposeOfGroupingRef(PurposeOfGroupingRefStructure value) {
        this.purposeOfGroupingRef = value;
    }

    public PrivateCodeStructure getPrivateCode() {
        return privateCode;
    }

    public void setPrivateCode(PrivateCodeStructure value) {
        this.privateCode = value;
    }

}

package org.rutebanken.tiamat.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;

@MappedSuperclass
public abstract class GroupOfEntities_VersionStructure
        extends DataManagedObjectStructure {

    @AttributeOverrides({
            @AttributeOverride(name="value", column= @Column(name="name_value")),
            @AttributeOverride(name="lang", column= @Column(name="name_lang"))
    })
    @Embedded
    protected EmbeddableMultilingualString name;

    @AttributeOverrides({
            @AttributeOverride(name="value", column= @Column(name="short_name_value")),
            @AttributeOverride(name="lang", column= @Column(name="short_name_lang"))
    })
    @Embedded
    protected EmbeddableMultilingualString shortName;

    @AttributeOverrides({
            @AttributeOverride(name="value", column= @Column(name="description_value")),
            @AttributeOverride(name="lang", column= @Column(name="description_lang"))
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

    /**
     * Gets the value of the name property.
     *
     * @return possible object is
     * {@link MultilingualStringEntity }
     */
    public EmbeddableMultilingualString getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is
     *              {@link MultilingualStringEntity }
     */
    public void setName(EmbeddableMultilingualString value) {
        this.name = value;
    }

    /**
     * Gets the value of the shortName property.
     *
     * @return possible object is
     * {@link MultilingualStringEntity }
     */
    public EmbeddableMultilingualString getShortName() {
        return shortName;
    }

    /**
     * Sets the value of the shortName property.
     *
     * @param value allowed object is
     *              {@link MultilingualStringEntity }
     */
    public void setShortName(EmbeddableMultilingualString value) {
        this.shortName = value;
    }

    /**
     * Gets the value of the description property.
     *
     * @return possible object is
     * {@link MultilingualStringEntity }
     */
    public EmbeddableMultilingualString getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value allowed object is
     *              {@link MultilingualStringEntity }
     */
    public void setDescription(EmbeddableMultilingualString value) {
        this.description = value;
    }

    /**
     * Reference to a PURPOSE OF GROUPING.
     *
     * @return possible object is
     * {@link PurposeOfGroupingRefStructure }
     */
    public PurposeOfGroupingRefStructure getPurposeOfGroupingRef() {
        return purposeOfGroupingRef;
    }

    /**
     * Sets the value of the purposeOfGroupingRef property.
     *
     * @param value allowed object is
     *              {@link PurposeOfGroupingRefStructure }
     */
    public void setPurposeOfGroupingRef(PurposeOfGroupingRefStructure value) {
        this.purposeOfGroupingRef = value;
    }

    /**
     * Gets the value of the privateCode property.
     *
     * @return possible object is
     * {@link PrivateCodeStructure }
     */
    public PrivateCodeStructure getPrivateCode() {
        return privateCode;
    }

    /**
     * Sets the value of the privateCode property.
     *
     * @param value allowed object is
     *              {@link PrivateCodeStructure }
     */
    public void setPrivateCode(PrivateCodeStructure value) {
        this.privateCode = value;
    }

}

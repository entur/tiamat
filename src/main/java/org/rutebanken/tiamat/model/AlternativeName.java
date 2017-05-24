package org.rutebanken.tiamat.model;

import javax.persistence.*;
import java.math.BigInteger;


@Entity
public class AlternativeName
        extends VersionedChildStructure {

    protected VersionOfObjectRefStructure namedObjectRef;

    protected String lang;

    protected NameTypeEnumeration nameType;

    protected String typeOfName;

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
            @AttributeOverride(name = "value", column = @Column(name = "abbreviation_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "abbreviation_lang"))
    })
    @Embedded
    protected EmbeddableMultilingualString abbreviation;

    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "qualifier_name_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "qualifier_name_lang"))
    })
    @Embedded
    protected EmbeddableMultilingualString qualifierName;

    @Transient
    protected BigInteger order;

    public VersionOfObjectRefStructure getNamedObjectRef() {
        return namedObjectRef;
    }

    public void setNamedObjectRef(VersionOfObjectRefStructure value) {
        this.namedObjectRef = value;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String value) {
        this.lang = value;
    }

    public NameTypeEnumeration getNameType() {
        return nameType;
    }

    public void setNameType(NameTypeEnumeration value) {
        this.nameType = value;
    }

    public String getTypeOfName() {
        return typeOfName;
    }

    public void setTypeOfName(String value) {
        this.typeOfName = value;
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

    public EmbeddableMultilingualString getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(EmbeddableMultilingualString value) {
        this.abbreviation = value;
    }

    public EmbeddableMultilingualString getQualifierName() {
        return qualifierName;
    }

    public void setQualifierName(EmbeddableMultilingualString value) {
        this.qualifierName = value;
    }

    public BigInteger getOrder() {
        return order;
    }

    public void setOrder(BigInteger value) {
        this.order = value;
    }

}

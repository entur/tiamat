package org.rutebanken.tiamat.model;

import javax.persistence.*;
import java.math.BigInteger;


@MappedSuperclass
public class AlternativeName_VersionedChildStructure
        extends VersionedChildStructure {

    protected VersionOfObjectRefStructure namedObjectRef;

    protected String lang;

    protected NameTypeEnumeration nameType;

    protected String typeOfName;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected MultilingualStringEntity name;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected MultilingualStringEntity shortName;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected MultilingualStringEntity abbreviation;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected MultilingualStringEntity qualifierName;

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

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public MultilingualStringEntity getShortName() {
        return shortName;
    }

    public void setShortName(MultilingualStringEntity value) {
        this.shortName = value;
    }

    public MultilingualStringEntity getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(MultilingualStringEntity value) {
        this.abbreviation = value;
    }

    public MultilingualStringEntity getQualifierName() {
        return qualifierName;
    }

    public void setQualifierName(MultilingualStringEntity value) {
        this.qualifierName = value;
    }

    public BigInteger getOrder() {
        return order;
    }

    public void setOrder(BigInteger value) {
        this.order = value;
    }

}

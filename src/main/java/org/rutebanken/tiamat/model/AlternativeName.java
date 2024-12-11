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
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Transient;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.math.BigInteger;


@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AlternativeName
        extends VersionedChildStructure {

    protected VersionOfObjectRefStructure namedObjectRef;

    protected String lang;

    @Enumerated(EnumType.STRING)
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("netexId", netexId)
                .add("lang", lang)
                .add("nameType", nameType)
                .add("name", name)
                .add("shortName", shortName)
                .add("abbreviation", abbreviation)
                .add("order", order)
                .toString();
    }
}

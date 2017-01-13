package org.rutebanken.tiamat.model;

import graphql.annotations.GraphQLField;
import graphql.annotations.GraphQLType;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
@GraphQLType
public abstract class MultilingualString {

    @GraphQLField
    protected String value;

    @Column(length = 5)
    @GraphQLField
    protected String lang;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String value) {
        this.lang = value;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null) return false;
        if (!(object instanceof MultilingualString)) return false;

        MultilingualString other = (MultilingualString) object;

        return Objects.equals(this.value, other.value)
                && Objects.equals(this.lang, other.lang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.lang, this.value);
    }

    @Override
    public String toString() {
        return getValue() + " (" + lang + ")";
    }
}

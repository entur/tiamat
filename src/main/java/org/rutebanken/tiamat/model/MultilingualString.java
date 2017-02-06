package org.rutebanken.tiamat.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;

@MappedSuperclass
public abstract class MultilingualString implements Serializable {

    protected String value;

    @Column(length = 5)
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

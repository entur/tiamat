package org.rutebanken.tiamat.model;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Objects;

@Entity
@Table(name = "multilingual_string", indexes = {@Index(name = "multilingual_value", columnList = "value")})
public class MultilingualString {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    protected String value;

    protected String lang;

    protected String textIdType;

    public MultilingualString() {}

    public MultilingualString(String value, String lang, String textIdType) {
        this.value = value;
        this.lang = lang;
        this.textIdType = textIdType;
    }

    public MultilingualString(String value) {
        this.value = value;
    }

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

    public String getTextIdType() {
        return textIdType;
    }

    public void setTextIdType(String value) {
        this.textIdType = value;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null) return false;
        if(!(object instanceof MultilingualString)) return false;

        MultilingualString other = (MultilingualString) object;

        return Objects.equals(this.value, other.value)
                && Objects.equals(this.lang, other.lang)
                && Objects.equals(this.textIdType, other.textIdType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.lang, this.value, this.textIdType);
    }

    @Override
    public String toString() {
        return getValue() +" ("+lang+")";
    }
}

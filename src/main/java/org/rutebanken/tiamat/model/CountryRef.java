package org.rutebanken.tiamat.model;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


@Embeddable
public class CountryRef {

    protected String value;
    
    @Enumerated(EnumType.STRING)
    protected IanaCountryTldEnumeration ref;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public IanaCountryTldEnumeration getRef() {
        return ref;
    }

    public void setRef(IanaCountryTldEnumeration value) {
        this.ref = value;
    }

}

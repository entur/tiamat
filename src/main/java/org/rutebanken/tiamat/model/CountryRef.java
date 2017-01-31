package org.rutebanken.tiamat.model;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;


@Embeddable
public class CountryRef implements Serializable{

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

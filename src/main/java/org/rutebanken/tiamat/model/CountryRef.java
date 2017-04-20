package org.rutebanken.tiamat.model;

import com.google.common.base.MoreObjects;

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


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("value", value)
                .add("ref", ref)
                .toString();
    }
}

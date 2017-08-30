package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;

public class Country_VersionStructure
        extends Place {

    protected PrivateCodeStructure uicCode;
    protected MultilingualStringEntity countryName;

    private final List<AlternativeName> alternativeNames = new ArrayList<>();

    public PrivateCodeStructure getUicCode() {
        return uicCode;
    }

    public void setUicCode(PrivateCodeStructure value) {
        this.uicCode = value;
    }

    public MultilingualStringEntity getCountryName() {
        return countryName;
    }

    public void setCountryName(MultilingualStringEntity value) {
        this.countryName = value;
    }


    public List<AlternativeName> getAlternativeNames() {
        return alternativeNames;
    }
}

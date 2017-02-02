package org.rutebanken.tiamat.model;

public class Country_VersionStructure
        extends Place {

    protected PrivateCodeStructure uicCode;
    protected MultilingualStringEntity countryName;
    protected AlternativeNames_RelStructure alternativeNames;

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

    public AlternativeNames_RelStructure getAlternativeNames() {
        return alternativeNames;
    }

    public void setAlternativeNames(AlternativeNames_RelStructure value) {
        this.alternativeNames = value;
    }

}

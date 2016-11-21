package org.rutebanken.tiamat.model;

public class Address_VersionStructure
        extends Place_VersionStructure {

    protected CountryRef countryRef;
    protected MultilingualStringEntity countryName;

    public CountryRef getCountryRef() {
        return countryRef;
    }

    public void setCountryRef(CountryRef value) {
        this.countryRef = value;
    }

    public MultilingualStringEntity getCountryName() {
        return countryName;
    }

    public void setCountryName(MultilingualStringEntity value) {
        this.countryName = value;
    }

}

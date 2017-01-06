package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class CountryRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<CountryRef> countryRef;

    public List<CountryRef> getCountryRef() {
        if (countryRef == null) {
            countryRef = new ArrayList<CountryRef>();
        }
        return this.countryRef;
    }

}



package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class CountryRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<CountryRef> countryRef;

    public List<CountryRef> getCountryRef() {
        if (countryRef == null) {
            countryRef = new ArrayList<CountryRef>();
        }
        return this.countryRef;
    }

}

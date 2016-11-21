

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "uicCode",
    "countryName",
public class Country_VersionStructure
    extends Place_VersionStructure
{

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



package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


public class ActivationPoint_VersionStructure
    extends Point_VersionStructure
{

    protected String activationPointNumber;
    protected MultilingualStringEntity shortName;
    protected PrivateCodeStructure privateCode;
    protected TypeOfActivationRefStructure typeOfActivationRef;

    public String getActivationPointNumber() {
        return activationPointNumber;
    }

    public void setActivationPointNumber(String value) {
        this.activationPointNumber = value;
    }

    public MultilingualStringEntity getShortName() {
        return shortName;
    }

    public void setShortName(MultilingualStringEntity value) {
        this.shortName = value;
    }

    public PrivateCodeStructure getPrivateCode() {
        return privateCode;
    }

    public void setPrivateCode(PrivateCodeStructure value) {
        this.privateCode = value;
    }

    public TypeOfActivationRefStructure getTypeOfActivationRef() {
        return typeOfActivationRef;
    }

    public void setTypeOfActivationRef(TypeOfActivationRefStructure value) {
        this.typeOfActivationRef = value;
    }

}

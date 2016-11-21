

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


public class InfoLinkStructure {

    protected String value;
    protected TypeOfInfolinkEnumeration typeOfInfoLink;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TypeOfInfolinkEnumeration getTypeOfInfoLink() {
        return typeOfInfoLink;
    }

    public void setTypeOfInfoLink(TypeOfInfolinkEnumeration value) {
        this.typeOfInfoLink = value;
    }

}

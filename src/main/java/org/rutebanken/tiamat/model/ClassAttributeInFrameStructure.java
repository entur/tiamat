

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


    "type",
public class ClassAttributeInFrameStructure {

    protected String type;
    protected MandatoryEnumeration mandatory;
    protected String name;

    public String getType() {
        return type;
    }

    public void setType(String value) {
        this.type = value;
    }

    public MandatoryEnumeration getMandatory() {
        return mandatory;
    }

    public void setMandatory(MandatoryEnumeration value) {
        this.mandatory = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

}

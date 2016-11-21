

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


    "name",
public class ValueSet_VersionStructure
    extends DataManagedObjectStructure
{

    protected MultilingualStringEntity name;
    protected TypesOfValueStructure values;
    protected String classOfValues;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public TypesOfValueStructure getValues() {
        return values;
    }

    public void setValues(TypesOfValueStructure value) {
        this.values = value;
    }

    public String getClassOfValues() {
        return classOfValues;
    }

    public void setClassOfValues(String value) {
    }

}

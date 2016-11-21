

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


public class TypeOfEntity_VersionStructure
    extends TypeOfValue_VersionStructure
{

    protected String nameOfClassifiedEntityClass;

    public String getNameOfClassifiedEntityClass() {
        return nameOfClassifiedEntityClass;
    }

    public void setNameOfClassifiedEntityClass(String value) {
        this.nameOfClassifiedEntityClass = value;
    }

}

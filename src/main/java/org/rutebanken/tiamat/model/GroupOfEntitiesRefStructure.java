package org.rutebanken.tiamat.model;

import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@MappedSuperclass
public abstract class GroupOfEntitiesRefStructure extends VersionOfObjectRefStructure {

    protected String nameOfMemberClass;

    public String getNameOfMemberClass() {
        return nameOfMemberClass;
    }

    public void setNameOfMemberClass(String value) {
        this.nameOfMemberClass = value;
    }
}

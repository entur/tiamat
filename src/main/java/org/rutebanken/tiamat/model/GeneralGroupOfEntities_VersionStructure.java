

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


public class GeneralGroupOfEntities_VersionStructure
    extends GroupOfEntities_VersionStructure
{

    protected ObjectRefs_RelStructure members;
    protected String nameOfMemberClass;

    public ObjectRefs_RelStructure getMembers() {
        return members;
    }

    public void setMembers(ObjectRefs_RelStructure value) {
        this.members = value;
    }

    public String getNameOfMemberClass() {
        return nameOfMemberClass;
    }

    public void setNameOfMemberClass(String value) {
        this.nameOfMemberClass = value;
    }

}

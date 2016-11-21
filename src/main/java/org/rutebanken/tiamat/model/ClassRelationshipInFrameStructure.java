

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


    "relationshipRef",
    "mandatory",
    "containment",
public class ClassRelationshipInFrameStructure {

    protected RelationshipRefStructure relationshipRef;
    protected MandatoryEnumeration mandatory;
    protected ContainmentEnumeration containment;
    protected ModificationSetEnumeration modificationSet;
    protected String name;

    public RelationshipRefStructure getRelationshipRef() {
        return relationshipRef;
    }

    public void setRelationshipRef(RelationshipRefStructure value) {
        this.relationshipRef = value;
    }

    public MandatoryEnumeration getMandatory() {
        return mandatory;
    }

    public void setMandatory(MandatoryEnumeration value) {
        this.mandatory = value;
    }

    public ContainmentEnumeration getContainment() {
        return containment;
    }

    public void setContainment(ContainmentEnumeration value) {
        this.containment = value;
    }

    public ModificationSetEnumeration getModificationSet() {
        return modificationSet;
    }

    public void setModificationSet(ModificationSetEnumeration value) {
        this.modificationSet = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

}

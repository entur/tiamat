

package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class PurposeOfGrouping_ValueStructure
    extends TypeOfValue_VersionStructure
{

    protected ClassRefs_RelStructure classes;
    protected JAXBElement<? extends TypeOfEntity_VersionStructure> typeOfEntity;

    public ClassRefs_RelStructure getClasses() {
        return classes;
    }

    public void setClasses(ClassRefs_RelStructure value) {
    }

    public JAXBElement<? extends TypeOfEntity_VersionStructure> getTypeOfEntity() {
        return typeOfEntity;
    }

    public void setTypeOfEntity(JAXBElement<? extends TypeOfEntity_VersionStructure> value) {
        this.typeOfEntity = value;
    }

}

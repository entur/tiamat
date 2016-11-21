

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "typeOfOperationRef",
public class Department_VersionStructure
    extends OrganisationPart_VersionStructure
{

    protected TypeOfOperationRefStructure typeOfOperationRef;
    protected OrganisationalUnitRefs_RelStructure units;

    public TypeOfOperationRefStructure getTypeOfOperationRef() {
        return typeOfOperationRef;
    }

    public void setTypeOfOperationRef(TypeOfOperationRefStructure value) {
        this.typeOfOperationRef = value;
    }

    public OrganisationalUnitRefs_RelStructure getUnits() {
        return units;
    }

    public void setUnits(OrganisationalUnitRefs_RelStructure value) {
        this.units = value;
    }

}

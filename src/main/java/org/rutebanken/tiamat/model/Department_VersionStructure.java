package org.rutebanken.tiamat.model;

public class Department_VersionStructure
        extends OrganisationPart_VersionStructure {

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

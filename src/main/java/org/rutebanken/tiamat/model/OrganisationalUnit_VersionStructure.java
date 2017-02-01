package org.rutebanken.tiamat.model;

public class OrganisationalUnit_VersionStructure
        extends OrganisationPart_VersionStructure {

    protected DepartmentRefStructure departmentRef;

    public DepartmentRefStructure getDepartmentRef() {
        return departmentRef;
    }

    public void setDepartmentRef(DepartmentRefStructure value) {
        this.departmentRef = value;
    }

}

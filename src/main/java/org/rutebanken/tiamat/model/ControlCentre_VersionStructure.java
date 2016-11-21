package org.rutebanken.tiamat.model;

import java.math.BigInteger;


public class ControlCentre_VersionStructure
        extends OrganisationPart_VersionStructure {

    protected BigInteger number;
    protected MultilingualStringEntity controlCentreCode;
    protected DepartmentRefStructure departmentRef;

    public BigInteger getNumber() {
        return number;
    }

    public void setNumber(BigInteger value) {
        this.number = value;
    }

    public MultilingualStringEntity getControlCentreCode() {
        return controlCentreCode;
    }

    public void setControlCentreCode(MultilingualStringEntity value) {
        this.controlCentreCode = value;
    }

    public DepartmentRefStructure getDepartmentRef() {
        return departmentRef;
    }

    public void setDepartmentRef(DepartmentRefStructure value) {
        this.departmentRef = value;
    }

}

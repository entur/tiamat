

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class ControlCentre_VersionStructure
    extends OrganisationPart_VersionStructure
{

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

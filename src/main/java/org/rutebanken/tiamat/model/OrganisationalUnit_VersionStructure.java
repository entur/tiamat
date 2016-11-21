

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class OrganisationalUnit_VersionStructure
    extends OrganisationPart_VersionStructure
{

    protected DepartmentRefStructure departmentRef;

    public DepartmentRefStructure getDepartmentRef() {
        return departmentRef;
    }

    public void setDepartmentRef(DepartmentRefStructure value) {
        this.departmentRef = value;
    }

}

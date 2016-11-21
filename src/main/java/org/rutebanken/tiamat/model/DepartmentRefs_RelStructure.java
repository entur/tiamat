

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class DepartmentRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<DepartmentRefStructure> departmentRef;

    public List<DepartmentRefStructure> getDepartmentRef() {
        if (departmentRef == null) {
            departmentRef = new ArrayList<DepartmentRefStructure>();
        }
        return this.departmentRef;
    }

}

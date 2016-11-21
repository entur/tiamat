package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class DepartmentRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<DepartmentRefStructure> departmentRef;

    public List<DepartmentRefStructure> getDepartmentRef() {
        if (departmentRef == null) {
            departmentRef = new ArrayList<DepartmentRefStructure>();
        }
        return this.departmentRef;
    }

}

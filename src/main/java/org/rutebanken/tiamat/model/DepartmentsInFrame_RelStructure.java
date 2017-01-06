package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class DepartmentsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Department> department;

    public List<Department> getDepartment() {
        if (department == null) {
            department = new ArrayList<Department>();
        }
        return this.department;
    }

}

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class Departments_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> departmentRefOrDepartment;

    public List<Object> getDepartmentRefOrDepartment() {
        if (departmentRefOrDepartment == null) {
            departmentRefOrDepartment = new ArrayList<Object>();
        }
        return this.departmentRefOrDepartment;
    }

}

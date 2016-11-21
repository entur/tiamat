

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class DepartmentsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<Department> department;

    public List<Department> getDepartment() {
        if (department == null) {
            department = new ArrayList<Department>();
        }
        return this.department;
    }

}

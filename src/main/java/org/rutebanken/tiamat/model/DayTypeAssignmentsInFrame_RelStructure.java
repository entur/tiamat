

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class DayTypeAssignmentsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<DayTypeAssignment> dayTypeAssignment;

    public List<DayTypeAssignment> getDayTypeAssignment() {
        if (dayTypeAssignment == null) {
            dayTypeAssignment = new ArrayList<DayTypeAssignment>();
        }
        return this.dayTypeAssignment;
    }

}

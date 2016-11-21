

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class TimeDemandTypeAssignmentsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<TimeDemandTypeAssignment> timeDemandTypeAssignment;

    public List<TimeDemandTypeAssignment> getTimeDemandTypeAssignment() {
        if (timeDemandTypeAssignment == null) {
            timeDemandTypeAssignment = new ArrayList<TimeDemandTypeAssignment>();
        }
        return this.timeDemandTypeAssignment;
    }

}



package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class ContainedAvailabilityConditions_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<AvailabilityCondition> availabilityCondition;

    public List<AvailabilityCondition> getAvailabilityCondition() {
        if (availabilityCondition == null) {
            availabilityCondition = new ArrayList<AvailabilityCondition>();
        }
        return this.availabilityCondition;
    }

}

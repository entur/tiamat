

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class OperatingDaysInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<OperatingDay> operatingDay;

    public List<OperatingDay> getOperatingDay() {
        if (operatingDay == null) {
            operatingDay = new ArrayList<OperatingDay>();
        }
        return this.operatingDay;
    }

}

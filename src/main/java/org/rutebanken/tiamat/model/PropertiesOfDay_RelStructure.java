

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class PropertiesOfDay_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<PropertyOfDay> propertyOfDay;

    public List<PropertyOfDay> getPropertyOfDay() {
        if (propertyOfDay == null) {
            propertyOfDay = new ArrayList<PropertyOfDay>();
        }
        return this.propertyOfDay;
    }

}

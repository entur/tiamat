package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class PropertiesOfDay_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<PropertyOfDay> propertyOfDay;

    public List<PropertyOfDay> getPropertyOfDay() {
        if (propertyOfDay == null) {
            propertyOfDay = new ArrayList<PropertyOfDay>();
        }
        return this.propertyOfDay;
    }

}

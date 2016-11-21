

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class GaragePoints_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<Object> garagePointRefOrGaragePoint;

    public List<Object> getGaragePointRefOrGaragePoint() {
        if (garagePointRefOrGaragePoint == null) {
            garagePointRefOrGaragePoint = new ArrayList<Object>();
        }
        return this.garagePointRefOrGaragePoint;
    }

}

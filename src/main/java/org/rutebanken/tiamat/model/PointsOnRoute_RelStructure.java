

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class PointsOnRoute_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<PointOnRoute> pointOnRoute;

    public List<PointOnRoute> getPointOnRoute() {
        if (pointOnRoute == null) {
            pointOnRoute = new ArrayList<PointOnRoute>();
        }
        return this.pointOnRoute;
    }

}



package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class PointsOfInterestInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<PointOfInterest> pointOfInterest;

    public List<PointOfInterest> getPointOfInterest() {
        if (pointOfInterest == null) {
            pointOfInterest = new ArrayList<PointOfInterest>();
        }
        return this.pointOfInterest;
    }

}

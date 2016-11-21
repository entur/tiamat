

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class PointOfInterestClassificationsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<PointOfInterestClassification> pointOfInterestClassification;

    public List<PointOfInterestClassification> getPointOfInterestClassification() {
        if (pointOfInterestClassification == null) {
            pointOfInterestClassification = new ArrayList<PointOfInterestClassification>();
        }
        return this.pointOfInterestClassification;
    }

}

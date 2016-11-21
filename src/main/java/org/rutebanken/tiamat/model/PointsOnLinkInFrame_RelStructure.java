

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class PointsOnLinkInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<PointOnLink> pointOnLink;

    public List<PointOnLink> getPointOnLink() {
        if (pointOnLink == null) {
            pointOnLink = new ArrayList<PointOnLink>();
        }
        return this.pointOnLink;
    }

}

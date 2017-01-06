package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class PointsOnLinkInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<PointOnLink> pointOnLink;

    public List<PointOnLink> getPointOnLink() {
        if (pointOnLink == null) {
            pointOnLink = new ArrayList<PointOnLink>();
        }
        return this.pointOnLink;
    }

}

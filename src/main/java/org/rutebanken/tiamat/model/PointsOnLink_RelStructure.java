package org.rutebanken.tiamat.model;

import net.opengis.gml._3.LineStringType;

import java.util.ArrayList;
import java.util.List;


public class PointsOnLink_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<PointOnLink> pointOnLink;
    protected LineStringType lineString;

    public List<PointOnLink> getPointOnLink() {
        if (pointOnLink == null) {
            pointOnLink = new ArrayList<PointOnLink>();
        }
        return this.pointOnLink;
    }

    public LineStringType getLineString() {
        return lineString;
    }

    public void setLineString(LineStringType value) {
        this.lineString = value;
    }

}

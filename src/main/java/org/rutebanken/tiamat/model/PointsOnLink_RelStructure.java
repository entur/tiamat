package org.rutebanken.tiamat.model;

import net.opengis.gml._3.DeprecatedLineStringType;

import java.util.ArrayList;
import java.util.List;


public class PointsOnLink_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<PointOnLink> pointOnLink;
    protected DeprecatedLineStringType lineString;

    public List<PointOnLink> getPointOnLink() {
        if (pointOnLink == null) {
            pointOnLink = new ArrayList<PointOnLink>();
        }
        return this.pointOnLink;
    }

    public DeprecatedLineStringType getLineString() {
        return lineString;
    }

    public void setLineString(DeprecatedLineStringType value) {
        this.lineString = value;
    }

}

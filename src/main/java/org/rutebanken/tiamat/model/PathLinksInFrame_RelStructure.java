package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class PathLinksInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<PathLink> pathLink;

    public List<PathLink> getPathLink() {
        if (pathLink == null) {
            pathLink = new ArrayList<PathLink>();
        }
        return this.pathLink;
    }

}

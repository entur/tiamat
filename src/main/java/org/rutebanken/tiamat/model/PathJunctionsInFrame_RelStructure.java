package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class PathJunctionsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<PathJunction> pathJunction;

    public List<PathJunction> getPathJunction() {
        if (pathJunction == null) {
            pathJunction = new ArrayList<PathJunction>();
        }
        return this.pathJunction;
    }

}

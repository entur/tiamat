package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class AllowedLineDirections_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> allowedLineDirectionRefOrAllowedLineDirection;

    public List<Object> getAllowedLineDirectionRefOrAllowedLineDirection() {
        if (allowedLineDirectionRefOrAllowedLineDirection == null) {
            allowedLineDirectionRefOrAllowedLineDirection = new ArrayList<Object>();
        }
        return this.allowedLineDirectionRefOrAllowedLineDirection;
    }

}

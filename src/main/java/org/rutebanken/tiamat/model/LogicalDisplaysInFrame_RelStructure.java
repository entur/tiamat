package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class LogicalDisplaysInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<LogicalDisplay> logicalDisplay;

    public List<LogicalDisplay> getLogicalDisplay() {
        if (logicalDisplay == null) {
            logicalDisplay = new ArrayList<LogicalDisplay>();
        }
        return this.logicalDisplay;
    }

}

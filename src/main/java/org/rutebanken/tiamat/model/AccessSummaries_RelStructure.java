package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class AccessSummaries_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<AccessSummary> accessSummary;

    public List<AccessSummary> getAccessSummary() {
        if (accessSummary == null) {
            accessSummary = new ArrayList<AccessSummary>();
        }
        return this.accessSummary;
    }

}

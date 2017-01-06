package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class VersionsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Version> version;

    public List<Version> getVersion() {
        if (version == null) {
            version = new ArrayList<Version>();
        }
        return this.version;
    }

}

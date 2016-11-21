package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class Versions_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> versionRefOrVersion;

    public List<Object> getVersionRefOrVersion() {
        if (versionRefOrVersion == null) {
            versionRefOrVersion = new ArrayList<Object>();
        }
        return this.versionRefOrVersion;
    }

}

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class AccessZones_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> accessZoneRefOrAccessZone;

    public List<Object> getAccessZoneRefOrAccessZone() {
        if (accessZoneRefOrAccessZone == null) {
            accessZoneRefOrAccessZone = new ArrayList<Object>();
        }
        return this.accessZoneRefOrAccessZone;
    }

}

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TransportModes_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> openTransportModeRefOrTransportMode;

    public List<Object> getOpenTransportModeRefOrTransportMode() {
        if (openTransportModeRefOrTransportMode == null) {
            openTransportModeRefOrTransportMode = new ArrayList<Object>();
        }
        return this.openTransportModeRefOrTransportMode;
    }

}

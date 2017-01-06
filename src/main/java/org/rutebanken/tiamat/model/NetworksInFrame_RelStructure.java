package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class NetworksInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Network> network;

    public List<Network> getNetwork() {
        if (network == null) {
            network = new ArrayList<Network>();
        }
        return this.network;
    }

}

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class LineNetworksInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<LineNetwork> lineNetwork;

    public List<LineNetwork> getLineNetwork() {
        if (lineNetwork == null) {
            lineNetwork = new ArrayList<LineNetwork>();
        }
        return this.lineNetwork;
    }

}

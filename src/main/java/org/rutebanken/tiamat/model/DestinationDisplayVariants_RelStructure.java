package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class DestinationDisplayVariants_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<DestinationDisplayVariant> destinationDisplayVariant;

    public List<DestinationDisplayVariant> getDestinationDisplayVariant() {
        if (destinationDisplayVariant == null) {
            destinationDisplayVariant = new ArrayList<DestinationDisplayVariant>();
        }
        return this.destinationDisplayVariant;
    }

}

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class DestinationDisplayVariantRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<DestinationDisplayVariantRefStructure> destinationDisplayVariantRef;

    public List<DestinationDisplayVariantRefStructure> getDestinationDisplayVariantRef() {
        if (destinationDisplayVariantRef == null) {
            destinationDisplayVariantRef = new ArrayList<DestinationDisplayVariantRefStructure>();
        }
        return this.destinationDisplayVariantRef;
    }

}

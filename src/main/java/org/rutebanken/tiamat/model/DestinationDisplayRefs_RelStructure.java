package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class DestinationDisplayRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<DestinationDisplayRefStructure> destinationDisplayRef;

    public List<DestinationDisplayRefStructure> getDestinationDisplayRef() {
        if (destinationDisplayRef == null) {
            destinationDisplayRef = new ArrayList<DestinationDisplayRefStructure>();
        }
        return this.destinationDisplayRef;
    }

}

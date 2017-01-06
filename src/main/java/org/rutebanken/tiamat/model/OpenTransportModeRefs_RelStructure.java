package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class OpenTransportModeRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<OpenTransportModeRefStructure> openTransportModeRef;

    public List<OpenTransportModeRefStructure> getOpenTransportModeRef() {
        if (openTransportModeRef == null) {
            openTransportModeRef = new ArrayList<OpenTransportModeRefStructure>();
        }
        return this.openTransportModeRef;
    }

}

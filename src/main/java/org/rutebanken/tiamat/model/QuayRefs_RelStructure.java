package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class QuayRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<QuayReference> quayRef;

    public List<QuayReference> getQuayRef() {
        if (quayRef == null) {
            quayRef = new ArrayList<QuayReference>();
        }
        return this.quayRef;
    }

}

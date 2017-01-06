package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class AccessRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<AccessRefStructure> accessRef;

    public List<AccessRefStructure> getAccessRef() {
        if (accessRef == null) {
            accessRef = new ArrayList<AccessRefStructure>();
        }
        return this.accessRef;
    }

}

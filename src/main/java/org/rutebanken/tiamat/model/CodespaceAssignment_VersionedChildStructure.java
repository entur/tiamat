package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class CodespaceAssignment_VersionedChildStructure
        extends VersionedChildStructure {

    protected List<Object> rest;

    public List<Object> getRest() {
        if (rest == null) {
            rest = new ArrayList<Object>();
        }
        return this.rest;
    }

}

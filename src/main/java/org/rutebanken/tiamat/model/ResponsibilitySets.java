package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ResponsibilitySets {

    protected List<ResponsibilitySet> responsibilitySet;

    public List<ResponsibilitySet> getResponsibilitySet() {
        if (responsibilitySet == null) {
            responsibilitySet = new ArrayList<ResponsibilitySet>();
        }
        return this.responsibilitySet;
    }

}

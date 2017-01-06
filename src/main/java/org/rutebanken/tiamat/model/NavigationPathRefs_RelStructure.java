package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class NavigationPathRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<NavigationPathRefStructure> navigationPathRef;

    public List<NavigationPathRefStructure> getNavigationPathRef() {
        if (navigationPathRef == null) {
            navigationPathRef = new ArrayList<NavigationPathRefStructure>();
        }
        return this.navigationPathRef;
    }

}

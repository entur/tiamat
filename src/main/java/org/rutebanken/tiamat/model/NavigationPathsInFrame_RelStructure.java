package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class NavigationPathsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<NavigationPath> navigationPath;

    public List<NavigationPath> getNavigationPath() {
        if (navigationPath == null) {
            navigationPath = new ArrayList<NavigationPath>();
        }
        return this.navigationPath;
    }

}

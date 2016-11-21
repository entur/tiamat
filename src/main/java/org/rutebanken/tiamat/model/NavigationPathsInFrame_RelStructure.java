

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class NavigationPathsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<NavigationPath> navigationPath;

    public List<NavigationPath> getNavigationPath() {
        if (navigationPath == null) {
            navigationPath = new ArrayList<NavigationPath>();
        }
        return this.navigationPath;
    }

}

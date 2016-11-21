

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class NavigationPathRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<NavigationPathRefStructure> navigationPathRef;

    public List<NavigationPathRefStructure> getNavigationPathRef() {
        if (navigationPathRef == null) {
            navigationPathRef = new ArrayList<NavigationPathRefStructure>();
        }
        return this.navigationPathRef;
    }

}

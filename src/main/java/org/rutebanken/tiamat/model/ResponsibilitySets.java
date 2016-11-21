

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class ResponsibilitySets {

    protected List<ResponsibilitySet> responsibilitySet;

    public List<ResponsibilitySet> getResponsibilitySet() {
        if (responsibilitySet == null) {
            responsibilitySet = new ArrayList<ResponsibilitySet>();
        }
        return this.responsibilitySet;
    }

}

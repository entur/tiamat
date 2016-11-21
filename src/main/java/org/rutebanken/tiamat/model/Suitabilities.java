

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class Suitabilities {

    protected List<Suitability> suitability;

    public List<Suitability> getSuitability() {
        if (suitability == null) {
            suitability = new ArrayList<Suitability>();
        }
        return this.suitability;
    }

}

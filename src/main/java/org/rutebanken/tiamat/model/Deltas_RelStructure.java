

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class Deltas_RelStructure {

    protected List<DeltaStructure> delta;

    public List<DeltaStructure> getDelta() {
        if (delta == null) {
            delta = new ArrayList<DeltaStructure>();
        }
        return this.delta;
    }

}

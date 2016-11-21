

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class DeltaValues_RelStructure {

    protected List<DeltaValueStructure> deltaValue;

    public List<DeltaValueStructure> getDeltaValue() {
        if (deltaValue == null) {
            deltaValue = new ArrayList<DeltaValueStructure>();
        }
        return this.deltaValue;
    }

}

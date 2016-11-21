

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class StopAreaRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<StopAreaRefStructure> stopAreaRef;

    public List<StopAreaRefStructure> getStopAreaRef() {
        if (stopAreaRef == null) {
            stopAreaRef = new ArrayList<StopAreaRefStructure>();
        }
        return this.stopAreaRef;
    }

}



package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class StopPlaceRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<StopPlaceReference> stopPlaceRef;

    public List<StopPlaceReference> getStopPlaceRef() {
        if (stopPlaceRef == null) {
            stopPlaceRef = new ArrayList<StopPlaceReference>();
        }
        return this.stopPlaceRef;
    }

}

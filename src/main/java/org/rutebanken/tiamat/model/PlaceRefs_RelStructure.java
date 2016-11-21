

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class PlaceRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<PlaceRef> placeRef;

    public List<PlaceRef> getPlaceRef() {
        if (placeRef == null) {
            placeRef = new ArrayList<PlaceRef>();
        }
        return this.placeRef;
    }

}



package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class PlacesInSequence_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<PlaceInSequence> placeInSequence;

    public List<PlaceInSequence> getPlaceInSequence() {
        if (placeInSequence == null) {
            placeInSequence = new ArrayList<PlaceInSequence>();
        }
        return this.placeInSequence;
    }

}



package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class FlexibleStopPlaces_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<Object> flexibleStopPlaceRefOrFlexibleStopPlace;

    public List<Object> getFlexibleStopPlaceRefOrFlexibleStopPlace() {
        if (flexibleStopPlaceRefOrFlexibleStopPlace == null) {
            flexibleStopPlaceRefOrFlexibleStopPlace = new ArrayList<Object>();
        }
        return this.flexibleStopPlaceRefOrFlexibleStopPlace;
    }

}

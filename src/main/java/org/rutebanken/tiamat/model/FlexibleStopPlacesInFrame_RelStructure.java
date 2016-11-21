

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class FlexibleStopPlacesInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<FlexibleStopPlace> flexibleStopPlace;

    public List<FlexibleStopPlace> getFlexibleStopPlace() {
        if (flexibleStopPlace == null) {
            flexibleStopPlace = new ArrayList<FlexibleStopPlace>();
        }
        return this.flexibleStopPlace;
    }

}

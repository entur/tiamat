

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class TopographicPlacesInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<TopographicPlace> topographicPlace;

    public List<TopographicPlace> getTopographicPlace() {
        if (topographicPlace == null) {
            topographicPlace = new ArrayList<TopographicPlace>();
        }
        return this.topographicPlace;
    }

}

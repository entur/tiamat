

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class GaragesInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<Garage> garage;

    public List<Garage> getGarage() {
        if (garage == null) {
            garage = new ArrayList<Garage>();
        }
        return this.garage;
    }

}

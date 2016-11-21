

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class FlexiblePointProperties_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<FlexiblePointProperties> flexiblePointProperties;

    public List<FlexiblePointProperties> getFlexiblePointProperties() {
        if (flexiblePointProperties == null) {
            flexiblePointProperties = new ArrayList<FlexiblePointProperties>();
        }
        return this.flexiblePointProperties;
    }

}

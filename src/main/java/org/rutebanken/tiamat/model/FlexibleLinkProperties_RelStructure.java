

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class FlexibleLinkProperties_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<FlexibleLinkProperties> flexibleLinkProperties;

    public List<FlexibleLinkProperties> getFlexibleLinkProperties() {
        if (flexibleLinkProperties == null) {
            flexibleLinkProperties = new ArrayList<FlexibleLinkProperties>();
        }
        return this.flexibleLinkProperties;
    }

}

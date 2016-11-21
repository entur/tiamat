

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class InfrastructureElementsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<InfrastructureLink_VersionStructure> railwayElementOrRoadElementOrWireElement;

    public List<InfrastructureLink_VersionStructure> getRailwayElementOrRoadElementOrWireElement() {
        if (railwayElementOrRoadElementOrWireElement == null) {
            railwayElementOrRoadElementOrWireElement = new ArrayList<InfrastructureLink_VersionStructure>();
        }
        return this.railwayElementOrRoadElementOrWireElement;
    }

}

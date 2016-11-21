

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class InfrastructureJunctionsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<InfrastructurePoint_VersionStructure> railwayJunctionOrRoadJunctionOrWireJunction;

    public List<InfrastructurePoint_VersionStructure> getRailwayJunctionOrRoadJunctionOrWireJunction() {
        if (railwayJunctionOrRoadJunctionOrWireJunction == null) {
            railwayJunctionOrRoadJunctionOrWireJunction = new ArrayList<InfrastructurePoint_VersionStructure>();
        }
        return this.railwayJunctionOrRoadJunctionOrWireJunction;
    }

}

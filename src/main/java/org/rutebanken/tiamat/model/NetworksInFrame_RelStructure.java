

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class NetworksInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<Network> network;

    public List<Network> getNetwork() {
        if (network == null) {
            network = new ArrayList<Network>();
        }
        return this.network;
    }

}

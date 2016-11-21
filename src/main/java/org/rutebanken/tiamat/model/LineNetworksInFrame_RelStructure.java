

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class LineNetworksInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<LineNetwork> lineNetwork;

    public List<LineNetwork> getLineNetwork() {
        if (lineNetwork == null) {
            lineNetwork = new ArrayList<LineNetwork>();
        }
        return this.lineNetwork;
    }

}

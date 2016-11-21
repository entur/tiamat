

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class TimingLinksInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<TimingLink> timingLink;

    public List<TimingLink> getTimingLink() {
        if (timingLink == null) {
            timingLink = new ArrayList<TimingLink>();
        }
        return this.timingLink;
    }

}



package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class GroupOfTimingLinksInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<GroupOfTimingLinks> groupOfTimingLinks;

    public List<GroupOfTimingLinks> getGroupOfTimingLinks() {
        if (groupOfTimingLinks == null) {
            groupOfTimingLinks = new ArrayList<GroupOfTimingLinks>();
        }
        return this.groupOfTimingLinks;
    }

}

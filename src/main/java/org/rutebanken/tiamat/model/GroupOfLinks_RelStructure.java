

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class GroupOfLinks_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<GroupOfLinks> groupOfLinks;

    public List<GroupOfLinks> getGroupOfLinks() {
        if (groupOfLinks == null) {
            groupOfLinks = new ArrayList<GroupOfLinks>();
        }
        return this.groupOfLinks;
    }

}



package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class GroupOfTimebandsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<GroupOfTimebands> groupOfTimebands;

    public List<GroupOfTimebands> getGroupOfTimebands() {
        if (groupOfTimebands == null) {
            groupOfTimebands = new ArrayList<GroupOfTimebands>();
        }
        return this.groupOfTimebands;
    }

}

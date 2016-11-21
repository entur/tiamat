

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class GroupOfTimebands_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<Object> groupOfTimebandsRefOrGroupOfTimebands;

    public List<Object> getGroupOfTimebandsRefOrGroupOfTimebands() {
        if (groupOfTimebandsRefOrGroupOfTimebands == null) {
            groupOfTimebandsRefOrGroupOfTimebands = new ArrayList<Object>();
        }
        return this.groupOfTimebandsRefOrGroupOfTimebands;
    }

}

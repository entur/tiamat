

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class GroupsOfLinesInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<GroupOfLines> groupOfLines;

    public List<GroupOfLines> getGroupOfLines() {
        if (groupOfLines == null) {
            groupOfLines = new ArrayList<GroupOfLines>();
        }
        return this.groupOfLines;
    }

}

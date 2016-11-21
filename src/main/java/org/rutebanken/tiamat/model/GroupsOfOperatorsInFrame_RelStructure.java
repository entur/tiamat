

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class GroupsOfOperatorsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<GroupOfOperators> groupOfOperators;

    public List<GroupOfOperators> getGroupOfOperators() {
        if (groupOfOperators == null) {
            groupOfOperators = new ArrayList<GroupOfOperators>();
        }
        return this.groupOfOperators;
    }

}

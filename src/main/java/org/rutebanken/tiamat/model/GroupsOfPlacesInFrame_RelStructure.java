

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class GroupsOfPlacesInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<GroupOfPlaces> groupOfPlaces;

    public List<GroupOfPlaces> getGroupOfPlaces() {
        if (groupOfPlaces == null) {
            groupOfPlaces = new ArrayList<GroupOfPlaces>();
        }
        return this.groupOfPlaces;
    }

}

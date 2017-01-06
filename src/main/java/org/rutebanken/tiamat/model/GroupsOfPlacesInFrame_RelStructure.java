package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class GroupsOfPlacesInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<GroupOfPlaces> groupOfPlaces;

    public List<GroupOfPlaces> getGroupOfPlaces() {
        if (groupOfPlaces == null) {
            groupOfPlaces = new ArrayList<GroupOfPlaces>();
        }
        return this.groupOfPlaces;
    }

}

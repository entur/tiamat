package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class PathLinkRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<Object> pathLinkRefOrPathLinkRefByValue;

    public List<Object> getPathLinkRefOrPathLinkRefByValue() {
        if (pathLinkRefOrPathLinkRefByValue == null) {
            pathLinkRefOrPathLinkRefByValue = new ArrayList<Object>();
        }
        return this.pathLinkRefOrPathLinkRefByValue;
    }

}

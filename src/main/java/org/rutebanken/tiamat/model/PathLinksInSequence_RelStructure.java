package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class PathLinksInSequence_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<PathLinkInSequence> pathLinkInSequence;

    public List<PathLinkInSequence> getPathLinkInSequence() {
        if (pathLinkInSequence == null) {
            pathLinkInSequence = new ArrayList<PathLinkInSequence>();
        }
        return this.pathLinkInSequence;
    }

}

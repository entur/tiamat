package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class StopAssignmentsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<? extends StopAssignment_VersionStructure>> stopAssignment;

    public List<JAXBElement<? extends StopAssignment_VersionStructure>> getStopAssignment() {
        if (stopAssignment == null) {
            stopAssignment = new ArrayList<JAXBElement<? extends StopAssignment_VersionStructure>>();
        }
        return this.stopAssignment;
    }

}

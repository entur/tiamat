package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TrainStopAssignments_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> trainStopAssignmentRefOrTrainStopAssignment;

    public List<Object> getTrainStopAssignmentRefOrTrainStopAssignment() {
        if (trainStopAssignmentRefOrTrainStopAssignment == null) {
            trainStopAssignmentRefOrTrainStopAssignment = new ArrayList<Object>();
        }
        return this.trainStopAssignmentRefOrTrainStopAssignment;
    }

}

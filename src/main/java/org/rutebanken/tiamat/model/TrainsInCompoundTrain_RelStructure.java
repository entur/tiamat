package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TrainsInCompoundTrain_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<TrainInCompoundTrain_VersionedChildStructure> trainInCompoundTrain;

    public List<TrainInCompoundTrain_VersionedChildStructure> getTrainInCompoundTrain() {
        if (trainInCompoundTrain == null) {
            trainInCompoundTrain = new ArrayList<TrainInCompoundTrain_VersionedChildStructure>();
        }
        return this.trainInCompoundTrain;
    }

}

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TrainComponents_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> trainComponentRefOrTrainComponent;

    public List<Object> getTrainComponentRefOrTrainComponent() {
        if (trainComponentRefOrTrainComponent == null) {
            trainComponentRefOrTrainComponent = new ArrayList<Object>();
        }
        return this.trainComponentRefOrTrainComponent;
    }

}

package org.rutebanken.tiamat.model.vehicle;

import org.rutebanken.tiamat.model.ContainmentAggregationStructure;

import java.util.ArrayList;
import java.util.List;

public class TrainComponents_RelStructure extends ContainmentAggregationStructure {

    private List<Object> trainComponentRefOrTrainComponent;

    public List<Object> getTrainComponentRefOrTrainComponent() {
        if (this.trainComponentRefOrTrainComponent == null) {
            this.trainComponentRefOrTrainComponent = new ArrayList();
        }

        return this.trainComponentRefOrTrainComponent;
    }
}

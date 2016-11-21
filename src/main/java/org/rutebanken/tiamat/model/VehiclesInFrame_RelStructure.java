package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class VehiclesInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<DataManagedObjectStructure> trainElementOrVehicle;

    public List<DataManagedObjectStructure> getTrainElementOrVehicle() {
        if (trainElementOrVehicle == null) {
            trainElementOrVehicle = new ArrayList<DataManagedObjectStructure>();
        }
        return this.trainElementOrVehicle;
    }

}

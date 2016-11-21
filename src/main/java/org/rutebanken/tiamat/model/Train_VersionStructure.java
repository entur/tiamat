package org.rutebanken.tiamat.model;

public class Train_VersionStructure
        extends VehicleType_VersionStructure {

    protected TrainSizeStructure trainSize;
    protected TrainComponents_RelStructure components;

    public TrainSizeStructure getTrainSize() {
        return trainSize;
    }

    public void setTrainSize(TrainSizeStructure value) {
        this.trainSize = value;
    }

    public TrainComponents_RelStructure getComponents() {
        return components;
    }

    public void setComponents(TrainComponents_RelStructure value) {
        this.components = value;
    }

}

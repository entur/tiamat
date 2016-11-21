

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "trainSize",
public class Train_VersionStructure
    extends VehicleType_VersionStructure
{

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

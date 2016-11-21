

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class VehiclesInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<DataManagedObjectStructure> trainElementOrVehicle;

    public List<DataManagedObjectStructure> getTrainElementOrVehicle() {
        if (trainElementOrVehicle == null) {
            trainElementOrVehicle = new ArrayList<DataManagedObjectStructure>();
        }
        return this.trainElementOrVehicle;
    }

}

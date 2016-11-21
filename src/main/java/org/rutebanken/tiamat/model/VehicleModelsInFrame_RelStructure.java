

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class VehicleModelsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<VehicleModel> vehicleModel;

    public List<VehicleModel> getVehicleModel() {
        if (vehicleModel == null) {
            vehicleModel = new ArrayList<VehicleModel>();
        }
        return this.vehicleModel;
    }

}

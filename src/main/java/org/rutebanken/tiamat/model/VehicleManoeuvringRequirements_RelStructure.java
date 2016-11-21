

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class VehicleManoeuvringRequirements_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<Object> vehicleManoeuvringRequirementRefOrVehicleManoeuvringRequirement;

    public List<Object> getVehicleManoeuvringRequirementRefOrVehicleManoeuvringRequirement() {
        if (vehicleManoeuvringRequirementRefOrVehicleManoeuvringRequirement == null) {
            vehicleManoeuvringRequirementRefOrVehicleManoeuvringRequirement = new ArrayList<Object>();
        }
        return this.vehicleManoeuvringRequirementRefOrVehicleManoeuvringRequirement;
    }

}

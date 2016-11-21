

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class VehicleEquipmenProfilesInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<VehicleEquipmentProfile> vehicleEquipmentProfile;

    public List<VehicleEquipmentProfile> getVehicleEquipmentProfile() {
        if (vehicleEquipmentProfile == null) {
            vehicleEquipmentProfile = new ArrayList<VehicleEquipmentProfile>();
        }
        return this.vehicleEquipmentProfile;
    }

}

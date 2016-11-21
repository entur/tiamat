package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class VehicleEquipments_RelStructure
        extends ContainmentAggregationStructure {

    protected List<ActualVehicleEquipment_VersionStructure> accessVehicleEquipmentOrWheelchairVehicleEquipment;

    public List<ActualVehicleEquipment_VersionStructure> getAccessVehicleEquipmentOrWheelchairVehicleEquipment() {
        if (accessVehicleEquipmentOrWheelchairVehicleEquipment == null) {
            accessVehicleEquipmentOrWheelchairVehicleEquipment = new ArrayList<ActualVehicleEquipment_VersionStructure>();
        }
        return this.accessVehicleEquipmentOrWheelchairVehicleEquipment;
    }

}

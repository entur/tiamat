package org.rutebanken.tiamat.model;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;


@Entity
public class VehicleStoppingPlaces_RelStructure extends ContainmentAggregationStructure {
    @ElementCollection(targetClass = VehicleStoppingPlaceRefStructure.class)
    protected List<VehicleStoppingPlaceRefStructure> vehicleStoppingPlaceRefOrVehicleStoppingPlace;

    public List<VehicleStoppingPlaceRefStructure> getVehicleStoppingPlaceRefOrVehicleStoppingPlace() {
        if (vehicleStoppingPlaceRefOrVehicleStoppingPlace == null) {
            vehicleStoppingPlaceRefOrVehicleStoppingPlace = new ArrayList<VehicleStoppingPlaceRefStructure>();
        }
        return this.vehicleStoppingPlaceRefOrVehicleStoppingPlace;
    }

}

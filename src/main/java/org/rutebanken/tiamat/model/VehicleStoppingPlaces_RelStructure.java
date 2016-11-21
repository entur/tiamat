

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


@Entity
public class VehicleStoppingPlaces_RelStructure
    extends ContainmentAggregationStructure
{

    }   )
    protected List<VehicleStoppingPlaceRefStructure> vehicleStoppingPlaceRefOrVehicleStoppingPlace;

    public List<VehicleStoppingPlaceRefStructure> getVehicleStoppingPlaceRefOrVehicleStoppingPlace() {
        if (vehicleStoppingPlaceRefOrVehicleStoppingPlace == null) {
            vehicleStoppingPlaceRefOrVehicleStoppingPlace = new ArrayList<VehicleStoppingPlaceRefStructure>();
        }
        return this.vehicleStoppingPlaceRefOrVehicleStoppingPlace;
    }

}

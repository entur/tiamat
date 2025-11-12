package org.rutebanken.tiamat.model.vehicle;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.rutebanken.tiamat.model.Value;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class VehicleType extends VehicleType_VersionStructure {
    @OneToOne(cascade = CascadeType.ALL)
    private PassengerCapacity passengerCapacity;

    @Override
    public void mergeWithExistingVersion(EntityInVersionStructure existingVersion) {
        if(existingVersion instanceof VehicleType) {
            if (((VehicleType) existingVersion).getKeyValues() != null) {
                ((VehicleType) existingVersion).getKeyValues().forEach((key, value) -> {
                    this.getKeyValues().put(key, new Value(value.getItems().stream().toList()));
                });
            }
        }
    }
}

package org.rutebanken.tiamat.model.vehicle;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.Value;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class VehicleType extends VehicleType_VersionStructure {
    @OneToMany(cascade = CascadeType.ALL)
    private Set<PassengerCapacity> capacities = new HashSet<>();

    @Override
    public void mergeWithExistingVersion(EntityInVersionStructure existingVersion) {
        if(existingVersion instanceof VehicleType) {
            if (((VehicleType) existingVersion).getKeyValues() != null) {
                ((VehicleType) existingVersion).getKeyValues().forEach((key, value) -> {
                    this.getKeyValues().put(key, new Value(value.toString()));
                });
            }
        }
    }
}

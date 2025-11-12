package org.rutebanken.tiamat.model.vehicle;

import jakarta.persistence.Entity;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.Value;

@Entity
public class Vehicle extends Vehicle_VersionStructure {
    @Override
    public void mergeWithExistingVersion(EntityInVersionStructure existingVersion) {
        if(existingVersion instanceof Vehicle) {
            if (((Vehicle) existingVersion).getKeyValues() != null) {
                ((Vehicle) existingVersion).getKeyValues().forEach((key, value) -> {
                    this.getKeyValues().put(key, new Value(value.getItems().stream().toList()));
                });
            }
        }
    }
}

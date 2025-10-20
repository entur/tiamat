package org.rutebanken.tiamat.model.vehicle;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class VehicleType extends VehicleType_VersionStructure {
    @OneToMany(cascade = CascadeType.ALL)
    private Set<PassengerCapacity> capacities = new HashSet<>();
}

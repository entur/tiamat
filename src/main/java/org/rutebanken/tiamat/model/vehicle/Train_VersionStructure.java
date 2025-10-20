package org.rutebanken.tiamat.model.vehicle;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public class Train_VersionStructure extends VehicleType_VersionStructure {
    @Transient
    private TrainSizeStructure trainSize;
    @Transient
    private TrainComponents_RelStructure components;

}

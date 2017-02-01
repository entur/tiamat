package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class VehicleTypesInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<VehicleType_VersionStructure> compoundTrainOrTrainOrVehicleType;

    public List<VehicleType_VersionStructure> getCompoundTrainOrTrainOrVehicleType() {
        if (compoundTrainOrTrainOrVehicleType == null) {
            compoundTrainOrTrainOrVehicleType = new ArrayList<VehicleType_VersionStructure>();
        }
        return this.compoundTrainOrTrainOrVehicleType;
    }

}

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ParkingTariffRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<VersionOfObjectRefStructure> parkingTariffRef_;

    public List<VersionOfObjectRefStructure> getParkingTariffRef_() {
        if (parkingTariffRef_ == null) {
            parkingTariffRef_ = new ArrayList<VersionOfObjectRefStructure>();
        }
        return this.parkingTariffRef_;
    }

}

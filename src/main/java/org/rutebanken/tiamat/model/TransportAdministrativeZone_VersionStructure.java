package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TransportAdministrativeZone_VersionStructure
        extends AdministrativeZone_VersionStructure {

    protected List<AllVehicleModesOfTransportEnumeration> vehicleModes;

    public List<AllVehicleModesOfTransportEnumeration> getVehicleModes() {
        if (vehicleModes == null) {
            vehicleModes = new ArrayList<AllVehicleModesOfTransportEnumeration>();
        }
        return this.vehicleModes;
    }

}

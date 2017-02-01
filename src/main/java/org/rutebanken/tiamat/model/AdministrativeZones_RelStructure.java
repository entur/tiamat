package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class AdministrativeZones_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Object> administrativeZoneRefOrAdministrativeZone;

    public List<Object> getAdministrativeZoneRefOrAdministrativeZone() {
        if (administrativeZoneRefOrAdministrativeZone == null) {
            administrativeZoneRefOrAdministrativeZone = new ArrayList<Object>();
        }
        return this.administrativeZoneRefOrAdministrativeZone;
    }

}

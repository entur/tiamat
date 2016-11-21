package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class AdministrativeZoneRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<AdministrativeZoneRef> administrativeZoneRef;

    public List<AdministrativeZoneRef> getAdministrativeZoneRef() {
        if (administrativeZoneRef == null) {
            administrativeZoneRef = new ArrayList<AdministrativeZoneRef>();
        }
        return this.administrativeZoneRef;
    }

}

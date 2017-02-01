package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class WaitingRoomEquipment_VersionStructure
        extends WaitingEquipment_VersionStructure {

    protected Boolean womenOnly;
    protected List<SanitaryFacilityEnumeration> sanitary;
    protected ClassOfUseRef classOfUseRef;

    public Boolean isWomenOnly() {
        return womenOnly;
    }

    public void setWomenOnly(Boolean value) {
        this.womenOnly = value;
    }

    public List<SanitaryFacilityEnumeration> getSanitary() {
        if (sanitary == null) {
            sanitary = new ArrayList<SanitaryFacilityEnumeration>();
        }
        return this.sanitary;
    }

    public ClassOfUseRef getClassOfUseRef() {
        return classOfUseRef;
    }

    public void setClassOfUseRef(ClassOfUseRef value) {
    }

}

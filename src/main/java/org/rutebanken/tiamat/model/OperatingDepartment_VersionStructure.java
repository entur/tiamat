package org.rutebanken.tiamat.model;

public class OperatingDepartment_VersionStructure
        extends Department_VersionStructure {

    protected OperationalContexRefs_RelStructure operationalContexts;

    public OperationalContexRefs_RelStructure getOperationalContexts() {
        return operationalContexts;
    }

    public void setOperationalContexts(OperationalContexRefs_RelStructure value) {
        this.operationalContexts = value;
    }

}

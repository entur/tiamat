

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class OperatingDepartment_VersionStructure
    extends Department_VersionStructure
{

    protected OperationalContexRefs_RelStructure operationalContexts;

    public OperationalContexRefs_RelStructure getOperationalContexts() {
        return operationalContexts;
    }

    public void setOperationalContexts(OperationalContexRefs_RelStructure value) {
        this.operationalContexts = value;
    }

}

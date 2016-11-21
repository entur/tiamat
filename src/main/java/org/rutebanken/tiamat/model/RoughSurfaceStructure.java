

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class RoughSurfaceStructure
    extends AccessEquipment_VersionStructure
{

    protected SurfaceTypeEnumeration surfaceType;
    protected Boolean suitableForCycles;

    public SurfaceTypeEnumeration getSurfaceType() {
        return surfaceType;
    }

    public void setSurfaceType(SurfaceTypeEnumeration value) {
        this.surfaceType = value;
    }

    public Boolean isSuitableForCycles() {
        return suitableForCycles;
    }

    public void setSuitableForCycles(Boolean value) {
        this.suitableForCycles = value;
    }

}



package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "accessSpaceType",
    "passageType",
public class AccessSpace_VersionStructure
    extends StopPlaceSpace_VersionStructure
{

    protected AccessSpaceTypeEnumeration accessSpaceType;
    protected PassageTypeEnumeration passageType;
    protected AccessSpaceRefStructure parentAccessSpaceRef;

    public AccessSpaceTypeEnumeration getAccessSpaceType() {
        return accessSpaceType;
    }

    public void setAccessSpaceType(AccessSpaceTypeEnumeration value) {
        this.accessSpaceType = value;
    }

    public PassageTypeEnumeration getPassageType() {
        return passageType;
    }

    public void setPassageType(PassageTypeEnumeration value) {
        this.passageType = value;
    }

    public AccessSpaceRefStructure getParentAccessSpaceRef() {
        return parentAccessSpaceRef;
    }

    public void setParentAccessSpaceRef(AccessSpaceRefStructure value) {
        this.parentAccessSpaceRef = value;
    }

}



package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "accessSpaceType",
    "pointOfInterestSpaceType",
    "passageType",
    "parentPointOfInterestSpaceRef",
public class PointOfInterestSpace_VersionStructure
    extends PointOfInterestComponent_VersionStructure
{

    protected AccessSpaceTypeEnumeration accessSpaceType;
    protected PointOfInterestSpaceTypeEnumeration pointOfInterestSpaceType;
    protected PassageTypeEnumeration passageType;
    protected PointOfInterestSpaceRefStructure parentPointOfInterestSpaceRef;
    protected PointOfInterestEntrances_RelStructure entrances;

    public AccessSpaceTypeEnumeration getAccessSpaceType() {
        return accessSpaceType;
    }

    public void setAccessSpaceType(AccessSpaceTypeEnumeration value) {
        this.accessSpaceType = value;
    }

    public PointOfInterestSpaceTypeEnumeration getPointOfInterestSpaceType() {
        return pointOfInterestSpaceType;
    }

    public void setPointOfInterestSpaceType(PointOfInterestSpaceTypeEnumeration value) {
        this.pointOfInterestSpaceType = value;
    }

    public PassageTypeEnumeration getPassageType() {
        return passageType;
    }

    public void setPassageType(PassageTypeEnumeration value) {
        this.passageType = value;
    }

    public PointOfInterestSpaceRefStructure getParentPointOfInterestSpaceRef() {
        return parentPointOfInterestSpaceRef;
    }

    public void setParentPointOfInterestSpaceRef(PointOfInterestSpaceRefStructure value) {
        this.parentPointOfInterestSpaceRef = value;
    }

    public PointOfInterestEntrances_RelStructure getEntrances() {
        return entrances;
    }

    public void setEntrances(PointOfInterestEntrances_RelStructure value) {
        this.entrances = value;
    }

}

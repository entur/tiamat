

package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class FlexibleLinkProperties_VersionedChildStructure
    extends VersionedChildStructure
{

    protected JAXBElement<? extends LinkRefStructure> linkRef;
    protected Boolean mayBeSkipped;
    protected Boolean onMainRoute;
    protected Boolean unscheduledPath;
    protected FlexibleLinkTypeEnumeration flexibleLinkType;

    public JAXBElement<? extends LinkRefStructure> getLinkRef() {
        return linkRef;
    }

    public void setLinkRef(JAXBElement<? extends LinkRefStructure> value) {
        this.linkRef = value;
    }

    public Boolean isMayBeSkipped() {
        return mayBeSkipped;
    }

    public void setMayBeSkipped(Boolean value) {
        this.mayBeSkipped = value;
    }

    public Boolean isOnMainRoute() {
        return onMainRoute;
    }

    public void setOnMainRoute(Boolean value) {
        this.onMainRoute = value;
    }

    public Boolean isUnscheduledPath() {
        return unscheduledPath;
    }

    public void setUnscheduledPath(Boolean value) {
        this.unscheduledPath = value;
    }

    public FlexibleLinkTypeEnumeration getFlexibleLinkType() {
        return flexibleLinkType;
    }

    public void setFlexibleLinkType(FlexibleLinkTypeEnumeration value) {
        this.flexibleLinkType = value;
    }

}

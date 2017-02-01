package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;


public class CommonSectionPointMember_VersionedChildStructure
        extends AbstractGroupMember_VersionedChildStructure {

    protected CommonSectionRefStructure parentCommonSectionRef;
    protected JAXBElement<? extends PointRefStructure> pointRef;
    protected JAXBElement<? extends LinkRefStructure> linkRef;
    protected Boolean reverse;

    public CommonSectionRefStructure getParentCommonSectionRef() {
        return parentCommonSectionRef;
    }

    public void setParentCommonSectionRef(CommonSectionRefStructure value) {
        this.parentCommonSectionRef = value;
    }

    public JAXBElement<? extends PointRefStructure> getPointRef() {
        return pointRef;
    }

    public void setPointRef(JAXBElement<? extends PointRefStructure> value) {
        this.pointRef = value;
    }

    public JAXBElement<? extends LinkRefStructure> getLinkRef() {
        return linkRef;
    }

    public void setLinkRef(JAXBElement<? extends LinkRefStructure> value) {
        this.linkRef = value;
    }

    public Boolean isReverse() {
        return reverse;
    }

    public void setReverse(Boolean value) {
        this.reverse = value;
    }

}

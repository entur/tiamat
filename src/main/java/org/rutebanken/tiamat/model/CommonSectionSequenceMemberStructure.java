

package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


    "parentCommonSectionRef",
public class CommonSectionSequenceMemberStructure
    extends AbstractGroupMember_VersionedChildStructure
{

    protected CommonSectionRefStructure parentCommonSectionRef;
    protected JAXBElement<? extends LinkSequenceRefStructure> linkSequenceRef;

    public CommonSectionRefStructure getParentCommonSectionRef() {
        return parentCommonSectionRef;
    }

    public void setParentCommonSectionRef(CommonSectionRefStructure value) {
        this.parentCommonSectionRef = value;
    }

    public JAXBElement<? extends LinkSequenceRefStructure> getLinkSequenceRef() {
        return linkSequenceRef;
    }

    public void setLinkSequenceRef(JAXBElement<? extends LinkSequenceRefStructure> value) {
        this.linkSequenceRef = value;
    }

}

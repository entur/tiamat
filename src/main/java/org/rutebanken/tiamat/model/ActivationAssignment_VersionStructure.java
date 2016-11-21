

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class ActivationAssignment_VersionStructure
    extends Assignment_VersionStructure
{

    protected ActivatedEquipmentRefStructure equipmentRef;
    protected ActivationLinkRefStructure linkRef;
    protected ActivationPointRefStructure pointRef;

    public ActivatedEquipmentRefStructure getEquipmentRef() {
        return equipmentRef;
    }

    public void setEquipmentRef(ActivatedEquipmentRefStructure value) {
        this.equipmentRef = value;
    }

    public ActivationLinkRefStructure getLinkRef() {
        return linkRef;
    }

    public void setLinkRef(ActivationLinkRefStructure value) {
        this.linkRef = value;
    }

    public ActivationPointRefStructure getPointRef() {
        return pointRef;
    }

    public void setPointRef(ActivationPointRefStructure value) {
        this.pointRef = value;
    }

}

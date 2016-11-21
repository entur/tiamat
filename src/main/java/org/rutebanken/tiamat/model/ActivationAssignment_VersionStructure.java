package org.rutebanken.tiamat.model;

public class ActivationAssignment_VersionStructure
        extends Assignment_VersionStructure {

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

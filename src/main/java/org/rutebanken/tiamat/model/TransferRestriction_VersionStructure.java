package org.rutebanken.tiamat.model;

public class TransferRestriction_VersionStructure
        extends Assignment_VersionStructure {

    protected TypeOfTransferRefStructure typeOfTransferRef;
    protected Boolean bothWays;
    protected TransferConstraintTypeEnumeration restrictionType;

    public TypeOfTransferRefStructure getTypeOfTransferRef() {
        return typeOfTransferRef;
    }

    public void setTypeOfTransferRef(TypeOfTransferRefStructure value) {
        this.typeOfTransferRef = value;
    }

    public Boolean isBothWays() {
        return bothWays;
    }

    public void setBothWays(Boolean value) {
        this.bothWays = value;
    }

    public TransferConstraintTypeEnumeration getRestrictionType() {
        return restrictionType;
    }

    public void setRestrictionType(TransferConstraintTypeEnumeration value) {
        this.restrictionType = value;
    }

}

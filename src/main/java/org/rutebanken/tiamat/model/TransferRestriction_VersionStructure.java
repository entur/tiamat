package org.rutebanken.tiamat.model;

public class TransferRestriction_VersionStructure
        extends Assignment_VersionStructure {

    protected TypeOfTransferRefStructure typeOfTransferRef;
    protected Boolean bothWays;
    protected TransferConstraintTypeEnumeration restrictionType;
    protected ScheduledStopPointRefStructure fromPointRef;
    protected ScheduledStopPointRefStructure toPointRef;

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

    public ScheduledStopPointRefStructure getFromPointRef() {
        return fromPointRef;
    }

    public void setFromPointRef(ScheduledStopPointRefStructure value) {
        this.fromPointRef = value;
    }

    public ScheduledStopPointRefStructure getToPointRef() {
        return toPointRef;
    }

    public void setToPointRef(ScheduledStopPointRefStructure value) {
        this.toPointRef = value;
    }

}

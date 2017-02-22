package org.rutebanken.tiamat.model;

import java.math.BigDecimal;


public abstract class Transfer_VersionStructure
        extends DataManagedObjectStructure {

    protected MultilingualStringEntity name;
    protected TypeOfTransferRefStructure typeOfTransferRef;
    protected MultilingualStringEntity description;
    protected BigDecimal distance;
    protected TransferDuration transferDuration;
    protected TransferDuration walkTransferDuration;
    protected Boolean bothWays;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public TypeOfTransferRefStructure getTypeOfTransferRef() {
        return typeOfTransferRef;
    }

    public void setTypeOfTransferRef(TypeOfTransferRefStructure value) {
        this.typeOfTransferRef = value;
    }

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal value) {
        this.distance = value;
    }

    public TransferDuration getTransferDuration() {
        return transferDuration;
    }

    public void setTransferDuration(TransferDuration value) {
        this.transferDuration = value;
    }

    public TransferDuration getWalkTransferDuration() {
        return walkTransferDuration;
    }

    public void setWalkTransferDuration(TransferDuration value) {
        this.walkTransferDuration = value;
    }

    public Boolean isBothWays() {
        return bothWays;
    }

    public void setBothWays(Boolean value) {
        this.bothWays = value;
    }

}



package org.rutebanken.tiamat.model;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "name",
    "typeOfTransferRef",
    "description",
    "distance",
    "transferDuration",
    "walkTransferDuration",
public abstract class Transfer_VersionStructure
    extends DataManagedObjectStructure
{

    protected MultilingualStringEntity name;
    protected TypeOfTransferRefStructure typeOfTransferRef;
    protected MultilingualStringEntity description;
    protected BigDecimal distance;
    protected TransferDurationStructure transferDuration;
    protected TransferDurationStructure walkTransferDuration;
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

    public TransferDurationStructure getTransferDuration() {
        return transferDuration;
    }

    public void setTransferDuration(TransferDurationStructure value) {
        this.transferDuration = value;
    }

    public TransferDurationStructure getWalkTransferDuration() {
        return walkTransferDuration;
    }

    public void setWalkTransferDuration(TransferDurationStructure value) {
        this.walkTransferDuration = value;
    }

    public Boolean isBothWays() {
        return bothWays;
    }

    public void setBothWays(Boolean value) {
        this.bothWays = value;
    }

}

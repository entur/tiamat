

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


public class TrainInCompoundTrain_VersionedChildStructure
    extends VersionedChildStructure
{

    protected MultilingualStringEntity description;
    protected CompoundTrainRef compoundTrainRef;
    protected TrainRefStructure trainRef;
    protected Train train;
    protected Boolean reversedOrientation;
    protected MultilingualStringEntity label;
    protected BigInteger order;

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public CompoundTrainRef getCompoundTrainRef() {
        return compoundTrainRef;
    }

    public void setCompoundTrainRef(CompoundTrainRef value) {
        this.compoundTrainRef = value;
    }

    public TrainRefStructure getTrainRef() {
        return trainRef;
    }

    public void setTrainRef(TrainRefStructure value) {
        this.trainRef = value;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train value) {
        this.train = value;
    }

    public Boolean isReversedOrientation() {
        return reversedOrientation;
    }

    public void setReversedOrientation(Boolean value) {
        this.reversedOrientation = value;
    }

    public MultilingualStringEntity getLabel() {
        return label;
    }

    public void setLabel(MultilingualStringEntity value) {
        this.label = value;
    }

    public BigInteger getOrder() {
        return order;
    }

    public void setOrder(BigInteger value) {
        this.order = value;
    }

}

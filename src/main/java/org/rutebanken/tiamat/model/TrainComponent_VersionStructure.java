

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class TrainComponent_VersionStructure
    extends DataManagedObjectStructure
{

    protected MultilingualStringEntity label;
    protected MultilingualStringEntity description;
    protected TrainRefStructure trainRef;
    protected TrainElementRefStructure trainElementRef;
    protected TrainElement trainElement;
    protected BigInteger order;

    public MultilingualStringEntity getLabel() {
        return label;
    }

    public void setLabel(MultilingualStringEntity value) {
        this.label = value;
    }

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public TrainRefStructure getTrainRef() {
        return trainRef;
    }

    public void setTrainRef(TrainRefStructure value) {
        this.trainRef = value;
    }

    public TrainElementRefStructure getTrainElementRef() {
        return trainElementRef;
    }

    public void setTrainElementRef(TrainElementRefStructure value) {
        this.trainElementRef = value;
    }

    public TrainElement getTrainElement() {
        return trainElement;
    }

    public void setTrainElement(TrainElement value) {
        this.trainElement = value;
    }

    public BigInteger getOrder() {
        return order;
    }

    public void setOrder(BigInteger value) {
        this.order = value;
    }

}

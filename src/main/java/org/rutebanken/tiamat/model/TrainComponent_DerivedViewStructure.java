

package org.rutebanken.tiamat.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class TrainComponent_DerivedViewStructure
    extends DerivedViewStructure
{

    protected TrainComponentRefStructure trainComponentRef;
    protected MultilingualStringEntity label;
    protected MultilingualStringEntity description;
    protected TrainElementRefStructure trainElementRef;

    protected TrainElementTypeEnumeration trainElementType;
    protected BigInteger order;

    public TrainComponentRefStructure getTrainComponentRef() {
        return trainComponentRef;
    }

    public void setTrainComponentRef(TrainComponentRefStructure value) {
        this.trainComponentRef = value;
    }

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

    public TrainElementRefStructure getTrainElementRef() {
        return trainElementRef;
    }

    public void setTrainElementRef(TrainElementRefStructure value) {
        this.trainElementRef = value;
    }


    public TrainElementTypeEnumeration getTrainElementType() {
        return trainElementType;
    }

    public void setTrainElementType(TrainElementTypeEnumeration value) {
        this.trainElementType = value;
    }

    public BigInteger getOrder() {
        return order;
    }

    public void setOrder(BigInteger value) {
        this.order = value;
    }

}



package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


    "typeOfActivationRef",
    "fromPointRef",
public class ActivationLink_VersionStructure
    extends Link_VersionStructure
{

    protected TypeOfActivationRefStructure typeOfActivationRef;
    protected ActivationPointRefStructure fromPointRef;
    protected ActivationPointRefStructure toPointRef;

    public TypeOfActivationRefStructure getTypeOfActivationRef() {
        return typeOfActivationRef;
    }

    public void setTypeOfActivationRef(TypeOfActivationRefStructure value) {
        this.typeOfActivationRef = value;
    }

    public ActivationPointRefStructure getFromPointRef() {
        return fromPointRef;
    }

    public void setFromPointRef(ActivationPointRefStructure value) {
        this.fromPointRef = value;
    }

    public ActivationPointRefStructure getToPointRef() {
        return toPointRef;
    }

    public void setToPointRef(ActivationPointRefStructure value) {
        this.toPointRef = value;
    }

}

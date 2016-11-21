

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


    "qualifierName",
public class Qualify {

    protected MultilingualStringEntity qualifierName;
    protected TopographicPlaceRefStructure topographicPlaceRef;

    public MultilingualStringEntity getQualifierName() {
        return qualifierName;
    }

    public void setQualifierName(MultilingualStringEntity value) {
        this.qualifierName = value;
    }

    public TopographicPlaceRefStructure getTopographicPlaceRef() {
        return topographicPlaceRef;
    }

    public void setTopographicPlaceRef(TopographicPlaceRefStructure value) {
        this.topographicPlaceRef = value;
    }

}



package org.rutebanken.tiamat.model;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class PointOnLinkByValueStructure {

    protected BigDecimal distanceFromStart;

    public BigDecimal getDistanceFromStart() {
        return distanceFromStart;
    }

    public void setDistanceFromStart(BigDecimal value) {
        this.distanceFromStart = value;
    }

}

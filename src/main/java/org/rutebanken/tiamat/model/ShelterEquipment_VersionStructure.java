

package org.rutebanken.tiamat.model;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class ShelterEquipment_VersionStructure
    extends WaitingEquipment_VersionStructure
{

    protected Boolean enclosed;
    protected BigDecimal distanceFromNearestKerb;

    public Boolean isEnclosed() {
        return enclosed;
    }

    public void setEnclosed(Boolean value) {
        this.enclosed = value;
    }

    public BigDecimal getDistanceFromNearestKerb() {
        return distanceFromNearestKerb;
    }

    public void setDistanceFromNearestKerb(BigDecimal value) {
        this.distanceFromNearestKerb = value;
    }

}

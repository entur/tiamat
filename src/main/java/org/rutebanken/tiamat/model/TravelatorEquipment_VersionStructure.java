

package org.rutebanken.tiamat.model;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


public class TravelatorEquipment_VersionStructure
    extends AccessEquipment_VersionStructure
{

    protected Boolean tactileActuators;
    protected Boolean energySaving;
    protected BigDecimal speed;

    public Boolean isTactileActuators() {
        return tactileActuators;
    }

    public void setTactileActuators(Boolean value) {
        this.tactileActuators = value;
    }

    public Boolean isEnergySaving() {
        return energySaving;
    }

    public void setEnergySaving(Boolean value) {
        this.energySaving = value;
    }

    public BigDecimal getSpeed() {
        return speed;
    }

    public void setSpeed(BigDecimal value) {
        this.speed = value;
    }

}

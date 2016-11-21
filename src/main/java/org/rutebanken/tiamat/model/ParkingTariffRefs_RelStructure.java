

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class ParkingTariffRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<VersionOfObjectRefStructure> parkingTariffRef_;

    public List<VersionOfObjectRefStructure> getParkingTariffRef_() {
        if (parkingTariffRef_ == null) {
            parkingTariffRef_ = new ArrayList<VersionOfObjectRefStructure>();
        }
        return this.parkingTariffRef_;
    }

}

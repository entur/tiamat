

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class PassengerInformationEquipmentsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<PassengerInformationEquipment> passengerInformationEquipment;

    public List<PassengerInformationEquipment> getPassengerInformationEquipment() {
        if (passengerInformationEquipment == null) {
            passengerInformationEquipment = new ArrayList<PassengerInformationEquipment>();
        }
        return this.passengerInformationEquipment;
    }

}

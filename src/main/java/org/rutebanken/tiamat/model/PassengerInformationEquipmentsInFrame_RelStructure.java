package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class PassengerInformationEquipmentsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<PassengerInformationEquipment> passengerInformationEquipment;

    public List<PassengerInformationEquipment> getPassengerInformationEquipment() {
        if (passengerInformationEquipment == null) {
            passengerInformationEquipment = new ArrayList<PassengerInformationEquipment>();
        }
        return this.passengerInformationEquipment;
    }

}

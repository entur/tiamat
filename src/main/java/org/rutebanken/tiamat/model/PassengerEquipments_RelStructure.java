package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class PassengerEquipments_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<?>> passengerEquipmentRefOrPassengerEquipment;

    public List<JAXBElement<?>> getPassengerEquipmentRefOrPassengerEquipment() {
        if (passengerEquipmentRefOrPassengerEquipment == null) {
            passengerEquipmentRefOrPassengerEquipment = new ArrayList<JAXBElement<?>>();
        }
        return this.passengerEquipmentRefOrPassengerEquipment;
    }

}



package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


public class PassengerEquipments_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<JAXBElement<?>> passengerEquipmentRefOrPassengerEquipment;

    public List<JAXBElement<?>> getPassengerEquipmentRefOrPassengerEquipment() {
        if (passengerEquipmentRefOrPassengerEquipment == null) {
            passengerEquipmentRefOrPassengerEquipment = new ArrayList<JAXBElement<?>>();
        }
        return this.passengerEquipmentRefOrPassengerEquipment;
    }

}

package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class EquipmentsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<? extends Equipment_VersionStructure>> equipment;

    public List<JAXBElement<? extends Equipment_VersionStructure>> getEquipment() {
        if (equipment == null) {
            equipment = new ArrayList<JAXBElement<? extends Equipment_VersionStructure>>();
        }
        return this.equipment;
    }

}

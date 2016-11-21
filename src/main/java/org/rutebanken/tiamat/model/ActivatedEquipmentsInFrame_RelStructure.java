package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ActivatedEquipmentsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<ActivatedEquipment> activatedEquipment;

    public List<ActivatedEquipment> getActivatedEquipment() {
        if (activatedEquipment == null) {
            activatedEquipment = new ArrayList<ActivatedEquipment>();
        }
        return this.activatedEquipment;
    }

}

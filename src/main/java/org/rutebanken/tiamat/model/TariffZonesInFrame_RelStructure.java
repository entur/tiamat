package org.rutebanken.tiamat.model;


import java.util.ArrayList;
import java.util.List;


public class TariffZonesInFrame_RelStructure extends ContainmentAggregationStructure {

    protected List<TariffZone> tariffZone;

    public TariffZonesInFrame_RelStructure(List<TariffZone> tariffZones) {
        getTariffZone().addAll(tariffZones);
    }

    public List<TariffZone> getTariffZone() {
        if (tariffZone == null) {
            tariffZone = new ArrayList<>();
        }
        return this.tariffZone;
    }

}

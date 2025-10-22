package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.PassengerEntrance;

public class PassengerEntranceMapper extends CustomMapper<PassengerEntrance, org.rutebanken.tiamat.model.vehicle.PassengerEntrance> {

    @Override
    public void mapAtoB(PassengerEntrance netexPassengerEntrance, org.rutebanken.tiamat.model.vehicle.PassengerEntrance tiamatPassengerEntrance, MappingContext context) {
        super.mapAtoB(netexPassengerEntrance, tiamatPassengerEntrance, context);
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.vehicle.PassengerEntrance tiamatPassengerEntrance, PassengerEntrance netexPassengerEntrance, MappingContext context) {
        super.mapBtoA(tiamatPassengerEntrance, netexPassengerEntrance, context);

        if (tiamatPassengerEntrance.getName() != null) {
            netexPassengerEntrance.getName().withContent(tiamatPassengerEntrance.getName().getValue());
        }

        if (tiamatPassengerEntrance.getDescription() != null) {
            netexPassengerEntrance.getDescription().withContent(tiamatPassengerEntrance.getDescription().getValue());
        }

        if (tiamatPassengerEntrance.getLabel() != null) {
            netexPassengerEntrance.getLabel().withContent(tiamatPassengerEntrance.getLabel().getValue());
        }
    }
}

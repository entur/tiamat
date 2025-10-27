package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.PassengerSpace;

public class PassengerSpaceMapper extends CustomMapper<PassengerSpace, org.rutebanken.tiamat.model.vehicle.PassengerSpace> {

    @Override
    public void mapAtoB(PassengerSpace netexPassengerSpace, org.rutebanken.tiamat.model.vehicle.PassengerSpace tiamatPassengerSpace, MappingContext context) {
        super.mapAtoB(netexPassengerSpace, tiamatPassengerSpace, context);
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.vehicle.PassengerSpace tiamatPassengerSpace, PassengerSpace netexPassengerSpace, MappingContext context) {
        super.mapBtoA(tiamatPassengerSpace, netexPassengerSpace, context);

        if (tiamatPassengerSpace.getName() != null) {
            netexPassengerSpace.getName().withContent(tiamatPassengerSpace.getName().getValue());
        }

        if (tiamatPassengerSpace.getDescription() != null) {
            netexPassengerSpace.getDescription().withContent(tiamatPassengerSpace.getDescription().getValue());
        }

        if (tiamatPassengerSpace.getLabel() != null) {
            netexPassengerSpace.getLabel().withContent(tiamatPassengerSpace.getLabel().getValue());
        }
    }
}

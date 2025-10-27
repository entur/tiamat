package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.PassengerSpot;
import org.rutebanken.netex.model.SpotRowRefStructure;

public class PassengerSpotMapper extends CustomMapper<PassengerSpot, org.rutebanken.tiamat.model.vehicle.PassengerSpot> {

    @Override
    public void mapAtoB(PassengerSpot netexPassengerSpot, org.rutebanken.tiamat.model.vehicle.PassengerSpot tiamatPassengerSpot, MappingContext context) {
        super.mapAtoB(netexPassengerSpot, tiamatPassengerSpot, context);
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.vehicle.PassengerSpot tiamatPassengerSpot, PassengerSpot netexPassengerSpot, MappingContext context) {
        super.mapBtoA(tiamatPassengerSpot, netexPassengerSpot, context);

        if (tiamatPassengerSpot.getName() != null) {
            netexPassengerSpot.getName().withContent(tiamatPassengerSpot.getName().getValue());
        }

        if (tiamatPassengerSpot.getDescription() != null) {
            netexPassengerSpot.getDescription().withContent(tiamatPassengerSpot.getDescription().getValue());
        }

        if (tiamatPassengerSpot.getLabel() != null) {
            netexPassengerSpot.getLabel().withContent(tiamatPassengerSpot.getLabel().getValue());
        }

        if (tiamatPassengerSpot.getSpotRowRef() != null) {
            netexPassengerSpot.withSpotRowRef(new SpotRowRefStructure().withRef(tiamatPassengerSpot.getSpotRowRef()));
        }
    }
}

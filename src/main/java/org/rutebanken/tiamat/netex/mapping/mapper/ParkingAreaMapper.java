package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.ParkingAreas_RelStructure;
import org.rutebanken.tiamat.model.ParkingArea;

public class ParkingAreaMapper extends CustomMapper<ParkingAreas_RelStructure, org.rutebanken.tiamat.model.ParkingArea> {

    @Override
    public void mapAtoB(ParkingAreas_RelStructure parkingAreas_relStructure, ParkingArea parkingArea, MappingContext context) {
        super.mapAtoB(parkingAreas_relStructure, parkingArea, context);
    }

    @Override
    public void mapBtoA(ParkingArea parkingArea, ParkingAreas_RelStructure parkingAreas_relStructure, MappingContext context) {
        super.mapBtoA(parkingArea, parkingAreas_relStructure, context);
    }
}

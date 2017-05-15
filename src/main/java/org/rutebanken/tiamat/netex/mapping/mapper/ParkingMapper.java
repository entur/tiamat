package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.ParkingArea;
import org.rutebanken.netex.model.ParkingAreas_RelStructure;

import java.util.List;

public class ParkingMapper extends CustomMapper<Parking, org.rutebanken.tiamat.model.Parking> {

    @Override
    public void mapAtoB(Parking parking, org.rutebanken.tiamat.model.Parking parking2, MappingContext context) {
        super.mapAtoB(parking, parking2, context);
        if (parking.getParkingAreas() != null &&
                parking.getParkingAreas().getParkingAreaRefOrParkingArea() != null &&
                !parking.getParkingAreas().getParkingAreaRefOrParkingArea().isEmpty()) {
            List<org.rutebanken.tiamat.model.ParkingArea> parkingAreas = mapperFacade.mapAsList(parking.getParkingAreas().getParkingAreaRefOrParkingArea(), org.rutebanken.tiamat.model.ParkingArea.class, context);
            if (!parkingAreas.isEmpty()) {
                parking2.setParkingAreas(parkingAreas);
            }
        }
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.Parking parking, Parking parking2, MappingContext context) {
        super.mapBtoA(parking, parking2, context);
        if (parking.getParkingAreas() != null &&
                !parking.getParkingAreas().isEmpty()) {

            List<ParkingArea> parkingAreas = mapperFacade.mapAsList(parking.getParkingAreas(), ParkingArea.class, context);
            if (!parkingAreas.isEmpty()) {
                ParkingAreas_RelStructure parkingAreas_relStructure = new ParkingAreas_RelStructure();
                parkingAreas_relStructure.getParkingAreaRefOrParkingArea().addAll(parkingAreas);

                parking2.setParkingAreas(parkingAreas_relStructure);
            }
        }
    }
}

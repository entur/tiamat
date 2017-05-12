package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.ParkingAreas_RelStructure;
import org.rutebanken.tiamat.model.ParkingArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ParkingAreaListConverter extends BidirectionalConverter<List<ParkingArea>, ParkingAreas_RelStructure> {

    private static final Logger logger = LoggerFactory.getLogger(ParkingAreaListConverter.class);

    @Override
    public ParkingAreas_RelStructure convertTo(List<ParkingArea> parkingAreas, Type<ParkingAreas_RelStructure> destinationType) {
        if(parkingAreas == null || parkingAreas.isEmpty()) {
            return null;
        }


        ParkingAreas_RelStructure parkingAreas_relStructure = new ParkingAreas_RelStructure();

        logger.debug("Mapping {} parkingAreas to netex", parkingAreas != null ? parkingAreas.size() : 0);

        parkingAreas.forEach(parkingArea -> {
            org.rutebanken.netex.model.ParkingArea netexParkingArea = mapperFacade.map(parkingArea, org.rutebanken.netex.model.ParkingArea.class);
            parkingAreas_relStructure.getParkingAreaRefOrParkingArea().add(netexParkingArea);
        });
        return parkingAreas_relStructure;
    }

    @Override
    public List<ParkingArea> convertFrom(ParkingAreas_RelStructure parkingAreas_relStructure, Type<List<ParkingArea>> destinationType) {
        logger.debug("Mapping {} quays to internal model", parkingAreas_relStructure != null ? parkingAreas_relStructure.getParkingAreaRefOrParkingArea().size() : 0);
        List<ParkingArea> parkingAreas = new ArrayList<>();
        if(parkingAreas_relStructure.getParkingAreaRefOrParkingArea() != null) {
            parkingAreas_relStructure.getParkingAreaRefOrParkingArea().stream()
                    .filter(object -> object instanceof org.rutebanken.netex.model.ParkingArea)
                    .map(object -> ((org.rutebanken.netex.model.ParkingArea) object))
                    .map(netexParkingArea -> {
                        ParkingArea tiamatQuay = mapperFacade.map(netexParkingArea, ParkingArea.class);
                        return tiamatQuay;
                    })
                    .forEach(parkingArea -> parkingAreas.add(parkingArea));
        }

        return parkingAreas;
    }
}

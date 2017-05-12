package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.ParkingProperties_RelStructure;
import org.rutebanken.tiamat.model.ParkingProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ParkingPropertiesListConverter extends BidirectionalConverter<List<ParkingProperties>, ParkingProperties_RelStructure> {

    private static final Logger logger = LoggerFactory.getLogger(ParkingPropertiesListConverter.class);

    @Override
    public ParkingProperties_RelStructure convertTo(List<ParkingProperties> parkingProperties, Type<ParkingProperties_RelStructure> destinationType) {
        if(parkingProperties == null || parkingProperties.isEmpty()) {
            return null;
        }


        ParkingProperties_RelStructure parkingAreas_relStructure = new ParkingProperties_RelStructure();

        logger.debug("Mapping {} parkingProperties to netex", parkingProperties != null ? parkingProperties.size() : 0);

        parkingProperties.forEach(parkingArea -> {
            org.rutebanken.netex.model.ParkingProperties netexParkingArea = mapperFacade.map(parkingArea, org.rutebanken.netex.model.ParkingProperties.class);
            parkingAreas_relStructure.getParkingProperties().add(netexParkingArea);
        });
        return parkingAreas_relStructure;
    }

    @Override
    public List<ParkingProperties> convertFrom(ParkingProperties_RelStructure parkingAreas_relStructure, Type<List<ParkingProperties>> destinationType) {
        logger.debug("Mapping {} quays to internal model", parkingAreas_relStructure != null ? parkingAreas_relStructure.getParkingProperties().size() : 0);
        List<ParkingProperties> parkingProperties = new ArrayList<>();
        if(parkingAreas_relStructure.getParkingProperties() != null) {
            parkingAreas_relStructure.getParkingProperties().stream()
                    .map(netexParkingProperty -> {
                        ParkingProperties tiamatProperty = mapperFacade.map(netexParkingProperty, ParkingProperties.class);
                        return tiamatProperty;
                    })
                    .forEach(parkingArea -> parkingProperties.add(parkingArea));
        }

        return parkingProperties;
    }
}

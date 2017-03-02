package org.rutebanken.tiamat.dtoassembling.assembler;

import org.rutebanken.tiamat.dtoassembling.dto.CountyOrMunicipalityDto;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceTypeEnumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CountyOrMunicipalityAssembler {
    private static final Logger logger = LoggerFactory.getLogger(CountyOrMunicipalityAssembler.class);
    private static final TopographicPlaceTypeEnumeration MUNICIPALITY_TYPE = TopographicPlaceTypeEnumeration.TOWN;

    public List<CountyOrMunicipalityDto> assemble(Iterable<TopographicPlace> places) {
        Map<String, TopographicPlace> counties = new HashMap<>();
        List<TopographicPlace> municipalities = new ArrayList<>();
        List<CountyOrMunicipalityDto> countyOrMunicipalityDtos = new ArrayList<>();

        places.forEach(place -> {
            if(place.getTopographicPlaceType() == null) {
                logger.warn("TopographicPlace has type null: {}", place.getNetexId());
            } else if (place.getTopographicPlaceType().equals(TopographicPlaceTypeEnumeration.COUNTY)) {
                counties.put(place.getNetexId(), place);
                countyOrMunicipalityDtos.add(map(place));
            } else if (place.getTopographicPlaceType().equals(MUNICIPALITY_TYPE)) {
                municipalities.add(place);
            } else {
                logger.warn("Cannot map topographic place wit type {}", place.getTopographicPlaceType().value());
            }
        });

        municipalities.forEach(municipality -> {
            if (municipality.getParentTopographicPlace() != null) {
                String countyId = municipality.getParentTopographicPlace().getNetexId();
                TopographicPlace county = counties.get(countyId);
                CountyOrMunicipalityDto admin = map(municipality);
                if (county != null) {
                    admin.county = county.getName().getValue();
                    countyOrMunicipalityDtos.add(admin);
                } else {
                    logger.warn("No county with ID {}", countyId);
                }
            } else {
                logger.warn("Found topographic place {} of type {} without parent place ref.", municipality.getNetexId(), MUNICIPALITY_TYPE);
            }
        });
        return countyOrMunicipalityDtos;
    }


    private CountyOrMunicipalityDto map(TopographicPlace place) {
        CountyOrMunicipalityDto admin = new CountyOrMunicipalityDto();
        admin.ref = String.valueOf(place.getNetexId());
        admin.name = place.getName().getValue();
        admin.type = place.getTopographicPlaceType().value();
        return admin;
    }


}

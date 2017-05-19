package org.rutebanken.tiamat.netex.mapping.converter;

import com.google.common.primitives.Longs;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.TopographicPlaceRefStructure;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMappingException;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

import static org.rutebanken.tiamat.model.VersionOfObjectRefStructure.ANY_VERSION;

@Component
public class TopographicPlaceRefConverter extends BidirectionalConverter<TopographicPlaceRefStructure, TopographicPlace> {

    private static final Logger logger = LoggerFactory.getLogger(TopographicPlaceRefConverter.class);

    // TODO: a mapper or converter should ideally not use repositories
    private final TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    public TopographicPlaceRefConverter(TopographicPlaceRepository topographicPlaceRepository) {
        this.topographicPlaceRepository = topographicPlaceRepository;
    }

    @Override
    public TopographicPlace convertTo(TopographicPlaceRefStructure topographicPlaceRefStructure, Type<TopographicPlace> type) {

        if(ANY_VERSION.equals(topographicPlaceRefStructure.getVersion())) {
            logger.debug("Version is any for topographic place ref. Finding newest version. ref: {}", topographicPlaceRefStructure);
            TopographicPlace topographicPlace = topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(topographicPlaceRefStructure.getRef());
            if(topographicPlace != null) {
                return topographicPlace;
            }
            throw new NetexMappingException("Cannot find topographic place from ref: " +topographicPlaceRefStructure.getRef());
        }

        Long version = Longs.tryParse(topographicPlaceRefStructure.getVersion());
        if(version != null) {
            logger.debug("Looking for topopgraphic place with ID {} and version {}", topographicPlaceRefStructure.getRef(), version);
            TopographicPlace topographicPlace = topographicPlaceRepository.findFirstByNetexIdAndVersion(topographicPlaceRefStructure.getRef(), version);
            if(topographicPlace != null) {
                return topographicPlace;
            }
            throw new NetexMappingException("Cannot find topographic place from ref " + topographicPlaceRefStructure.getRef() + " and version " + version);
        }
        throw new NetexMappingException("Version is not number or string 'any': " + topographicPlaceRefStructure.getVersion());
    }

    @Override
    public TopographicPlaceRefStructure convertFrom(TopographicPlace topographicPlace, Type<TopographicPlaceRefStructure> type) {
        TopographicPlaceRefStructure topographicPlaceRefStructure = new TopographicPlaceRefStructure()
                .withCreated(OffsetDateTime.now())
                .withRef(topographicPlace.getNetexId())
                .withVersion(String.valueOf(topographicPlace.getVersion()));

        logger.debug("Mapped topographic place ref structure: {}", topographicPlaceRefStructure);

        return topographicPlaceRefStructure;
    }
}

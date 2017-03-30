package org.rutebanken.tiamat.importer;

import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Transactional
@Component
public class TopographicPlaceImporter {

    private static final Logger logger = LoggerFactory.getLogger(TopographicPlaceImporter.class);

    private final NetexMapper netexMapper;

    private final TopographicPlaceRepository topographicPlaceRepository;

    @Autowired
    public TopographicPlaceImporter(NetexMapper netexMapper, TopographicPlaceRepository topographicPlaceRepository) {
        this.netexMapper = netexMapper;
        this.topographicPlaceRepository = topographicPlaceRepository;
    }

    public List<TopographicPlace> importTopographicPlaces(List<org.rutebanken.tiamat.model.TopographicPlace> topographicPlaces) {

        logger.info("Importing {} incoming topogprahic places", topographicPlaces.size());


        return topographicPlaces
                .stream()
                .peek(topographicPlace -> logger.info("{}", topographicPlace))
                .map(topographicPlace -> topographicPlaceRepository.save(topographicPlace))
                .map(topographicPlace -> netexMapper.mapToNetexModel(topographicPlace))
                .collect(toList());



    }

}

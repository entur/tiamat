package org.rutebanken.tiamat.importer;

import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceRefStructure;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.rutebanken.tiamat.versioning.TopographicPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;
import static org.rutebanken.tiamat.model.VersionOfObjectRefStructure.ANY_VERSION;

@Transactional
@Component
public class TopographicPlaceImporter {

    private static final Logger logger = LoggerFactory.getLogger(TopographicPlaceImporter.class);

    private final NetexMapper netexMapper;

    private final TopographicPlaceRepository topographicPlaceRepository;

    private final TopographicPlaceVersionedSaverService topographicPlaceVersionedSaverService;

    @Autowired
    public TopographicPlaceImporter(NetexMapper netexMapper, TopographicPlaceRepository topographicPlaceRepository, TopographicPlaceVersionedSaverService topographicPlaceVersionedSaverService) {
        this.netexMapper = netexMapper;
        this.topographicPlaceRepository = topographicPlaceRepository;
        this.topographicPlaceVersionedSaverService = topographicPlaceVersionedSaverService;
    }

    public List<org.rutebanken.netex.model.TopographicPlace> importTopographicPlaces(List<TopographicPlace> topographicPlaces, AtomicInteger topographicPlacesCounter) {

        logger.info("Importing {} incoming topogprahic places", topographicPlaces.size());

        List<TopographicPlace> parentTopographicPlaces = new ArrayList<>();

        logger.info("Importing topographic places without parent topographic place");
        for (TopographicPlace incomingTopographicPlace : topographicPlaces) {
            if(incomingTopographicPlace.getParentTopographicPlaceRef() == null) {
                parentTopographicPlaces.add(importTopographicPlace(incomingTopographicPlace, topographicPlacesCounter));
            }
        }

        List<TopographicPlace> withParentTopographicPlace = new ArrayList<>();

        logger.info("Importing topographic places with parent topographic place");
        for (TopographicPlace incomingTopographicPlace : topographicPlaces) {
            if(incomingTopographicPlace.getParentTopographicPlaceRef() != null) {

                boolean parentExist = false;
                for(TopographicPlace parentTopographicPlace : parentTopographicPlaces) {
                    if(parentTopographicPlace.getNetexId().equals(incomingTopographicPlace.getParentTopographicPlaceRef().getRef())) {
                        parentExist = true;
                        break;
                    }
                }

                if(!parentExist) {
                    throw new IllegalArgumentException("Invalid references to topographic place: " + incomingTopographicPlace.getParentTopographicPlaceRef());
                }

                withParentTopographicPlace.add(importTopographicPlace(incomingTopographicPlace, topographicPlacesCounter));
            }
        }

        List<TopographicPlace> result = new ArrayList<>(parentTopographicPlaces);
        result.addAll(withParentTopographicPlace);
        return result.stream().map(topographicPlace -> netexMapper.mapToNetexModel(topographicPlace)).collect(toList());

    }

    private TopographicPlace importTopographicPlace(TopographicPlace incomingTopographicPlace, AtomicInteger topographicPlacesCounter) {
        logger.debug("{}", incomingTopographicPlace);
        if (incomingTopographicPlace.getParentTopographicPlaceRef() != null) {
            // Rewrite the versioned reference to any.
            logger.debug("Resolving reference: {}", incomingTopographicPlace.getParentTopographicPlaceRef());
            TopographicPlace parent = topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(incomingTopographicPlace.getParentTopographicPlaceRef().getRef());
            TopographicPlaceRefStructure topographicPlaceRefStructure = new TopographicPlaceRefStructure(parent.getNetexId(), String.valueOf(parent.getVersion()));
            logger.debug("Found parent topographic place and created reference: {}", topographicPlaceRefStructure);
            incomingTopographicPlace.setParentTopographicPlaceRef(topographicPlaceRefStructure);

        }
        incomingTopographicPlace = topographicPlaceVersionedSaverService.saveNewVersion(incomingTopographicPlace);
        topographicPlacesCounter.incrementAndGet();
        return incomingTopographicPlace;
    }

}

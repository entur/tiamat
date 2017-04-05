package org.rutebanken.tiamat.importer;

import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TopographicPlaceRefStructure;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
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

    @Autowired
    public TopographicPlaceImporter(NetexMapper netexMapper, TopographicPlaceRepository topographicPlaceRepository) {
        this.netexMapper = netexMapper;
        this.topographicPlaceRepository = topographicPlaceRepository;
    }

    public List<org.rutebanken.netex.model.TopographicPlace> importTopographicPlaces(List<TopographicPlace> topographicPlaces, AtomicInteger topographicPlacesCounter) {

        logger.info("Importing {} incoming topogprahic places", topographicPlaces.size());

        logger.info("Checking invalid references");
        checkInvalidReferences(topographicPlaces);

        logger.info("Importing topographic places");
        return topographicPlaces
                .stream()
                .peek(topographicPlace -> logger.trace("{}", topographicPlace))
                .map(topographicPlace -> topographicPlaceRepository.save(topographicPlace))
                .peek(p -> topographicPlacesCounter.incrementAndGet())
                .map(topographicPlace -> netexMapper.mapToNetexModel(topographicPlace))
                .collect(toList());


    }

    private void checkInvalidReferences(List<TopographicPlace> topographicPlaces) {
        List<TopographicPlaceRefStructure> invalidrefs = topographicPlaces.stream()
                .filter(topographicPlace -> topographicPlace.getParentTopographicPlaceRef() != null)
                .map(topographicPlace -> topographicPlace.getParentTopographicPlaceRef())
                .filter(parentTopographicPlaceRef ->
                        topographicPlaces.stream()
                                .allMatch(other -> {
                                    return other.getNetexId().equals(parentTopographicPlaceRef)
                                            && (ANY_VERSION.equals(parentTopographicPlaceRef.getVersion())
                                            || parentTopographicPlaceRef.getVersion() == null
                                            || parentTopographicPlaceRef.getVersion().equals(other.getVersion()));
                                }))
                .collect(toList());

        if (!invalidrefs.isEmpty()) {
            throw new IllegalArgumentException("Invalid references to topographic place: " + invalidrefs);
        }
    }

}

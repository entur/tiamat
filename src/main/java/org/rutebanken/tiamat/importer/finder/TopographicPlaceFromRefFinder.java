package org.rutebanken.tiamat.importer.finder;

import org.rutebanken.tiamat.model.TopographicPlaceRefStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.rutebanken.tiamat.model.TopographicPlace;

import java.util.List;
import java.util.Optional;

@Component
public class TopographicPlaceFromRefFinder {

    private static final Logger logger = LoggerFactory.getLogger(TopographicPlaceFromRefFinder.class);

    public Optional<TopographicPlace> findTopographicPlaceFromRef(List<TopographicPlace> incomingTopographicPlaces, TopographicPlaceRefStructure topographicPlaceRef) {
        return incomingTopographicPlaces
                .stream()
                .filter(topographicPlace -> topographicPlace.getNetexId() != null)
                .filter(topographicPlace -> {
                    return topographicPlace.getNetexId().equals(topographicPlaceRef.getRef());
                })
                .peek(topographicPlace -> logger.trace("Looking at topographical place with name {} and id {}", topographicPlace.getName(), topographicPlace.getNetexId()))
                .findFirst();
    }

}

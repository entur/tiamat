package no.rutebanken.tiamat.rest.ifopt.siteframe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.TopographicPlace;
import uk.org.netex.netex.TopographicPlaceRefStructure;

import java.util.List;
import java.util.Optional;

@Component
public class TopographicPlaceFromRefFinder {

    private static final Logger logger = LoggerFactory.getLogger(TopographicPlaceFromRefFinder.class);

    public Optional<TopographicPlace> findTopographicPlaceFromRef(List<TopographicPlace> incomingTopographicPlaces, TopographicPlaceRefStructure topographicPlaceRef) {
        return incomingTopographicPlaces
                .stream()
                .filter(topographicPlace -> topographicPlace.getId() != null)
                .filter(topographicPlace -> topographicPlace.getId().equals(topographicPlaceRef.getRef()))
                .peek(topographicPlace -> logger.trace("Looking at topographical place with name {} and id {}", topographicPlace.getName(), topographicPlace.getId()))
                .findFirst();
    }

}

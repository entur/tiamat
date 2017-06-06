package org.rutebanken.tiamat.importer.filter;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class StopPlaceTypeFilter {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceTypeFilter.class);

    public List<StopPlace> filter(List<StopPlace> stopPlaceList, Set<StopTypeEnumeration> allowedTypes) {
        return filter(stopPlaceList, allowedTypes, false);
    }

    public List<StopPlace> filter(List<StopPlace> stopPlaceList, Set<StopTypeEnumeration> allowedTypes, boolean negate) {

        if (allowedTypes == null || allowedTypes.isEmpty()) {
            return stopPlaceList;
        }

        List<StopPlace> filteredStopPlaces = stopPlaceList.stream()
                .filter(stopPlace -> negate ? !allowedTypes.contains(stopPlace.getStopPlaceType()) : allowedTypes.contains(stopPlace.getStopPlaceType()))
                .collect(Collectors.toList());

        if (filteredStopPlaces.size() < stopPlaceList.size()) {
            logger.info("Filtered {} stop place. {}/{} {} negate: {}", stopPlaceList.size() - filteredStopPlaces.size(),
                    filteredStopPlaces.size(), stopPlaceList.size(), allowedTypes, negate);
        }
        return filteredStopPlaces;
    }

}

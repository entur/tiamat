package org.rutebanken.tiamat.importer.filter;

import org.apache.commons.collections.ArrayStack;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class StopPlaceTypeFilter {


    public List<StopPlace> filter(List<StopPlace> stopPlaceList, Set<StopTypeEnumeration> allowedTypes) {
        return filter(stopPlaceList, allowedTypes, false);
    }

    public List<StopPlace> filter(List<StopPlace> stopPlaceList, Set<StopTypeEnumeration> allowedTypes, boolean negate) {

        if(allowedTypes == null || allowedTypes.isEmpty()) {
            return stopPlaceList;
        }

        return stopPlaceList.stream()
                .filter(stopPlace -> negate ? ! allowedTypes.contains(stopPlace.getStopPlaceType()) : allowedTypes.contains(stopPlace.getStopPlaceType()))
                .collect(Collectors.toList());
    }

}

package org.rutebanken.tiamat.importer;

import com.google.common.collect.Sets;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Define alternative types.
 * Sometimes, onstreet bus and bus station should be treated as the same.
 */
@Component
public class AlternativeStopTypes {
    private final Map<StopTypeEnumeration, Set<StopTypeEnumeration>> alternativeTypesMap;


    public AlternativeStopTypes() {
        alternativeTypesMap = new HashMap<>();
        alternativeTypesMap.put(StopTypeEnumeration.BUS_STATION, Sets.newHashSet(StopTypeEnumeration.ONSTREET_BUS));
        alternativeTypesMap.put(StopTypeEnumeration.ONSTREET_BUS, Sets.newHashSet(StopTypeEnumeration.BUS_STATION));
    }

    public Set<StopTypeEnumeration> getAlternativeTypes(StopTypeEnumeration typeEnumeration) {
        return alternativeTypesMap.get(typeEnumeration);
    }

    public boolean matchesAlternativeType(StopTypeEnumeration source, StopTypeEnumeration candidate) {

        Set<StopTypeEnumeration> alternativeTypes = getAlternativeTypes(source);

        if(alternativeTypes == null) {
            return false;
        }

        return alternativeTypes.contains(candidate);

    }

}

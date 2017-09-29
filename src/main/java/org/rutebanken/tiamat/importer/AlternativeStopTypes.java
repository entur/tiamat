/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

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

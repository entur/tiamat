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

package org.rutebanken.tiamat.importer.filter;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;


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
                .toList();

        if (filteredStopPlaces.size() < stopPlaceList.size()) {
            logger.info("Filtered {} stop place. {}/{} {} negate: {}", stopPlaceList.size() - filteredStopPlaces.size(),
                    filteredStopPlaces.size(), stopPlaceList.size(), allowedTypes, negate);
        }
        return filteredStopPlaces;
    }

}

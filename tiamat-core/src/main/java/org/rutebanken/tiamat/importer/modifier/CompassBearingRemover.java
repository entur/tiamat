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

package org.rutebanken.tiamat.importer.modifier;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Remove compass bearing for specific types
 */
@Component
public class CompassBearingRemover {

    private static final Logger logger = LoggerFactory.getLogger(CompassBearingRemover.class);

    private final Set<StopTypeEnumeration> stopTypes = new HashSet<>();

    public CompassBearingRemover(@Value("${CompassBearingRemover.types:airport,railStation,liftStation,ferryStop,ferryPort,harbourPort}")
                                         String[] removeBearingForTypes) {
        for (String type : removeBearingForTypes) {
            StopTypeEnumeration stopTypeEnumeration = StopTypeEnumeration.fromValue(type);
            stopTypes.add(stopTypeEnumeration);
        }
    }

    public StopPlace remove(StopPlace stopPlace) {

        if (stopPlace == null) {
            return stopPlace;
        }
        if (stopPlace.getStopPlaceType() == null) {
            return stopPlace;
        }

        if (stopTypes.contains(stopPlace.getStopPlaceType())) {
            if (stopPlace.getQuays() == null) {
                return stopPlace;
            }

            if (stopPlace.getQuays().isEmpty()) {
                return stopPlace;
            }

            stopPlace.getQuays().forEach(quay -> {
                quay.setCompassBearing(null);
                logger.debug("Removing compass bearing for quay as parent stop place type is {}. {}", stopPlace.getStopPlaceType(), quay);
            });
        }
        return stopPlace;
    }
}

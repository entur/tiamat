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

import org.rutebanken.tiamat.geo.StopPlaceCentroidComputer;
import org.rutebanken.tiamat.importer.PublicationDeliveryImporter;
import org.rutebanken.tiamat.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Change stop places and quays before stop place import.
 */
@Service
public class StopPlacePreSteps {

    private static final Logger logger = LoggerFactory.getLogger(StopPlacePreSteps.class);

    private final StopPlaceCentroidComputer stopPlaceCentroidComputer;
    private final StopPlaceSplitter stopPlaceSplitter;

    @Autowired
    public StopPlacePreSteps(StopPlaceCentroidComputer stopPlaceCentroidComputer,
                             StopPlaceSplitter stopPlaceSplitter) {

        this.stopPlaceCentroidComputer = stopPlaceCentroidComputer;
        this.stopPlaceSplitter = stopPlaceSplitter;
    }

    public List<StopPlace> run(List<StopPlace> stops) {
        final String logCorrelationId = MDC.get(PublicationDeliveryImporter.IMPORT_CORRELATION_ID);
        stops = stopPlaceSplitter.split(stops);
        stops.parallelStream()
                .peek(stopPlace -> MDC.put(PublicationDeliveryImporter.IMPORT_CORRELATION_ID, logCorrelationId))
                .map(stopPlace -> {
                    stopPlaceCentroidComputer.computeCentroidForStopPlace(stopPlace);
                    return stopPlace;
                })
                .collect(toList());
        return stops;
    }
}

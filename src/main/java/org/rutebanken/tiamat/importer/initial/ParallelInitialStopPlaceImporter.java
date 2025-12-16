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

package org.rutebanken.tiamat.importer.initial;

import org.rutebanken.tiamat.importer.StopPlaceTopographicPlaceReferenceUpdater;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.versioning.save.StopPlaceVersionedSaverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Stream.concat;

@Component
@Transactional
public class ParallelInitialStopPlaceImporter {

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private StopPlaceTopographicPlaceReferenceUpdater stopPlaceTopographicPlaceReferenceUpdater;

    @Autowired
    private NetexMapper netexMapper;

    @Value("${changelog.publish.enabled:false}")
    private boolean publishChangelog;

    @Autowired
    private StopPlaceParentCreator parentStopPlaceCreator;

    public List<org.rutebanken.netex.model.StopPlace> importStopPlaces(List<StopPlace> tiamatStops, AtomicInteger stopPlacesCreated) {
        if (publishChangelog) {
            throw new IllegalStateException("Initial import not allowed with changelog publishing enabled! Set changelog.publish.enabled=false");
        }

        StopPlaceParentChildProcessor processor = new StopPlaceParentChildProcessor(
                stopPlaceVersionedSaverService,
                parentStopPlaceCreator
        );

        List<StopPlace> stops = tiamatStops.parallelStream()
                .map(stopPlace -> {
                    if (stopPlace.getTariffZones() != null) {
                        stopPlace.getTariffZones().forEach(tariffZoneRef -> tariffZoneRef.setVersion(null));
                    }
                    return stopPlace;
                })
                .peek(stopPlace -> stopPlaceTopographicPlaceReferenceUpdater.updateTopographicReference(stopPlace))
                .flatMap(stopPlace -> processor.processStopPlace(stopPlace, stopPlacesCreated))
                .toList();

        List<StopPlace> parents = processor.createAndSaveParentStopPlaces(stopPlacesCreated);

        return concat(stops.stream(), parents.stream())
                .map(netexMapper::mapToNetexModel)
                .toList();
    }

}

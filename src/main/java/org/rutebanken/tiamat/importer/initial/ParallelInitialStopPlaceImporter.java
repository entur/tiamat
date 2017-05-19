package org.rutebanken.tiamat.importer.initial;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;

@Component
@Transactional
public class ParallelInitialStopPlaceImporter {

    private static final Logger logger = LoggerFactory.getLogger(ParallelInitialStopPlaceImporter.class);

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private NetexMapper netexMapper;

    @Value("${changelog.publish.enabled:false}")
    private boolean publishChangelog;

    public List<org.rutebanken.netex.model.StopPlace> importStopPlaces(List<StopPlace> tiamatStops, AtomicInteger stopPlacesCreated) {

        if (publishChangelog){
            throw new IllegalStateException("Initial import not allowed with changelog publishing enabled! Set changelog.publish.enabled=false");
        }

        return tiamatStops.parallelStream()
                .map(stopPlace -> {

                    if(stopPlace.getTariffZones() != null) {
                        stopPlace.getTariffZones().forEach(tariffZoneRef -> tariffZoneRef.setVersion(null));
                    }

                    return stopPlace;
                })
                .map(stopPlace -> stopPlaceVersionedSaverService.saveNewVersion(stopPlace))
                .peek(stopPlace -> stopPlacesCreated.incrementAndGet())
                .map(stopPlace -> netexMapper.mapToNetexModel(stopPlace))
                .collect(toList());
    }

}

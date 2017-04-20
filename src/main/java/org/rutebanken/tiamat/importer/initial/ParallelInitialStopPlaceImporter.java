package org.rutebanken.tiamat.importer.initial;

import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;

@Component
@Transactional
public class ParallelInitialStopPlaceImporter {

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private NetexMapper netexMapper;

    public List<org.rutebanken.netex.model.StopPlace> importStopPlaces(List<StopPlace> tiamatStops, AtomicInteger stopPlacesCreated) {

        return tiamatStops.parallelStream()
                .map(stopPlace -> stopPlaceVersionedSaverService.saveNewVersion(stopPlace))
                .peek(stopPlace -> stopPlacesCreated.incrementAndGet())
                .map(stopPlace -> netexMapper.mapToNetexModel(stopPlace))
                .collect(toList());
    }

}

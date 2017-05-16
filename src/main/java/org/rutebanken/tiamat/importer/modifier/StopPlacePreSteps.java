package org.rutebanken.tiamat.importer.modifier;

import org.rutebanken.tiamat.geo.CentroidComputer;
import org.rutebanken.tiamat.importer.PublicationDeliveryImporter;
import org.rutebanken.tiamat.importer.modifier.name.*;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.service.TopographicPlaceLookupService;
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

    private final CentroidComputer centroidComputer;
    private final StopPlaceSplitter stopPlaceSplitter;

    @Autowired
    public StopPlacePreSteps(CentroidComputer centroidComputer,
                             StopPlaceSplitter stopPlaceSplitter) {

        this.centroidComputer = centroidComputer;
        this.stopPlaceSplitter = stopPlaceSplitter;
    }

    public List<StopPlace> run(List<StopPlace> stops) {
        final String logCorrelationId = MDC.get(PublicationDeliveryImporter.IMPORT_CORRELATION_ID);
        stops = stopPlaceSplitter.split(stops);
        stops.parallelStream()
                .peek(stopPlace -> MDC.put(PublicationDeliveryImporter.IMPORT_CORRELATION_ID, logCorrelationId))
                .map(stopPlace -> {
                    centroidComputer.computeCentroidForStopPlace(stopPlace);
                    return stopPlace;
                })
                .collect(toList());
        return stops;
    }
}

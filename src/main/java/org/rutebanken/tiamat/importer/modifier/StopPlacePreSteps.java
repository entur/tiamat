package org.rutebanken.tiamat.importer.modifier;

import org.rutebanken.tiamat.importer.PublicationDeliveryImporter;
import org.rutebanken.tiamat.importer.modifier.name.*;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.pelias.CountyAndMunicipalityLookupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;

/**
 * Change stop places and quays before stop place import.
 */
@Service
public class StopPlacePreSteps {

    private static final Logger logger = LoggerFactory.getLogger(StopPlacePreSteps.class);

    private final StopPlaceNameCleaner stopPlaceNameCleaner;
    private final NameToDescriptionMover nameToDescriptionMover;
    private final QuayNameRemover quayNameRemover;
    private final StopPlaceNameNumberToQuayMover stopPlaceNameNumberToQuayMover;
    private final QuayDescriptionPlatformCodeExtractor quayDescriptionPlatformCodeExtractor;
    private final CompassBearingRemover compassBearingRemover;


    @Autowired
    public StopPlacePreSteps(StopPlaceNameCleaner stopPlaceNameCleaner,
                             NameToDescriptionMover nameToDescriptionMover,
                             QuayNameRemover quayNameRemover,
                             StopPlaceNameNumberToQuayMover stopPlaceNameNumberToQuayMover,
                             QuayDescriptionPlatformCodeExtractor quayDescriptionPlatformCodeExtractor,
                             CompassBearingRemover compassBearingRemover) {
        this.stopPlaceNameCleaner = stopPlaceNameCleaner;
        this.nameToDescriptionMover = nameToDescriptionMover;
        this.quayNameRemover = quayNameRemover;
        this.stopPlaceNameNumberToQuayMover = stopPlaceNameNumberToQuayMover;
        this.quayDescriptionPlatformCodeExtractor = quayDescriptionPlatformCodeExtractor;
        this.compassBearingRemover = compassBearingRemover;
    }

    public List<StopPlace> run(List<StopPlace> stops, AtomicInteger topographicPlacesCounter) {
        final String logCorrelationId = MDC.get(PublicationDeliveryImporter.IMPORT_CORRELATION_ID);
        stops.parallelStream()
                .peek(stopPlace -> MDC.put(PublicationDeliveryImporter.IMPORT_CORRELATION_ID, logCorrelationId))
                .map(stopPlace -> compassBearingRemover.remove(stopPlace))
                .map(stopPlace -> stopPlaceNameCleaner.cleanNames(stopPlace))
                .map(stopPlace -> nameToDescriptionMover.updateDescriptionFromName(stopPlace))
                .map(stopPlace -> quayNameRemover.removeQuayNameIfEqualToStopPlaceName(stopPlace))
                .map(stopPlace -> stopPlaceNameNumberToQuayMover.moveNumberEndingToQuay(stopPlace))
                .map(stopPlace -> quayDescriptionPlatformCodeExtractor.extractPlatformCodes(stopPlace))
                .collect(toList());
        return stops;
    }
}

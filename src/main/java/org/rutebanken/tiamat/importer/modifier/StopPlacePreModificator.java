package org.rutebanken.tiamat.importer.modifier;

import org.rutebanken.tiamat.importer.PublicationDeliveryImporter;
import org.rutebanken.tiamat.importer.modifier.name.*;
import org.rutebanken.tiamat.model.SiteFrame;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
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
public class StopPlacePreModificator {

    private static final Logger logger = LoggerFactory.getLogger(StopPlacePreModificator.class);

    private final StopPlaceNameCleaner stopPlaceNameCleaner;
    private final NameToDescriptionMover nameToDescriptionMover;
    private final QuayNameRemover quayNameRemover;
    private final StopPlaceNameNumberToQuayMover stopPlaceNameNumberToQuayMover;
    private final QuayDescriptionPlatformCodeExtractor quayDescriptionPlatformCodeExtractor;
    private final CompassBearingRemover compassBearingRemover;
    private final CountyAndMunicipalityLookupService countyAndMunicipalityLookupService;


    @Autowired
    public StopPlacePreModificator(StopPlaceNameCleaner stopPlaceNameCleaner,
                                   NameToDescriptionMover nameToDescriptionMover,
                                   QuayNameRemover quayNameRemover,
                                   StopPlaceNameNumberToQuayMover stopPlaceNameNumberToQuayMover,
                                   QuayDescriptionPlatformCodeExtractor quayDescriptionPlatformCodeExtractor,
                                   CompassBearingRemover compassBearingRemover, CountyAndMunicipalityLookupService countyAndMunicipalityLookupService) {
        this.stopPlaceNameCleaner = stopPlaceNameCleaner;
        this.nameToDescriptionMover = nameToDescriptionMover;
        this.quayNameRemover = quayNameRemover;
        this.stopPlaceNameNumberToQuayMover = stopPlaceNameNumberToQuayMover;
        this.quayDescriptionPlatformCodeExtractor = quayDescriptionPlatformCodeExtractor;
        this.compassBearingRemover = compassBearingRemover;
        this.countyAndMunicipalityLookupService = countyAndMunicipalityLookupService;
    }

    public SiteFrame modify(SiteFrame siteFrame, AtomicInteger topographicPlacesCounter) {
        List<StopPlace> stops = siteFrame.getStopPlaces().getStopPlace().parallelStream()
                .peek(stopPlace -> MDC.put(PublicationDeliveryImporter.IMPORT_CORRELATION_ID, siteFrame.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).toString()))
                .map(stopPlace -> compassBearingRemover.remove(stopPlace))
                .map(stopPlace -> stopPlaceNameCleaner.cleanNames(stopPlace))
                .map(stopPlace -> nameToDescriptionMover.updateDescriptionFromName(stopPlace))
                .map(stopPlace -> quayNameRemover.removeQuayNameIfEqualToStopPlaceName(stopPlace))
                .map(stopPlace -> stopPlaceNameNumberToQuayMover.moveNumberEndingToQuay(stopPlace))
                .map(stopPlace -> quayDescriptionPlatformCodeExtractor.extractPlatformCodes(stopPlace))
                .map(stopPlace -> {
                    try {
                        countyAndMunicipalityLookupService.populateCountyAndMunicipality(stopPlace, topographicPlacesCounter);
                    } catch (IOException |InterruptedException e) {
                        logger.warn("Error looking up county and municipality", e);
                    }
                    return stopPlace;
                }).collect(toList());
        siteFrame.getStopPlaces().getStopPlace().clear();
        siteFrame.getStopPlaces().getStopPlace().addAll(stops);
        return siteFrame;
    }
}

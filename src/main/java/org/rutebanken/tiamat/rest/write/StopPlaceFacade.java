package org.rutebanken.tiamat.rest.write;

import jakarta.annotation.PostConstruct;
import org.rutebanken.netex.model.LocaleStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.VersionFrameDefaultsStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.rest.write.dto.StopPlaceDto;
import org.rutebanken.tiamat.rest.write.dto.StopPlaceJobDto;
import org.rutebanken.tiamat.rest.write.dto.StopPlacesDto;
import org.springframework.stereotype.Service;

import static org.rutebanken.tiamat.netex.mapping.NetexMappingContextThreadLocal.updateMappingContext;

@Service
public class StopPlaceFacade {

    private final NetexMapper netexMapper;
    private final JobService jobService;
    private final StopPlaceAsyncProcessor asyncProcessor;
    private final StopPlaceDomainService stopPlaceDomainService;

    public StopPlaceFacade(
        NetexMapper netexMapper,
        JobService jobService,
        StopPlaceAsyncProcessor asyncProcessor,
        StopPlaceDomainService stopPlaceDomainService) {
        this.netexMapper = netexMapper;
        this.jobService = jobService;
        this.asyncProcessor = asyncProcessor;
        this.stopPlaceDomainService = stopPlaceDomainService;
    }

    @PostConstruct
    private void initializeMappingContext() {
        /*
         * The mapper needs a timezone, usually provided from the SiteFrame in the Netex
         * document. Since we are only dealing with StopPlaces here, we set a default
         * timezone (UTC) to avoid mapping errors. Any date times provided in the StopPlace
         * data should be ignored anyway.
         */
        updateMappingContext(
            new SiteFrame().withFrameDefaults(
                new VersionFrameDefaultsStructure().withDefaultLocale(
                    new LocaleStructure().withTimeZone("UTC")
                )
            )
        );
    }

    public StopPlaceDto getStopPlace(String netexId) {
        // TOOD: do not cast
        return (StopPlaceDto) netexMapper.mapToNetexModel(stopPlaceDomainService.getStopPlace(netexId));
    }

    public StopPlaceJobDto createStopPlaces(StopPlacesDto dto) {
        var job = jobService.createJob();
        try {
            var stopPlace = validateAndGetSingleStopPlace(dto);
            asyncProcessor.processCreateStopPlace(job.getId(), stopPlace);
            return StopPlaceJobDto.from(job);
        } catch (Exception e) {
            return StopPlaceJobDto.from(
                jobService.fail(job.getId(), e.getMessage())
            );
        }
    }

    public StopPlaceJobDto updateStopPlace(StopPlacesDto dto) {
        var job = jobService.createJob();
        try {
            var stopPlace = validateAndGetSingleStopPlace(dto);
            asyncProcessor.processUpdateStopPlace(job.getId(), stopPlace);
            return StopPlaceJobDto.from(job);
        } catch (Exception e) {
            return StopPlaceJobDto.from(
                jobService.fail(job.getId(), e.getMessage())
            );
        }
    }

    public StopPlaceJobDto deleteStopPlace(String id) {
        var job = jobService.createJob();
        try {
            asyncProcessor.processDeleteStopPlace(job.getId(), id);
            return StopPlaceJobDto.from(job);
        } catch (Exception e) {
            return StopPlaceJobDto.from(
                jobService.fail(job.getId(), e.getMessage())
            );
        }
    }

    private StopPlace validateAndGetSingleStopPlace(StopPlacesDto dto) {
        var stopPlaces = netexMapper.mapStopsToTiamatModel(dto.getStopPlaces());
        if (stopPlaces.size() != 1) {
            throw new IllegalArgumentException(
                "Only one stop place allowed per request"
            );
        }
        var stopPlace = stopPlaces.getFirst();
        if (
            stopPlace.isParentStopPlace() || !stopPlace.getChildren().isEmpty()
        ) {
            throw new IllegalArgumentException(
                "Only mono-modal stop place allowed"
            );
        }
        return stopPlace;
    }
}

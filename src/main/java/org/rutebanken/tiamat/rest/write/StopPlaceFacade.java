package org.rutebanken.tiamat.rest.write;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.StreamingOutput;
import org.rutebanken.netex.model.LocaleStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.VersionFrameDefaultsStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.NetexMappingContextThreadLocal;
import org.rutebanken.tiamat.rest.write.dto.StopPlaceJobDto;
import org.rutebanken.tiamat.rest.write.dto.StopPlacesDto;
import org.springframework.stereotype.Service;

@Service
public class StopPlaceFacade {

    private final NetexMapper netexMapper;
    private final JobService jobService;
    private final StopPlaceAsyncProcessor asyncProcessor;
    private final StopPlaceDomainService stopPlaceDomainService;
    private final StopPlaceXmlWriter stopPlaceXmlWriter;

    public StopPlaceFacade(
            NetexMapper netexMapper,
            JobService jobService,
            StopPlaceAsyncProcessor asyncProcessor,
            StopPlaceDomainService stopPlaceDomainService,
            StopPlaceXmlWriter stopPlaceXmlWriter) {
        this.netexMapper = netexMapper;
        this.jobService = jobService;
        this.asyncProcessor = asyncProcessor;
        this.stopPlaceDomainService = stopPlaceDomainService;
        this.stopPlaceXmlWriter = stopPlaceXmlWriter;
    }

    @Transactional
    public StreamingOutput getStopPlace(String netexId) {
        updateMappingContext();
        org.rutebanken.netex.model.StopPlace stopPlace = netexMapper.mapToNetexModel(stopPlaceDomainService.getStopPlace(netexId));
        return stopPlaceXmlWriter.write(stopPlace);
    }

    public StopPlaceJobDto createStopPlaces(StopPlacesDto dto) {
        updateMappingContext();
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
        updateMappingContext();
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

    private void updateMappingContext() {
        if (NetexMappingContextThreadLocal.get() == null) {
            NetexMappingContextThreadLocal.updateMappingContext(
                    new SiteFrame().withFrameDefaults(
                            new VersionFrameDefaultsStructure().withDefaultLocale(
                                    new LocaleStructure().withTimeZone("UTC")
                            )
                    )
            );
        }
    }
}

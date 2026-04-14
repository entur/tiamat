package org.rutebanken.tiamat.rest.write;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.core.StreamingOutput;
import org.rutebanken.netex.model.LocaleStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.VersionFrameDefaultsStructure;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.NetexMappingContextThreadLocal;
import org.rutebanken.tiamat.rest.write.async.StopPlaceAsyncProcessor;
import org.rutebanken.tiamat.rest.write.dto.StopPlaceJobDto;
import org.rutebanken.tiamat.rest.write.dto.StopPlacesDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.RejectedExecutionException;

@Service
public class StopPlaceWriteService {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceWriteService.class);

    private final NetexMapper netexMapper;
    private final JobService jobService;
    private final StopPlaceAsyncProcessor asyncProcessor;
    private final StopPlaceWriteDomainService stopPlaceWriteDomainService;
    private final StopPlaceXmlWriter stopPlaceXmlWriter;

    public StopPlaceWriteService(
            NetexMapper netexMapper,
            JobService jobService,
            StopPlaceAsyncProcessor asyncProcessor,
            StopPlaceWriteDomainService stopPlaceWriteDomainService,
            StopPlaceXmlWriter stopPlaceXmlWriter) {
        this.netexMapper = netexMapper;
        this.jobService = jobService;
        this.asyncProcessor = asyncProcessor;
        this.stopPlaceWriteDomainService = stopPlaceWriteDomainService;
        this.stopPlaceXmlWriter = stopPlaceXmlWriter;
    }

    @Transactional
    public StreamingOutput getStopPlace(String netexId) {
        updateMappingContext();
        var tiamatStopPlace = Optional.ofNullable(stopPlaceWriteDomainService.getStopPlace(netexId))
                .orElseThrow(() -> new NotFoundException("Stop place not found: " + netexId));
        org.rutebanken.netex.model.StopPlace netexStopPlace =
                netexMapper.mapToNetexModel(tiamatStopPlace);
        return stopPlaceXmlWriter.write(netexStopPlace);
    }

    public StopPlaceJobDto createStopPlaces(StopPlacesDto dto) {
        updateMappingContext();
        var job = jobService.createJob();
        try {
            asyncProcessor.processCreateStopPlace(job.getId(), dto);
            return StopPlaceJobDto.from(job);
        } catch (RejectedExecutionException e) {
            throw rejectJobIfQueueFull(job, e);
        } catch (Exception e) {
            return StopPlaceJobDto.from(
                    jobService.fail(job.getId(), e)
            );
        }
    }

    public StopPlaceJobDto updateStopPlace(StopPlacesDto dto) {
        updateMappingContext();
        var job = jobService.createJob();
        try {
            asyncProcessor.processUpdateStopPlace(job.getId(), dto);
            return StopPlaceJobDto.from(job);
        } catch (RejectedExecutionException e) {
            throw rejectJobIfQueueFull(job, e);
        } catch (Exception e) {
            return StopPlaceJobDto.from(
                    jobService.fail(job.getId(), e)
            );
        }
    }

    public StopPlaceJobDto deleteStopPlace(String id) {
        var job = jobService.createJob();
        try {
            asyncProcessor.processDeleteStopPlace(job.getId(), id);
            return StopPlaceJobDto.from(job);
        } catch (RejectedExecutionException e) {
            throw rejectJobIfQueueFull(job, e);
        } catch (Exception e) {
            return StopPlaceJobDto.from(
                    jobService.fail(job.getId(), e)
            );
        }
    }



    private void updateMappingContext() {
        // TODO: solve this in some other way
        // perhaps just ignore validbetween in this api?
        // or require timezone in the request?
        // or require that validbetween does not exist in request?
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

    private ServiceUnavailableException rejectJobIfQueueFull(AsyncStopPlaceJob job, Exception exception) {
        logger.warn("Write queue is full, rejecting new job.", exception);
        jobService.fail(job.getId(), exception);
        return new ServiceUnavailableException("Write queue is full, please retry later.");
    }
}

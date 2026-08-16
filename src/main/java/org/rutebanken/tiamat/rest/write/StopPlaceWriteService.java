package org.rutebanken.tiamat.rest.write;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.rest.write.async.StopPlaceAsyncProcessor;
import org.rutebanken.tiamat.rest.write.dto.StopPlaceJobDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
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
    private final int maxPayloadSize;

    public StopPlaceWriteService(
            NetexMapper netexMapper,
            JobService jobService,
            StopPlaceAsyncProcessor asyncProcessor,
            StopPlaceWriteDomainService stopPlaceWriteDomainService,
            StopPlaceXmlWriter stopPlaceXmlWriter,
            @Value("${tiamat.write-api.max-payload-size-bytes:10485760}") int maxPayloadSize) {
        this.netexMapper = netexMapper;
        this.jobService = jobService;
        this.asyncProcessor = asyncProcessor;
        this.stopPlaceWriteDomainService = stopPlaceWriteDomainService;
        this.stopPlaceXmlWriter = stopPlaceXmlWriter;
        this.maxPayloadSize = maxPayloadSize;
    }

    @Transactional
    public StreamingOutput getStopPlace(String netexId) {
        var tiamatStopPlace = Optional.ofNullable(stopPlaceWriteDomainService.getStopPlace(netexId))
                .orElseThrow(() -> new NotFoundException("Stop place not found: " + netexId));
        org.rutebanken.netex.model.StopPlace netexStopPlace =
                netexMapper.mapToNetexModel(tiamatStopPlace);
        return stopPlaceXmlWriter.write(netexStopPlace);
    }

    public StopPlaceJobDto createStopPlaces(InputStream body) {
        byte[] payload = readPayload(body);
        var job = jobService.createJob();
        try {
            asyncProcessor.processCreateStopPlace(job.getId(), payload);
            return StopPlaceJobDto.from(job);
        } catch (RejectedExecutionException e) {
            throw rejectJobIfQueueFull(job, e);
        } catch (Exception e) {
            return StopPlaceJobDto.from(
                    jobService.fail(job.getId(), e)
            );
        }
    }

    public StopPlaceJobDto updateStopPlace(InputStream body) {
        byte[] payload = readPayload(body);
        var job = jobService.createJob();
        try {
            asyncProcessor.processUpdateStopPlace(job.getId(), payload);
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

    /**
     * Reads at most one byte beyond the limit, so an oversized payload is rejected without
     * buffering all of it. No parsing happens here: the request thread should not do the
     * payload's CPU bound work before the job has even been accepted.
     */
    private byte[] readPayload(InputStream body) {
        try {
            byte[] payload = body.readNBytes(maxPayloadSize + 1);
            if (payload.length > maxPayloadSize) {
                throw new ClientErrorException(
                        "Payload exceeds the maximum supported size of " + maxPayloadSize + " bytes.",
                        Response.Status.REQUEST_ENTITY_TOO_LARGE);
            }
            return payload;
        } catch (IOException e) {
            throw new BadRequestException("Could not read request payload.");
        }
    }

    private ServiceUnavailableException rejectJobIfQueueFull(AsyncStopPlaceJob job, Exception exception) {
        logger.warn("Write queue is full, rejecting new job.", exception);
        jobService.fail(job.getId(), exception);
        return new ServiceUnavailableException("Write queue is full, please retry later.");
    }
}

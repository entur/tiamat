package org.rutebanken.tiamat.writer;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.core.Response;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.writer.async.WriteJobMessage;
import org.rutebanken.tiamat.writer.async.WriteJobPublisher;
import org.rutebanken.tiamat.writer.async.WriteJobRejectedException;
import org.rutebanken.tiamat.rest.write.dto.StopPlaceJobDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class AsyncStopPlaceWriter {

    private static final Logger logger = LoggerFactory.getLogger(AsyncStopPlaceWriter.class);

    private final JobService jobService;
    private final WriteJobPublisher writeJobPublisher;
    private final int maxPayloadSize;

    public AsyncStopPlaceWriter(
            JobService jobService,
            WriteJobPublisher writeJobPublisher,
            @Value("${tiamat.write-api.max-payload-size-bytes:10485760}") int maxPayloadSize) {
        this.jobService = jobService;
        this.writeJobPublisher = writeJobPublisher;
        this.maxPayloadSize = maxPayloadSize;
    }


    public StopPlaceJobDto createStopPlaces(InputStream body) {
        byte[] payload = readPayload(body);
        var job = jobService.createJob();
        try {
            writeJobPublisher.publish(WriteJobMessage.create(job.getId(), payload));
            return StopPlaceJobDto.from(job);
        } catch (WriteJobRejectedException e) {
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
            writeJobPublisher.publish(WriteJobMessage.update(job.getId(), payload));
            return StopPlaceJobDto.from(job);
        } catch (WriteJobRejectedException e) {
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
            writeJobPublisher.publish(WriteJobMessage.delete(job.getId(), id));
            return StopPlaceJobDto.from(job);
        } catch (WriteJobRejectedException e) {
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

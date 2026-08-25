package org.rutebanken.tiamat.writer;


import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ServiceUnavailableException;
import jakarta.ws.rs.core.StreamingOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.writer.async.WriteJobMessage;
import org.rutebanken.tiamat.writer.async.WriteJobPublisher;
import org.rutebanken.tiamat.writer.async.WriteJobRejectedException;
import org.rutebanken.tiamat.rest.write.dto.StopPlaceJobDto;
import org.rutebanken.tiamat.rest.write.dto.StopPlacesDto;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsyncStopPlaceWriterTest {

    @Mock
    private NetexMapper netexMapper;

    @Mock
    private JobService jobService;

    @Mock
    private WriteJobPublisher writeJobPublisher;

    @Mock
    private StopPlaceWriter stopPlaceWriter;

    private AsyncStopPlaceWriter facade;

    private static java.io.InputStream stream() {
        return new java.io.ByteArrayInputStream("<stopPlaces/>".getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    @BeforeEach
    void setup() {
        facade = new AsyncStopPlaceWriter(
                jobService,
                writeJobPublisher,
                stopPlaceWriter,
                10485760
        );
    }



    @Test
    void createStopPlaces_Success() {
        StopPlacesDto dto = createStopPlacesDto();
        AsyncStopPlaceJob job = createJob(
            1L,
            AsyncStopPlaceJobStatus.PROCESSING
        );

        when(jobService.createJob()).thenReturn(job);

        StopPlaceJobDto result = facade.createStopPlaces(stream());

        assertNotNull(result);
        assertEquals(1L, result.jobId());
        assertEquals(AsyncStopPlaceJobStatus.PROCESSING, result.status());
        verify(writeJobPublisher).publish(any(WriteJobMessage.class));
    }

    @Test
    void createStopPlaces_ProcessorThrowsException_ReturnsFailedJob() {
        StopPlacesDto dto = createStopPlacesDto();
        AsyncStopPlaceJob job = createJob(1L, AsyncStopPlaceJobStatus.FAILED);

        when(jobService.createJob()).thenReturn(job);
        doThrow(new RuntimeException("Create failed"))
            .when(writeJobPublisher)
            .publish(any(WriteJobMessage.class));
        when(jobService.fail(eq(1L), any(Exception.class))).thenReturn(job);

        StopPlaceJobDto result = facade.createStopPlaces(stream());

        assertEquals(AsyncStopPlaceJobStatus.FAILED, result.status());
        verify(jobService).fail(eq(1L), any(Exception.class));
    }

    @Test
    void createStopPlaces_QueueFull_ThrowsServiceUnavailableException() {
        StopPlacesDto dto = createStopPlacesDto();
        AsyncStopPlaceJob job = createJob(1L, AsyncStopPlaceJobStatus.FAILED);

        when(jobService.createJob()).thenReturn(job);
        doThrow(new WriteJobRejectedException("Queue full", null))
            .when(writeJobPublisher)
            .publish(any(WriteJobMessage.class));

        assertThrows(ServiceUnavailableException.class, () ->
            facade.createStopPlaces(stream())
        );
        verify(jobService).fail(eq(1L), any(WriteJobRejectedException.class));
    }

    @Test
    void updateStopPlace_Success() {
        var dto = createStopPlacesDto();
        AsyncStopPlaceJob job = createJob(
            2L,
            AsyncStopPlaceJobStatus.PROCESSING
        );

        when(jobService.createJob()).thenReturn(job);

        StopPlaceJobDto result = facade.updateStopPlace(stream());

        assertNotNull(result);
        assertEquals(2L, result.jobId());
        assertEquals(AsyncStopPlaceJobStatus.PROCESSING, result.status());
        verify(writeJobPublisher).publish(any(WriteJobMessage.class));
    }

    @Test
    void updateStopPlace_ProcessorThrowsException_ReturnsFailedJob() {
        var dto = createStopPlacesDto();
        AsyncStopPlaceJob job = createJob(2L, AsyncStopPlaceJobStatus.FAILED);

        when(jobService.createJob()).thenReturn(job);
        doThrow(new RuntimeException("Update failed"))
            .when(writeJobPublisher)
            .publish(any(WriteJobMessage.class));
        when(jobService.fail(eq(2L), any(Exception.class))).thenReturn(job);

        StopPlaceJobDto result = facade.updateStopPlace(stream());

        assertEquals(AsyncStopPlaceJobStatus.FAILED, result.status());
        verify(jobService).fail(eq(2L), any(Exception.class));
    }

    @Test
    void updateStopPlace_QueueFull_ThrowsServiceUnavailableException() {
        var dto = createStopPlacesDto();
        AsyncStopPlaceJob job = createJob(2L, AsyncStopPlaceJobStatus.FAILED);

        when(jobService.createJob()).thenReturn(job);
        doThrow(new WriteJobRejectedException("Queue full", null))
            .when(writeJobPublisher)
            .publish(any(WriteJobMessage.class));

        assertThrows(ServiceUnavailableException.class, () ->
            facade.updateStopPlace(stream())
        );
        verify(jobService).fail(eq(2L), any(WriteJobRejectedException.class));
    }

    @Test
    void deleteStopPlace_Success() {
        String stopPlaceId = "NSR:StopPlace:100";
        AsyncStopPlaceJob job = createJob(
            3L,
            AsyncStopPlaceJobStatus.PROCESSING
        );

        when(jobService.createJob()).thenReturn(job);

        StopPlaceJobDto result = facade.deleteStopPlace(stopPlaceId);

        assertNotNull(result);
        assertEquals(3L, result.jobId());
        assertEquals(AsyncStopPlaceJobStatus.PROCESSING, result.status());
        verify(writeJobPublisher).publish(any(WriteJobMessage.class));
    }

    @Test
    void deleteStopPlace_ProcessorThrowsException_ReturnsFailedJob() {
        String stopPlaceId = "NSR:StopPlace:100";
        AsyncStopPlaceJob job = createJob(3L, AsyncStopPlaceJobStatus.FAILED);

        when(jobService.createJob()).thenReturn(job);
        doThrow(new RuntimeException("Delete failed"))
            .when(writeJobPublisher)
            .publish(any(WriteJobMessage.class));
        when(jobService.fail(eq(3L), any(Exception.class))).thenReturn(job);

        StopPlaceJobDto result = facade.deleteStopPlace(stopPlaceId);

        assertEquals(AsyncStopPlaceJobStatus.FAILED, result.status());
        verify(jobService).fail(eq(3L), any(Exception.class));
    }

    @Test
    void deleteStopPlace_QueueFull_ThrowsServiceUnavailableException() {
        String stopPlaceId = "NSR:StopPlace:100";
        AsyncStopPlaceJob job = createJob(3L, AsyncStopPlaceJobStatus.FAILED);

        when(jobService.createJob()).thenReturn(job);
        doThrow(new WriteJobRejectedException("Queue full", null))
            .when(writeJobPublisher)
            .publish(any(WriteJobMessage.class));

        assertThrows(ServiceUnavailableException.class, () ->
            facade.deleteStopPlace(stopPlaceId)
        );
        verify(jobService).fail(eq(3L), any(WriteJobRejectedException.class));
    }

    private StopPlacesDto createStopPlacesDto() {
        StopPlacesDto dto = new StopPlacesDto();
        org.rutebanken.netex.model.StopPlace netexStopPlace =
            new org.rutebanken.netex.model.StopPlace()
                .withId("NSR:StopPlace:100")
                .withName(
                    new org.rutebanken.netex.model.MultilingualString().withValue(
                        "Test Stop"
                    )
                );
        dto.setStopPlaces(Collections.singletonList(netexStopPlace));
        return dto;
    }

    private AsyncStopPlaceJob createJob(
        Long id,
        AsyncStopPlaceJobStatus status
    ) {
        AsyncStopPlaceJob job = new AsyncStopPlaceJob();
        job.setId(id);
        job.setStatus(status);
        return job;
    }
}

package org.rutebanken.tiamat.rest.write;

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
import org.rutebanken.tiamat.rest.write.async.StopPlaceAsyncProcessor;
import org.rutebanken.tiamat.rest.write.dto.StopPlaceJobDto;
import org.rutebanken.tiamat.rest.write.dto.StopPlacesDto;

import java.util.Collections;
import java.util.concurrent.RejectedExecutionException;

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
class StopPlaceWriteServiceTest {

    @Mock
    private NetexMapper netexMapper;

    @Mock
    private JobService jobService;

    @Mock
    private StopPlaceAsyncProcessor asyncProcessor;

    @Mock
    private StopPlaceDomainService stopPlaceDomainService;

    @Mock
    private StopPlaceXmlWriter stopPlaceXmlWriter;

    private StopPlaceWriteService facade;

    @BeforeEach
    void setup() {
        facade = new StopPlaceWriteService(
            netexMapper,
            jobService,
            asyncProcessor,
            stopPlaceDomainService,
            stopPlaceXmlWriter
        );
    }

    @Test
    void getStopPlace_WhenFound_ReturnsStreamingOutput() {
        String netexId = "NSR:StopPlace:100";
        var tiamatStopPlace = new org.rutebanken.tiamat.model.StopPlace();
        var netexStopPlace = new org.rutebanken.netex.model.StopPlace();
        StreamingOutput expectedOutput = outputStream -> {};

        when(stopPlaceDomainService.getStopPlace(netexId)).thenReturn(
            tiamatStopPlace
        );
        when(netexMapper.mapToNetexModel(tiamatStopPlace)).thenReturn(
            netexStopPlace
        );
        when(stopPlaceXmlWriter.write(netexStopPlace)).thenReturn(
            expectedOutput
        );

        StreamingOutput result = facade.getStopPlace(netexId);

        assertEquals(expectedOutput, result);
        verify(stopPlaceDomainService).getStopPlace(netexId);
        verify(netexMapper).mapToNetexModel(tiamatStopPlace);
        verify(stopPlaceXmlWriter).write(netexStopPlace);
    }

    @Test
    void getStopPlace_WhenNotFound_ThrowsNotFoundException() {
        String netexId = "NSR:StopPlace:999";
        when(stopPlaceDomainService.getStopPlace(netexId)).thenReturn(null);

        assertThrows(NotFoundException.class, () ->
            facade.getStopPlace(netexId)
        );
        verify(netexMapper, never()).mapToNetexModel(
            any(org.rutebanken.tiamat.model.StopPlace.class)
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

        StopPlaceJobDto result = facade.createStopPlaces(dto);

        assertNotNull(result);
        assertEquals(1L, result.jobId());
        assertEquals(AsyncStopPlaceJobStatus.PROCESSING, result.status());
        verify(asyncProcessor).processCreateStopPlace(1L, dto);
    }

    @Test
    void createStopPlaces_ProcessorThrowsException_ReturnsFailedJob() {
        StopPlacesDto dto = createStopPlacesDto();
        AsyncStopPlaceJob job = createJob(1L, AsyncStopPlaceJobStatus.FAILED);

        when(jobService.createJob()).thenReturn(job);
        doThrow(new RuntimeException("Create failed"))
            .when(asyncProcessor)
            .processCreateStopPlace(anyLong(), any());
        when(jobService.fail(eq(1L), any(Exception.class))).thenReturn(job);

        StopPlaceJobDto result = facade.createStopPlaces(dto);

        assertEquals(AsyncStopPlaceJobStatus.FAILED, result.status());
        verify(jobService).fail(eq(1L), any(Exception.class));
    }

    @Test
    void createStopPlaces_QueueFull_ThrowsServiceUnavailableException() {
        StopPlacesDto dto = createStopPlacesDto();
        AsyncStopPlaceJob job = createJob(1L, AsyncStopPlaceJobStatus.FAILED);

        when(jobService.createJob()).thenReturn(job);
        doThrow(new RejectedExecutionException("Queue full"))
            .when(asyncProcessor)
            .processCreateStopPlace(anyLong(), any());

        assertThrows(ServiceUnavailableException.class, () ->
            facade.createStopPlaces(dto)
        );
        verify(jobService).fail(eq(1L), any(RejectedExecutionException.class));
    }

    @Test
    void updateStopPlace_Success() {
        StopPlacesDto dto = createStopPlacesDto();
        AsyncStopPlaceJob job = createJob(
            2L,
            AsyncStopPlaceJobStatus.PROCESSING
        );

        when(jobService.createJob()).thenReturn(job);

        StopPlaceJobDto result = facade.updateStopPlace(dto);

        assertNotNull(result);
        assertEquals(2L, result.jobId());
        assertEquals(AsyncStopPlaceJobStatus.PROCESSING, result.status());
        verify(asyncProcessor).processUpdateStopPlace(2L, dto);
    }

    @Test
    void updateStopPlace_ProcessorThrowsException_ReturnsFailedJob() {
        StopPlacesDto dto = createStopPlacesDto();
        AsyncStopPlaceJob job = createJob(2L, AsyncStopPlaceJobStatus.FAILED);

        when(jobService.createJob()).thenReturn(job);
        doThrow(new RuntimeException("Update failed"))
            .when(asyncProcessor)
            .processUpdateStopPlace(anyLong(), any());
        when(jobService.fail(eq(2L), any(Exception.class))).thenReturn(job);

        StopPlaceJobDto result = facade.updateStopPlace(dto);

        assertEquals(AsyncStopPlaceJobStatus.FAILED, result.status());
        verify(jobService).fail(eq(2L), any(Exception.class));
    }

    @Test
    void updateStopPlace_QueueFull_ThrowsServiceUnavailableException() {
        StopPlacesDto dto = createStopPlacesDto();
        AsyncStopPlaceJob job = createJob(2L, AsyncStopPlaceJobStatus.FAILED);

        when(jobService.createJob()).thenReturn(job);
        doThrow(new RejectedExecutionException("Queue full"))
            .when(asyncProcessor)
            .processUpdateStopPlace(anyLong(), any());

        assertThrows(ServiceUnavailableException.class, () ->
            facade.updateStopPlace(dto)
        );
        verify(jobService).fail(eq(2L), any(RejectedExecutionException.class));
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
        verify(asyncProcessor).processDeleteStopPlace(3L, stopPlaceId);
    }

    @Test
    void deleteStopPlace_ProcessorThrowsException_ReturnsFailedJob() {
        String stopPlaceId = "NSR:StopPlace:100";
        AsyncStopPlaceJob job = createJob(3L, AsyncStopPlaceJobStatus.FAILED);

        when(jobService.createJob()).thenReturn(job);
        doThrow(new RuntimeException("Delete failed"))
            .when(asyncProcessor)
            .processDeleteStopPlace(anyLong(), anyString());
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
        doThrow(new RejectedExecutionException("Queue full"))
            .when(asyncProcessor)
            .processDeleteStopPlace(anyLong(), anyString());

        assertThrows(ServiceUnavailableException.class, () ->
            facade.deleteStopPlace(stopPlaceId)
        );
        verify(jobService).fail(eq(3L), any(RejectedExecutionException.class));
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

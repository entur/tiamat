package org.rutebanken.tiamat.rest.write;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.rest.write.dto.StopPlaceJobDto;
import org.rutebanken.tiamat.rest.write.dto.StopPlacesDto;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StopPlaceFacadeTest {

    @Mock
    private NetexMapper netexMapper;

    @Mock
    private JobService jobService;

    @Mock
    private StopPlaceAsyncProcessor asyncProcessor;

    private StopPlaceFacade facade;

    private StopPlaceDomainService domainService;

    private StopPlaceXmlWriter xmlWriter;

    @BeforeEach
    void setup() {
        facade = new StopPlaceFacade(netexMapper, jobService, asyncProcessor, domainService, xmlWriter);
    }

    @Test
    void createStopPlaces_Success() {
        StopPlacesDto dto = createStopPlacesDto();
        org.rutebanken.tiamat.model.StopPlace tiamatStopPlace =
                createTiamatStopPlace();
        AsyncStopPlaceJob job = createJob(
                1L,
                AsyncStopPlaceJobStatus.PROCESSING
        );

        when(netexMapper.mapStopsToTiamatModel(anyList())).thenReturn(
                List.of(tiamatStopPlace)
        );
        when(jobService.createJob()).thenReturn(job);

        StopPlaceJobDto result = facade.createStopPlaces(dto);

        assertNotNull(result);
        assertEquals(1, result.jobId());
        assertEquals(AsyncStopPlaceJobStatus.PROCESSING, result.status());
        verify(asyncProcessor).processCreateStopPlace(1L, tiamatStopPlace);
    }

    @Test
    void createStopPlaces_MultipleStopPlaces_ThrowsException() {
        StopPlacesDto dto = createStopPlacesDto();
        org.rutebanken.tiamat.model.StopPlace stopPlace1 =
                createTiamatStopPlace();
        org.rutebanken.tiamat.model.StopPlace stopPlace2 =
                createTiamatStopPlace();
        AsyncStopPlaceJob job = createJob(1L, AsyncStopPlaceJobStatus.FAILED);

        when(netexMapper.mapStopsToTiamatModel(anyList())).thenReturn(
                List.of(stopPlace1, stopPlace2)
        );
        when(jobService.createJob()).thenReturn(job);
        when(jobService.fail(eq(1L), anyString())).thenReturn(job);

        StopPlaceJobDto result = facade.createStopPlaces(dto);

        assertEquals(AsyncStopPlaceJobStatus.FAILED, result.status());
        verify(jobService).fail(
                eq(1L),
                contains("Only one stop place allowed")
        );
        verify(asyncProcessor, never()).processCreateStopPlace(
                anyLong(),
                any()
        );
    }

    @Test
    void createStopPlaces_ParentStopPlace_ThrowsException() {
        StopPlacesDto dto = createStopPlacesDto();
        org.rutebanken.tiamat.model.StopPlace tiamatStopPlace =
                createTiamatStopPlace();
        tiamatStopPlace.setParentStopPlace(true);
        AsyncStopPlaceJob job = createJob(1L, AsyncStopPlaceJobStatus.FAILED);

        when(netexMapper.mapStopsToTiamatModel(anyList())).thenReturn(
                List.of(tiamatStopPlace)
        );
        when(jobService.createJob()).thenReturn(job);
        when(jobService.fail(eq(1L), anyString())).thenReturn(job);

        StopPlaceJobDto result = facade.createStopPlaces(dto);

        assertEquals(AsyncStopPlaceJobStatus.FAILED, result.status());
        verify(jobService).fail(
                eq(1L),
                contains("Only mono-modal stop place allowed")
        );
        verify(asyncProcessor, never()).processCreateStopPlace(
                anyLong(),
                any()
        );
    }

    @Test
    void createStopPlaces_WithChildren_ThrowsException() {
        StopPlacesDto dto = createStopPlacesDto();
        org.rutebanken.tiamat.model.StopPlace tiamatStopPlace =
                createTiamatStopPlace();
        tiamatStopPlace
                .getChildren()
                .add(new org.rutebanken.tiamat.model.StopPlace());
        AsyncStopPlaceJob job = createJob(1L, AsyncStopPlaceJobStatus.FAILED);

        when(netexMapper.mapStopsToTiamatModel(anyList())).thenReturn(
                List.of(tiamatStopPlace)
        );
        when(jobService.createJob()).thenReturn(job);
        when(jobService.fail(eq(1L), anyString())).thenReturn(job);

        StopPlaceJobDto result = facade.createStopPlaces(dto);

        assertEquals(AsyncStopPlaceJobStatus.FAILED, result.status());
        verify(jobService).fail(
                eq(1L),
                contains("Only mono-modal stop place allowed")
        );
        verify(asyncProcessor, never()).processCreateStopPlace(
                anyLong(),
                any()
        );
    }

    @Test
    void updateStopPlace_Success() {
        StopPlacesDto dto = createStopPlacesDto();
        org.rutebanken.tiamat.model.StopPlace tiamatStopPlace =
                createTiamatStopPlace();
        tiamatStopPlace.setNetexId("NSR:StopPlace:100");
        AsyncStopPlaceJob job = createJob(
                2L,
                AsyncStopPlaceJobStatus.PROCESSING
        );

        when(netexMapper.mapStopsToTiamatModel(anyList())).thenReturn(
                List.of(tiamatStopPlace)
        );
        when(jobService.createJob()).thenReturn(job);

        StopPlaceJobDto result = facade.updateStopPlace(dto);

        assertNotNull(result);
        assertEquals(2L, result.jobId());
        assertEquals(AsyncStopPlaceJobStatus.PROCESSING, result.status());
        verify(asyncProcessor).processUpdateStopPlace(2L, tiamatStopPlace);
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
        assertEquals(3, result.jobId());
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
        when(jobService.fail(eq(3L), anyString())).thenReturn(job);

        StopPlaceJobDto result = facade.deleteStopPlace(stopPlaceId);

        assertEquals(AsyncStopPlaceJobStatus.FAILED, result.status());
        verify(jobService).fail(eq(3L), eq("Delete failed"));
    }

    private StopPlacesDto createStopPlacesDto() {
        StopPlacesDto dto = new StopPlacesDto();
        StopPlace netexStopPlace = new StopPlace()
                .withId("NSR:StopPlace:100")
                .withName(
                        new org.rutebanken.netex.model.MultilingualString().withValue(
                                "Test Stop"
                        )
                );
        dto.setStopPlaces(Collections.singletonList(netexStopPlace));
        return dto;
    }

    private org.rutebanken.tiamat.model.StopPlace createTiamatStopPlace() {
        org.rutebanken.tiamat.model.StopPlace stopPlace =
                new org.rutebanken.tiamat.model.StopPlace();
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        return stopPlace;
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

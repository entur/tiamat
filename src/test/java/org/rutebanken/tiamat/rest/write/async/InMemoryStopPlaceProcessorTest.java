package org.rutebanken.tiamat.rest.write.async;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rutebanken.netex.model.KeyListStructure;
import org.rutebanken.netex.model.KeyValueStructure;
import org.rutebanken.netex.model.SiteRefStructure;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.model.job.StopPlaceIdMapping;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.rest.write.JobService;
import org.rutebanken.tiamat.rest.write.StopPlaceWriteDomainService;
import org.rutebanken.tiamat.rest.write.dto.StopPlacesDto;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InMemoryStopPlaceProcessorTest {

    @Mock
    private JobService jobService;

    @Mock
    private StopPlaceWriteDomainService domainService;

    @Mock
    private NetexMapper netexMapper;

    private InMemoryStopPlaceProcessor processor;

    private static final Long JOB_ID = 42L;

    @BeforeEach
    void setup() {
        processor = new InMemoryStopPlaceProcessor(jobService, domainService, netexMapper);
    }

    @Test
    void processCreateStopPlace_Success_CallsSucceedWithIdMapping() {
        var dto = singleStopPlaceDto("CLIENT:StopPlace:1");
        var tiamatStop = monoModalTiamatStop("NSR:StopPlace:100");
        var netexStop = dto.getStopPlaces().getFirst();

        when(domainService.createStopPlace(netexStop)).thenReturn(tiamatStop);

        processor.processCreateStopPlace(JOB_ID, dto);

        verify(domainService).createStopPlace(netexStop);
        verify(jobService).succeed(
                eq(JOB_ID),
                eq(List.of(new StopPlaceIdMapping("CLIENT:StopPlace:1", "NSR:StopPlace:100")))
        );
    }

    @Test
    void processCreateStopPlace_DomainServiceThrows_CallsFail() {
        var dto = singleStopPlaceDto("CLIENT:StopPlace:1");
        var netexStop = dto.getStopPlaces().getFirst();
        var exception = new RuntimeException("DB error");

        when(domainService.createStopPlace(netexStop)).thenThrow(exception);

        processor.processCreateStopPlace(JOB_ID, dto);

        verify(jobService).fail(JOB_ID, exception);
        verify(jobService, never()).succeed(any(), any());
    }

    @Test
    void processCreateStopPlace_ZeroStopPlaces_CallsFail() {
        var dto = new StopPlacesDto();
        dto.setStopPlaces(Collections.emptyList());

        processor.processCreateStopPlace(JOB_ID, dto);

        verify(jobService).fail(eq(JOB_ID), any(IllegalArgumentException.class));
        verify(domainService, never()).createStopPlace(any());
    }

    @Test
    void processCreateStopPlace_MultipleStopPlaces_CallsFail() {
        var dto = new StopPlacesDto();
        dto.setStopPlaces(List.of(new StopPlace(), new StopPlace()));

        processor.processCreateStopPlace(JOB_ID, dto);

        verify(jobService).fail(eq(JOB_ID), any(IllegalArgumentException.class));
        verify(domainService, never()).createStopPlace(any());
    }

    @Test
    void processCreateStopPlace_ParentStopPlace_CallsFail() {
        var dto = singleStopPlaceDto("CLIENT:StopPlace:1");
        // Mark as parent via IS_PARENT_STOP_PLACE key
        var netexStop = dto.getStopPlaces().getFirst();
        var kv = new KeyValueStructure().withKey("IS_PARENT_STOP_PLACE").withValue("true");
        netexStop.setKeyList(new KeyListStructure().withKeyValue(kv));

        processor.processCreateStopPlace(JOB_ID, dto);

        verify(jobService).fail(eq(JOB_ID), any(IllegalArgumentException.class));
        verify(domainService, never()).createStopPlace(any());
    }

    @Test
    void processCreateStopPlace_ChildStopPlace_CallsFail() {
        var dto = singleStopPlaceDto("CLIENT:StopPlace:1");
        // Mark as child via parentSiteRef
        var netexStop = dto.getStopPlaces().getFirst();
        netexStop.setParentSiteRef(new SiteRefStructure().withRef("NSR:StopPlace:999"));

        processor.processCreateStopPlace(JOB_ID, dto);

        verify(jobService).fail(eq(JOB_ID), any(IllegalArgumentException.class));
        verify(domainService, never()).createStopPlace(any());
    }

    @Test
    void processUpdateStopPlace_Success_CallsSucceedWithNullCreatedIds() {
        var dto = singleStopPlaceDto("NSR:StopPlace:200");
        var netexStop = dto.getStopPlaces().getFirst();

        processor.processUpdateStopPlace(JOB_ID, dto);

        verify(domainService).updateStopPlace(netexStop);
        verify(jobService).succeed(JOB_ID, null);
    }

    @Test
    void processUpdateStopPlace_DomainServiceThrows_CallsFail() {
        var dto = singleStopPlaceDto("NSR:StopPlace:200");
        var netexStop = dto.getStopPlaces().getFirst();
        var exception = new IllegalArgumentException("No changes detected");

        doThrow(exception).when(domainService).updateStopPlace(netexStop);

        processor.processUpdateStopPlace(JOB_ID, dto);

        verify(jobService).fail(JOB_ID, exception);
        verify(jobService, never()).succeed(any(), any());
    }

    @Test
    void processUpdateStopPlace_ParentStopPlace_CallsFail() {
        var dto = singleStopPlaceDto("NSR:StopPlace:200");
        var netexStop = dto.getStopPlaces().getFirst();
        var kv = new KeyValueStructure().withKey("IS_PARENT_STOP_PLACE").withValue("true");
        netexStop.setKeyList(new KeyListStructure().withKeyValue(kv));

        processor.processUpdateStopPlace(JOB_ID, dto);

        verify(jobService).fail(eq(JOB_ID), any(IllegalArgumentException.class));
        verify(domainService, never()).updateStopPlace(any());
    }

    @Test
    void processDeleteStopPlace_Success_CallsSucceedWithNullCreatedIds() {
        processor.processDeleteStopPlace(JOB_ID, "NSR:StopPlace:300");

        verify(domainService).deleteStopPlace("NSR:StopPlace:300");
        verify(jobService).succeed(JOB_ID, null);
    }

    @Test
    void processDeleteStopPlace_DomainServiceThrows_CallsFail() {
        var exception = new RuntimeException("Not found");
        doThrow(exception)
                .when(domainService).deleteStopPlace("NSR:StopPlace:300");

        processor.processDeleteStopPlace(JOB_ID, "NSR:StopPlace:300");

        verify(jobService).fail(JOB_ID, exception);
        verify(jobService, never()).succeed(any(), any());
    }

    private StopPlacesDto singleStopPlaceDto(String id) {
        var netexStop = new StopPlace();
        netexStop.setId(id);
        var dto = new StopPlacesDto();
        dto.setStopPlaces(List.of(netexStop));
        return dto;
    }

    private org.rutebanken.tiamat.model.StopPlace monoModalTiamatStop(String netexId) {
        var stop = new org.rutebanken.tiamat.model.StopPlace();
        stop.setNetexId(netexId);
        return stop;
    }
}


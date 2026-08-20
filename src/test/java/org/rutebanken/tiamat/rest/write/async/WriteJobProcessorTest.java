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
import org.rutebanken.tiamat.rest.write.JobService;
import org.rutebanken.tiamat.rest.write.StopPlacesPayloadUnmarshaller;
import org.rutebanken.tiamat.rest.write.StopPlaceWriteDomainService;
import org.rutebanken.tiamat.rest.write.dto.StopPlacesDto;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WriteJobProcessorTest {

    @Mock
    private JobService jobService;

    @Mock
    private StopPlaceWriteDomainService domainService;

    @Mock
    private StopPlacesPayloadUnmarshaller payloadUnmarshaller;

    private WriteJobProcessor processor;

    private static final Long JOB_ID = 42L;
    private static final byte[] PAYLOAD = "<stopPlaces/>".getBytes(java.nio.charset.StandardCharsets.UTF_8);

    @BeforeEach
    void setup() {
        processor = new WriteJobProcessor(jobService, domainService, payloadUnmarshaller);
    }

    @Test
    void processCreateStopPlace_Success_CallsSucceedWithIdMapping() {
        var dto = singleStopPlaceDto("CLIENT:StopPlace:1");
        var tiamatStop = monoModalTiamatStop("NSR:StopPlace:100");
        var netexStop = dto.getStopPlaces().getFirst();

        when(domainService.createStopPlace(netexStop)).thenReturn(tiamatStop);

        when(payloadUnmarshaller.unmarshal(PAYLOAD)).thenReturn(dto);
        processor.process(WriteJobMessage.create(JOB_ID, PAYLOAD));

        verify(domainService).createStopPlace(netexStop);
        verify(jobService).succeed(
                eq(JOB_ID),
                eq(List.of(new StopPlaceIdMapping("CLIENT:StopPlace:1", "NSR:StopPlace:100")))
        );
    }

    @Test
    void processCreateStopPlace_DomainServiceThrows_Throws() {
        var dto = singleStopPlaceDto("CLIENT:StopPlace:1");
        var netexStop = dto.getStopPlaces().getFirst();
        var exception = new RuntimeException("DB error");

        when(domainService.createStopPlace(netexStop)).thenThrow(exception);

        when(payloadUnmarshaller.unmarshal(PAYLOAD)).thenReturn(dto);
        assertThatThrownBy(() -> processor.process(WriteJobMessage.create(JOB_ID, PAYLOAD)))
                .isSameAs(exception);
        verify(jobService, never()).succeed(any(), any());
    }

    @Test
    void processCreateStopPlace_ZeroStopPlaces_Throws() {
        var dto = new StopPlacesDto();
        dto.setStopPlaces(Collections.emptyList());

        when(payloadUnmarshaller.unmarshal(PAYLOAD)).thenReturn(dto);
        assertThatThrownBy(() -> processor.process(WriteJobMessage.create(JOB_ID, PAYLOAD)))
                .isInstanceOf(IllegalArgumentException.class);
        verify(domainService, never()).createStopPlace(any());
    }

    @Test
    void processCreateStopPlace_MultipleStopPlaces_Throws() {
        var dto = new StopPlacesDto();
        dto.setStopPlaces(List.of(new StopPlace(), new StopPlace()));

        when(payloadUnmarshaller.unmarshal(PAYLOAD)).thenReturn(dto);
        assertThatThrownBy(() -> processor.process(WriteJobMessage.create(JOB_ID, PAYLOAD)))
                .isInstanceOf(IllegalArgumentException.class);
        verify(domainService, never()).createStopPlace(any());
    }

    @Test
    void processCreateStopPlace_ParentStopPlace_Throws() {
        var dto = singleStopPlaceDto("CLIENT:StopPlace:1");
        // Mark as parent via IS_PARENT_STOP_PLACE key
        var netexStop = dto.getStopPlaces().getFirst();
        var kv = new KeyValueStructure().withKey("IS_PARENT_STOP_PLACE").withValue("true");
        netexStop.setKeyList(new KeyListStructure().withKeyValue(kv));

        when(payloadUnmarshaller.unmarshal(PAYLOAD)).thenReturn(dto);
        assertThatThrownBy(() -> processor.process(WriteJobMessage.create(JOB_ID, PAYLOAD)))
                .isInstanceOf(IllegalArgumentException.class);
        verify(domainService, never()).createStopPlace(any());
    }

    @Test
    void processCreateStopPlace_ChildStopPlace_Throws() {
        var dto = singleStopPlaceDto("CLIENT:StopPlace:1");
        // Mark as child via parentSiteRef
        var netexStop = dto.getStopPlaces().getFirst();
        netexStop.setParentSiteRef(new SiteRefStructure().withRef("NSR:StopPlace:999"));

        when(payloadUnmarshaller.unmarshal(PAYLOAD)).thenReturn(dto);
        assertThatThrownBy(() -> processor.process(WriteJobMessage.create(JOB_ID, PAYLOAD)))
                .isInstanceOf(IllegalArgumentException.class);
        verify(domainService, never()).createStopPlace(any());
    }

    @Test
    void processUpdateStopPlace_Success_CallsSucceedWithNullCreatedIds() {
        var dto = singleStopPlaceDto("NSR:StopPlace:200");
        var netexStop = dto.getStopPlaces().getFirst();

        when(payloadUnmarshaller.unmarshal(PAYLOAD)).thenReturn(dto);
        processor.process(WriteJobMessage.update(JOB_ID, PAYLOAD));

        verify(domainService).updateStopPlace(netexStop);
        verify(jobService).succeed(JOB_ID, null);
    }

    @Test
    void processUpdateStopPlace_DomainServiceThrows_Throws() {
        var dto = singleStopPlaceDto("NSR:StopPlace:200");
        var netexStop = dto.getStopPlaces().getFirst();
        var exception = new IllegalArgumentException("No changes detected");

        doThrow(exception).when(domainService).updateStopPlace(netexStop);

        when(payloadUnmarshaller.unmarshal(PAYLOAD)).thenReturn(dto);
        assertThatThrownBy(() -> processor.process(WriteJobMessage.update(JOB_ID, PAYLOAD)))
                .isSameAs(exception);
        verify(jobService, never()).succeed(any(), any());
    }

    @Test
    void processUpdateStopPlace_ParentStopPlace_Throws() {
        var dto = singleStopPlaceDto("NSR:StopPlace:200");
        var netexStop = dto.getStopPlaces().getFirst();
        var kv = new KeyValueStructure().withKey("IS_PARENT_STOP_PLACE").withValue("true");
        netexStop.setKeyList(new KeyListStructure().withKeyValue(kv));

        when(payloadUnmarshaller.unmarshal(PAYLOAD)).thenReturn(dto);
        assertThatThrownBy(() -> processor.process(WriteJobMessage.update(JOB_ID, PAYLOAD)))
                .isInstanceOf(IllegalArgumentException.class);
        verify(domainService, never()).updateStopPlace(any());
    }

    @Test
    void processDeleteStopPlace_Success_CallsSucceedWithNullCreatedIds() {
        processor.process(WriteJobMessage.delete(JOB_ID, "NSR:StopPlace:300"));

        verify(domainService).deleteStopPlace("NSR:StopPlace:300");
        verify(jobService).succeed(JOB_ID, null);
    }

    @Test
    void processDeleteStopPlace_DomainServiceThrows_Throws() {
        var exception = new RuntimeException("Not found");
        doThrow(exception)
                .when(domainService).deleteStopPlace("NSR:StopPlace:300");

        assertThatThrownBy(() -> processor.process(WriteJobMessage.delete(JOB_ID, "NSR:StopPlace:300")))
                .isSameAs(exception);
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


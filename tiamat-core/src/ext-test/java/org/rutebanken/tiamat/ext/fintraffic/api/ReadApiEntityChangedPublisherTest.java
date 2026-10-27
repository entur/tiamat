package org.rutebanken.tiamat.ext.fintraffic.api;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.hibernate.LazyInitializationException;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rutebanken.tiamat.changelog.EntityChangedEvent;
import org.rutebanken.tiamat.exporter.ServiceFrameElementCreator;
import org.rutebanken.tiamat.ext.fintraffic.api.repository.NetexRepository;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ReadApiEntityChangedPublisherTest {

    private ReadApiNetexMarshallingService marshallingService;
    private ReadApiEntityChangedPublisher publisher;

    private ListAppender<ILoggingEvent> appender;
    private Logger publisherLogger;

    @AfterEach
    void detachAppender() {
        publisherLogger.detachAppender(appender);
    }

    private List<ILoggingEvent> events(Level level) {
        return appender.list.stream().filter(e -> e.getLevel() == level).toList();
    }

    @BeforeEach
    void setUp() {
        publisherLogger = (Logger) LoggerFactory.getLogger(ReadApiEntityChangedPublisher.class);
        appender = new ListAppender<>();
        appender.start();
        publisherLogger.addAppender(appender);
        marshallingService = mock(ReadApiNetexMarshallingService.class);
        publisher = new ReadApiEntityChangedPublisher(marshallingService);
    }

    @Test
    void onChangeCallsMarshallingService() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("FSR:StopPlace:1");
        stopPlace.setVersion(1L);
        stopPlace.setChanged(Instant.now());

        publisher.onChange(stopPlace);

        verify(marshallingService, times(1)).handleEntityChange(eq(stopPlace), any(EntityChangedEvent.class));
    }

    @Test
    void onChangeHandlesStopPlaceWithChildren() {
        StopPlace parent = new StopPlace();
        parent.setNetexId("FSR:StopPlace:1");
        parent.setVersion(1L);
        parent.setChanged(Instant.now());

        StopPlace child = new StopPlace();
        child.setNetexId("FSR:StopPlace:2");
        child.setVersion(1L);
        child.setChanged(Instant.now());

        parent.setChildren(Set.of(child));

        publisher.onChange(parent);

        // Should process both parent and child (2 total calls)
        verify(marshallingService, times(2)).handleEntityChange(any(StopPlace.class), any(EntityChangedEvent.class));
    }

    @Test
    void onChangeHandlesStopPlaceWithoutChildren() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("FSR:StopPlace:1");
        stopPlace.setVersion(1L);
        stopPlace.setChanged(Instant.now());
        stopPlace.setChildren(Set.of());

        publisher.onChange(stopPlace);

        // Should only process the stop place
        verify(marshallingService, times(1)).handleEntityChange(eq(stopPlace), any(EntityChangedEvent.class));
    }

    @Test
    void onChangeHandlesNonStopPlaceEntity() {
        Parking parking = new Parking();
        parking.setNetexId("FSR:Parking:1");
        parking.setVersion(1L);
        parking.setChanged(Instant.now());

        publisher.onChange(parking);

        verify(marshallingService, times(1)).handleEntityChange(eq(parking), any(EntityChangedEvent.class));
    }

    @Test
    void onDeleteCallsMarshallingService() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("FSR:StopPlace:1");
        stopPlace.setVersion(1L);
        stopPlace.setChanged(Instant.now());

        publisher.onDelete(stopPlace);

        verify(marshallingService, times(1)).handleEntityDeleted(stopPlace);
        verify(marshallingService, never()).handleEntityChange(any(), any());
    }

    @Test
    void onDeleteOfParkingMarksRowDeletedWithoutMappingToNetex() {
        NetexMapper netexMapper = mock(NetexMapper.class);
        when(netexMapper.mapToNetexModel(any(Parking.class)))
                .thenThrow(new LazyInitializationException("could not initialize proxy - no Session"));
        NetexRepository netexRepository = mock(NetexRepository.class);
        ReadApiEntityChangedPublisher realServicePublisher = new ReadApiEntityChangedPublisher(
                new ReadApiNetexMarshallingService(
                        netexMapper,
                        netexRepository,
                        mock(ServiceFrameElementCreator.class),
                        mock(SearchKeyService.class),
                        mock(NetexEntityEnricher.class)));

        Parking parking = new Parking();
        parking.setNetexId("FSR:Parking:1");
        parking.setVersion(1L);
        parking.setChanged(Instant.now());

        realServicePublisher.onDelete(parking);

        verify(netexRepository, times(1)).markEntityDeleted("FSR:Parking:1", "Parking");
        verify(netexRepository, never()).upsertEntities(any());
        verifyNoInteractions(netexMapper);
    }

    @Test
    void onDeleteOfParkingUsesDeletePathOnly() {
        Parking parking = new Parking();
        parking.setNetexId("FSR:Parking:1");
        parking.setVersion(1L);
        parking.setChanged(Instant.now());

        publisher.onDelete(parking);

        verify(marshallingService, times(1)).handleEntityDeleted(parking);
        verify(marshallingService, never()).handleEntityChange(any(), any());
    }

    private static Parking parking() {
        Parking parking = new Parking();
        parking.setNetexId("FSR:Parking:1");
        parking.setVersion(1L);
        parking.setChanged(Instant.now());
        return parking;
    }

    @Test
    void onDeleteLogsSuccessOnlyWhenParkingUpdateSucceeded() {
        Parking parking = parking();
        when(marshallingService.handleEntityDeleted(parking)).thenReturn(true);

        publisher.onDelete(parking);

        assertTrue(events(Level.ERROR).isEmpty());
        assertTrue(events(Level.INFO).stream().anyMatch(e -> e.getFormattedMessage().startsWith("Processed entity deleted event")));
    }

    @Test
    void onDeleteLogsErrorAndNoSuccessWhenParkingUpdateFailed() {
        Parking parking = parking();
        when(marshallingService.handleEntityDeleted(parking)).thenReturn(false);

        publisher.onDelete(parking);

        assertTrue(events(Level.INFO).stream().noneMatch(e -> e.getFormattedMessage().startsWith("Processed")));
        List<ILoggingEvent> errors = events(Level.ERROR);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).getFormattedMessage().contains("FSR:Parking:1"));
        assertTrue(errors.get(0).getFormattedMessage().contains("Parking"));
    }

    @Test
    void onDeleteLogsErrorWhenStopPlaceUpdateFailed() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("FSR:StopPlace:1");
        stopPlace.setVersion(1L);
        when(marshallingService.handleEntityDeleted(stopPlace)).thenReturn(false);

        publisher.onDelete(stopPlace);

        assertTrue(events(Level.INFO).stream().noneMatch(e -> e.getFormattedMessage().startsWith("Processed")));
        assertEquals(1, events(Level.ERROR).size());
        assertTrue(events(Level.ERROR).get(0).getFormattedMessage().contains("StopPlace"));
    }

    @Test
    void onChangeLogsSuccessOnlyWhenUpdateSucceeded() {
        Parking parking = parking();
        when(marshallingService.handleEntityChange(eq(parking), any(EntityChangedEvent.class))).thenReturn(true);

        publisher.onChange(parking);

        assertTrue(events(Level.ERROR).isEmpty());
        assertTrue(events(Level.INFO).stream().anyMatch(e -> e.getFormattedMessage().startsWith("Processed entity changed event")));
    }

    @Test
    void onChangeLogsErrorAndNoSuccessWhenUpdateFailed() {
        Parking parking = parking();
        when(marshallingService.handleEntityChange(eq(parking), any(EntityChangedEvent.class))).thenReturn(false);

        publisher.onChange(parking);

        assertTrue(events(Level.INFO).stream().noneMatch(e -> e.getFormattedMessage().startsWith("Processed")));
        assertEquals(1, events(Level.ERROR).size());
        assertTrue(events(Level.ERROR).get(0).getFormattedMessage().contains("FSR:Parking:1"));
    }
}

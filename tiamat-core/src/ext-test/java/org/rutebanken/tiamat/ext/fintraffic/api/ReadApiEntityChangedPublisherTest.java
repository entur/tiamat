package org.rutebanken.tiamat.ext.fintraffic.api;

import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rutebanken.tiamat.changelog.EntityChangedEvent;
import org.rutebanken.tiamat.exporter.ServiceFrameElementCreator;
import org.rutebanken.tiamat.ext.fintraffic.api.repository.NetexRepository;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;

import java.time.Instant;
import java.util.Set;

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

    @BeforeEach
    void setUp() {
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

        verify(marshallingService, times(1)).handleEntityChange(eq(stopPlace), any(EntityChangedEvent.class));
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

        verify(marshallingService, times(1)).handleParkingDeleted(parking);
        verify(marshallingService, never()).handleEntityChange(any(), any());
    }
}

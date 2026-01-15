package org.rutebanken.tiamat.ext.fintraffic.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rutebanken.tiamat.changelog.EntityChangedEvent;
import org.rutebanken.tiamat.exporter.ServiceFrameElementCreator;
import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiEntityInRecord;
import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiEntityStatus;
import org.rutebanken.tiamat.ext.fintraffic.api.repository.NetexRepository;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReadApiNetexMarshallingServiceTest {

    private NetexMapper netexMapper;
    private NetexRepository netexRepository;
    private ServiceFrameElementCreator serviceFrameElementCreator;
    private SearchKeyService searchKeyService;
    private ReadApiNetexMarshallingService marshallingService;

    @BeforeEach
    void setUp() {
        netexMapper = mock(NetexMapper.class);
        netexRepository = mock(NetexRepository.class);
        serviceFrameElementCreator = mock(ServiceFrameElementCreator.class);
        searchKeyService = mock(SearchKeyService.class);

        marshallingService = new ReadApiNetexMarshallingService(
                netexMapper,
                netexRepository,
                serviceFrameElementCreator,
                searchKeyService
        );
    }

    @Test
    void createEntityRecordsForStopPlace() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("FSR:StopPlace:1");
        stopPlace.setVersion(1L);
        stopPlace.setChanged(Instant.now());

        org.rutebanken.netex.model.StopPlace netexStopPlace = new org.rutebanken.netex.model.StopPlace();
        netexStopPlace.setId(stopPlace.getNetexId());
        netexStopPlace.setVersion(String.valueOf(stopPlace.getVersion()));

        when(netexMapper.mapToNetexModel(stopPlace)).thenReturn(netexStopPlace);
        when(serviceFrameElementCreator.createServiceFrameElements(stopPlace)).thenReturn(List.of());
        when(searchKeyService.generateSearchKeyJSON(stopPlace)).thenReturn("{}");

        Collection<ReadApiEntityInRecord> records = marshallingService.createEntityRecords(stopPlace, ReadApiEntityStatus.CURRENT);

        assertThat(records, notNullValue());
        assertThat(records.size(), equalTo(1));

        ReadApiEntityInRecord record = records.iterator().next();
        assertThat(record.id(), equalTo("FSR:StopPlace:1"));
        assertThat(record.version(), equalTo(1L));
        assertThat(record.status(), equalTo(ReadApiEntityStatus.CURRENT));
    }

    @Test
    void createEntityRecordsForParking() {
        Parking parking = new Parking();
        parking.setNetexId("FSR:Parking:1");
        parking.setVersion(1L);
        parking.setChanged(Instant.now());

        org.rutebanken.netex.model.Parking netexParking = new org.rutebanken.netex.model.Parking();
        netexParking.setId(parking.getNetexId());
        netexParking.setVersion(String.valueOf(parking.getVersion()));

        when(netexMapper.mapToNetexModel(parking)).thenReturn(netexParking);
        when(searchKeyService.generateSearchKeyJSON(parking)).thenReturn("{}");

        Collection<ReadApiEntityInRecord> records = marshallingService.createEntityRecords(parking, ReadApiEntityStatus.CURRENT);

        assertThat(records, notNullValue());
        assertThat(records.size(), equalTo(1));

        ReadApiEntityInRecord record = records.iterator().next();
        assertThat(record.id(), equalTo("FSR:Parking:1"));
        assertThat(record.version(), equalTo(1L));
        assertThat(record.status(), equalTo(ReadApiEntityStatus.CURRENT));
    }

    @Test
    void createEntityRecordsWithParentRef() {
        Parking parking = new Parking();
        parking.setNetexId("FSR:Parking:1");
        parking.setVersion(1L);
        parking.setChanged(Instant.now());

        SiteRefStructure parentRef = new SiteRefStructure();
        parentRef.setRef("FSR:StopPlace:100");
        parking.setParentSiteRef(parentRef);

        org.rutebanken.netex.model.Parking netexParking = new org.rutebanken.netex.model.Parking();
        netexParking.setId(parking.getNetexId());
        netexParking.setVersion(String.valueOf(parking.getVersion()));

        when(netexMapper.mapToNetexModel(parking)).thenReturn(netexParking);
        when(searchKeyService.generateSearchKeyJSON(parking)).thenReturn("{}");

        Collection<ReadApiEntityInRecord> records = marshallingService.createEntityRecords(parking, ReadApiEntityStatus.CURRENT);

        ReadApiEntityInRecord record = records.iterator().next();
        assertThat(record.parentRefs().length, equalTo(1));
        assertThat(record.parentRefs()[0], equalTo("FSR:StopPlace:100"));
    }

    @Test
    void marshallToXMLProducesValidXML() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("FSR:StopPlace:1");
        stopPlace.setVersion(1L);
        stopPlace.setChanged(Instant.now());

        org.rutebanken.netex.model.StopPlace netexStopPlace = new org.rutebanken.netex.model.StopPlace();
        netexStopPlace.setId(stopPlace.getNetexId());
        netexStopPlace.setVersion(String.valueOf(stopPlace.getVersion()));

        when(netexMapper.mapToNetexModel(stopPlace)).thenReturn(netexStopPlace);

        String xml = marshallingService.marshallToXML(stopPlace);

        assertThat(xml, notNullValue());
        assertThat(xml, containsString("StopPlace"));
        assertThat(xml, containsString("FSR:StopPlace:1"));
    }

    @Test
    void handleEntityChangeRetries() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setNetexId("FSR:StopPlace:1");
        stopPlace.setVersion(1L);
        stopPlace.setChanged(Instant.now());

        org.rutebanken.netex.model.StopPlace netexStopPlace = new org.rutebanken.netex.model.StopPlace();
        netexStopPlace.setId(stopPlace.getNetexId());
        netexStopPlace.setVersion(String.valueOf(stopPlace.getVersion()));

        when(netexMapper.mapToNetexModel(stopPlace)).thenReturn(netexStopPlace);
        when(serviceFrameElementCreator.createServiceFrameElements(stopPlace)).thenReturn(List.of());
        when(searchKeyService.generateSearchKeyJSON(stopPlace)).thenReturn("{}");

        EntityChangedEvent event = new EntityChangedEvent();
        event.entityId = stopPlace.getNetexId();
        event.entityVersion = stopPlace.getVersion();
        event.crudAction = EntityChangedEvent.CrudAction.UPDATE;
        event.entityType = EntityChangedEvent.EntityType.STOP_PLACE;

        marshallingService.handleEntityChange(stopPlace, event);

        verify(netexRepository, times(1)).upsertEntities(any());
    }

    @Test
    void createEntityRecordsWithDeletedStatus() {
        Parking parking = new Parking();
        parking.setNetexId("FSR:Parking:1");
        parking.setVersion(1L);
        parking.setChanged(Instant.now());

        org.rutebanken.netex.model.Parking netexParking = new org.rutebanken.netex.model.Parking();
        netexParking.setId(parking.getNetexId());
        netexParking.setVersion(String.valueOf(parking.getVersion()));

        when(netexMapper.mapToNetexModel(parking)).thenReturn(netexParking);
        when(searchKeyService.generateSearchKeyJSON(parking)).thenReturn("{}");

        Collection<ReadApiEntityInRecord> records = marshallingService.createEntityRecords(parking, ReadApiEntityStatus.DELETED);

        ReadApiEntityInRecord record = records.iterator().next();
        assertThat(record.status(), equalTo(ReadApiEntityStatus.DELETED));
    }
}


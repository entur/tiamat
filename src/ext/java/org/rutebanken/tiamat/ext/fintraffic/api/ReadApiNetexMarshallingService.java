package org.rutebanken.tiamat.ext.fintraffic.api;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.tiamat.changelog.EntityChangedEvent;
import org.rutebanken.tiamat.exporter.ServiceFrameElementCreator;
import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiEntityInRecord;
import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiEntityStatus;
import org.rutebanken.tiamat.ext.fintraffic.api.repository.NetexRepository;
import org.rutebanken.tiamat.ext.fintraffic.xml.NoNameSpaceXMLStreamWriter;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
public class ReadApiNetexMarshallingService {
    private static final ConcurrentHashMap<Class<?>, JAXBContext> jaxbContextMap = new ConcurrentHashMap<>();
    private static final ObjectFactory objectFactory = new ObjectFactory();
    private final NetexMapper netexMapper;
    private final NetexRepository netexRepository;
    private final ServiceFrameElementCreator serviceFrameElementCreator;
    private final SearchKeyService searchKeyService;

    private static final Map<Class<?>, Function<org.rutebanken.netex.model.EntityInVersionStructure, JAXBElement<?>>> JAXB_ELEMENT_FACTORIES = Map.of(
            org.rutebanken.netex.model.StopPlace.class, e -> objectFactory.createStopPlace((org.rutebanken.netex.model.StopPlace) e),
            org.rutebanken.netex.model.Parking.class, e -> objectFactory.createParking((org.rutebanken.netex.model.Parking) e),
            org.rutebanken.netex.model.TopographicPlace.class, e -> objectFactory.createTopographicPlace((org.rutebanken.netex.model.TopographicPlace) e),
            org.rutebanken.netex.model.ScheduledStopPoint.class, e -> objectFactory.createScheduledStopPoint((org.rutebanken.netex.model.ScheduledStopPoint) e),
            org.rutebanken.netex.model.PassengerStopAssignment.class, e -> objectFactory.createPassengerStopAssignment((org.rutebanken.netex.model.PassengerStopAssignment) e)
    );

    private final Logger logger = LoggerFactory.getLogger(ReadApiNetexMarshallingService.class);

    public ReadApiNetexMarshallingService(
            NetexMapper netexMapper,
            NetexRepository netexRepository,
            ServiceFrameElementCreator serviceFrameElementCreator,
            SearchKeyService searchKeyService
    ) {
        this.netexMapper = netexMapper;
        this.netexRepository = netexRepository;
        this.serviceFrameElementCreator = serviceFrameElementCreator;
        this.searchKeyService = searchKeyService;
    }

    public void handleEntityChange(EntityInVersionStructure entity, EntityChangedEvent event) {
        // Try up to 3 times and then log error
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                Collection<ReadApiEntityInRecord> records = this.createEntityRecords(entity, crudToStatus(event.crudAction));
                netexRepository.upsertEntities(records);
                return;
            } catch (Exception e) {
                logger.error("Attempt {} to process entity change for entity id: {} failed", attempt, entity.getId(), e);
                try {
                    Thread.sleep(attempt * 200L); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }
            }
        }
        logger.error("All attempts to process entity change for entity id: {} have failed", entity.getId());
    }

    private Collection<ReadApiEntityInRecord> createEntityRecords(EntityInVersionStructure entity, ReadApiEntityStatus status) {
        String searchKey = searchKeyService.generateSearchKeyJSON(entity);
        if (entity instanceof StopPlace stopPlace) {
            ArrayList<ReadApiEntityInRecord> records = new ArrayList<>(this.createServiceFrameEntityRecords(stopPlace, status, searchKey));
            records.add(createEntityRecord(stopPlace, status, searchKey));
            return records;
        }
        return List.of(createEntityRecord(entity, status, searchKey));
    }

    private List<ReadApiEntityInRecord> createServiceFrameEntityRecords(StopPlace stopPlace, ReadApiEntityStatus status, String searchKey) {
        return serviceFrameElementCreator.createServiceFrameElements(stopPlace).stream().map(ssp -> {
            String xml = marshallToXML(ssp);
            return new ReadApiEntityInRecord(
                    ssp.getId(),
                    ssp.getClass().getSimpleName(),
                    searchKey,
                    xml,
                    Long.parseLong(ssp.getVersion()),
                    stopPlace.getChanged() != null ? stopPlace.getChanged().toEpochMilli() : 0L,
                    status,
                    new String[]{stopPlace.getNetexId()}
            );
        }).toList();
    }

    private static Optional<String> parentRef(EntityInVersionStructure entity) {
        if (entity instanceof StopPlace sp && sp.getParentSiteRef() != null) {
            return Optional.ofNullable(sp.getParentSiteRef().getRef());
        }
        if (entity instanceof Parking p && p.getParentSiteRef() != null) {
            return Optional.ofNullable(p.getParentSiteRef().getRef());
        }
        return Optional.empty();
    }

    private ReadApiEntityInRecord createEntityRecord(EntityInVersionStructure entity, ReadApiEntityStatus status, String searchKey) {
        String xml = this.marshallToXML(entity);
        Optional<String> parentRef = parentRef(entity);
        return new ReadApiEntityInRecord(
                entity.getNetexId(),
                entity.getClass().getSimpleName(),
                searchKey,
                xml,
                entity.getVersion(),
                entity.getChanged() != null ? entity.getChanged().toEpochMilli() : 0L,
                status,
                parentRef.map(ref -> new String[]{ref}).orElse(new String[]{})
        );
    }

    private static JAXBContext createJAXBContext(Class<?> clazz) {
        try {
            return JAXBContext.newInstance(clazz);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to create JAXBContext for class: " + clazz.getName(), e);
        }
    }

    private static Marshaller createMarshaller(Class<?> clazz) throws JAXBException {
        JAXBContext jaxbContext = jaxbContextMap.computeIfAbsent(clazz, ReadApiNetexMarshallingService::createJAXBContext);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        return marshaller;
    }

    private org.rutebanken.netex.model.EntityInVersionStructure mapToNetexModel(EntityInVersionStructure entity) {
        // Check supported types
        return switch (entity) {
            case StopPlace stopPlace -> netexMapper.mapToNetexModel(stopPlace);
            case Parking parking -> netexMapper.mapToNetexModel(parking);
            case TopographicPlace topographicPlace -> netexMapper.mapToNetexModel(topographicPlace);
            default ->
                    throw new IllegalArgumentException("Unsupported entity type for NeTEx mapping: " + entity.getClass().getName());
        };
    }

    private static JAXBElement<?> createJAXBElementForEntity(org.rutebanken.netex.model.EntityInVersionStructure entity) {
        Function<org.rutebanken.netex.model.EntityInVersionStructure, JAXBElement<?>> factory =
                JAXB_ELEMENT_FACTORIES.get(entity.getClass());
        if (factory == null) {
            throw new IllegalArgumentException("Unsupported entity type: " + entity.getClass().getName());
        }
        return factory.apply(entity);
    }

    public String marshallToXML(EntityInVersionStructure tiamatEntity) {
        return this.marshallToXML(mapToNetexModel(tiamatEntity));
    }

    public String marshallToXML(org.rutebanken.netex.model.EntityInVersionStructure netexEntity) {
        XMLOutputFactory xof = XMLOutputFactory.newInstance();

        try (StringWriter stringWriter = new StringWriter(512)) {
            XMLStreamWriter xsw = new NoNameSpaceXMLStreamWriter(xof.createXMLStreamWriter(stringWriter));
            JAXBElement<?> jaxbElement = createJAXBElementForEntity(netexEntity);
            Marshaller marshaller = createMarshaller(netexEntity.getClass());
            marshaller.marshal(jaxbElement, xsw);
            return stringWriter.toString();
        } catch (JAXBException | XMLStreamException | IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("Failed to marshall entity to NeTEx XML: " + netexEntity.getId(), e);
        }
    }

    private static ReadApiEntityStatus crudToStatus(EntityChangedEvent.CrudAction crudAction) {
        return switch (crudAction) {
            case CREATE, UPDATE -> ReadApiEntityStatus.CURRENT;
            case DELETE, REMOVE -> ReadApiEntityStatus.DELETED;
        };
    }
}

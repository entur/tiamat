package org.rutebanken.tiamat.exporter;

import org.rutebanken.netex.model.*;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.ParkingsInFrame_RelStructure;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopPlacesInFrame_RelStructure;
import org.rutebanken.tiamat.exporter.async.ListeningNetexMappingIterator;
import org.rutebanken.tiamat.exporter.async.NetexMappingIteratorList;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.exporter.params.ParkingSearch;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static javax.xml.bind.JAXBContext.newInstance;

/**
 * Stream data objects inside already serialized publication delivery.
 * To be able to export many stop places wihtout keeping them all in memory.
 */
@Transactional
@Component
public class StreamingPublicationDelivery {

    private static final Logger logger = LoggerFactory.getLogger(StreamingPublicationDelivery.class);

    private static final JAXBContext publicationDeliveryContext = createContext(PublicationDeliveryStructure.class);
    private static final ObjectFactory netexObjectFactory = new ObjectFactory();

    private final PublicationDeliveryHelper publicationDeliveryHelper;


    private final StopPlaceRepository stopPlaceRepository;
    private final ParkingRepository parkingRepository;
    private final PublicationDeliveryExporter publicationDeliveryExporter;
    private final TiamatSiteFrameExporter tiamatSiteFrameExporter;

    private final NetexMapper netexMapper;


    @Autowired
    public StreamingPublicationDelivery(PublicationDeliveryHelper publicationDeliveryHelper, StopPlaceRepository stopPlaceRepository, ParkingRepository parkingRepository, PublicationDeliveryExporter publicationDeliveryExporter, TiamatSiteFrameExporter tiamatSiteFrameExporter, NetexMapper netexMapper) {
        this.publicationDeliveryHelper = publicationDeliveryHelper;
        this.stopPlaceRepository = stopPlaceRepository;
        this.parkingRepository = parkingRepository;
        this.publicationDeliveryExporter = publicationDeliveryExporter;
        this.tiamatSiteFrameExporter = tiamatSiteFrameExporter;
        this.netexMapper = netexMapper;
    }
    public void stream(ExportParams exportParams, OutputStream outputStream) throws JAXBException, XMLStreamException, IOException, InterruptedException {

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryExporter.exportPublicationDeliveryWithoutStops();

        // Todo: use export params to generate a more descriptive name
        org.rutebanken.netex.model.SiteFrame netexSiteFrame = publicationDeliveryHelper.findSiteFrame(publicationDeliveryStructure);

        Iterator<org.rutebanken.tiamat.model.StopPlace> stopPlaceIterator = stopPlaceRepository.scrollStopPlaces(exportParams);
        Set<String> stopPlaceIds = new HashSet<>();
        // Override lists with custom iterator to be able to scroll database results on the fly.
        if(stopPlaceIterator.hasNext()) {
            StopPlacesInFrame_RelStructure stopPlacesInFrame_relStructure = new StopPlacesInFrame_RelStructure();

            Consumer<StopPlace> listener = (stopPlace) -> stopPlaceIds.add(stopPlace.getId());
            List<StopPlace> stopPlaces = new NetexMappingIteratorList<>(netexMapper, stopPlaceIterator, StopPlace.class, listener);
            setField(StopPlacesInFrame_RelStructure.class, "stopPlace", stopPlacesInFrame_relStructure, stopPlaces);
            netexSiteFrame.setStopPlaces(stopPlacesInFrame_relStructure);
        }

        ParkingSearch parkingSearch = ParkingSearch.newParkingSearchBuilder().setAllVersions(true).setParentSiteRefs(stopPlaceIds).build();

        Iterator<org.rutebanken.tiamat.model.Parking> parkingIterator = parkingRepository.scrollParkings(parkingSearch);
        if(parkingIterator.hasNext()) {
            ParkingsInFrame_RelStructure parkingsInFrame_relStructure = new ParkingsInFrame_RelStructure();
            List<Parking> parkings = new NetexMappingIteratorList<>(netexMapper, parkingIterator, Parking.class);
            setField(ParkingsInFrame_RelStructure.class, "parking", parkingsInFrame_relStructure, parkings);
            netexSiteFrame.setParkings(parkingsInFrame_relStructure);
        }

        Marshaller marshaller = createMarshaller();

        marshaller.marshal(netexObjectFactory.createPublicationDelivery(publicationDeliveryStructure), outputStream);
    }

    /**
     * Set field value with reflection.
     * Used for setting list values in netex model.
     */
    private void setField(Class clazz, String fieldName, Object instance, Object fieldValue) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, fieldValue);
        } catch (IllegalAccessException|NoSuchFieldException e) {
            throw new RuntimeException("Cannot set field "+fieldName +" of "+instance, e);
        }
    }

    private static JAXBContext createContext(Class clazz) {
        try {
            return newInstance(clazz);
        } catch (JAXBException e) {
            logger.warn("Could not create instance of jaxb context for class " + clazz, e);
            throw new RuntimeException(e);
        }
    }

    private Marshaller createMarshaller() throws JAXBException {
        Marshaller stopPlaceMarshaller = publicationDeliveryContext.createMarshaller();
        stopPlaceMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        stopPlaceMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        stopPlaceMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "");
        return stopPlaceMarshaller;
    }
}

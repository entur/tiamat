package org.rutebanken.tiamat.exporter;

import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.exporter.params.StopPlaceSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.Iterator;

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
    private static final JAXBContext stopPlaceContext = createContext(org.rutebanken.netex.model.StopPlace.class);
    private static final ObjectFactory netexObjectFactory = new ObjectFactory();

    private final StopPlaceRepository stopPlaceRepository;
    private final ParkingRepository parkingRepository;

    private final NetexMapper netexMapper;

    @Autowired
    public StreamingPublicationDelivery(StopPlaceRepository stopPlaceRepository, ParkingRepository parkingRepository, NetexMapper netexMapper) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.parkingRepository = parkingRepository;
        this.netexMapper = netexMapper;
    }

    public static JAXBContext createContext(Class clazz) {
        try {
            return newInstance(clazz);
        } catch (JAXBException e) {
            logger.warn("Could not create instance of jaxb context for class " + clazz, e);
            throw new RuntimeException(e);
        }
    }

    public String writePublicationDeliverySkeletonToString(PublicationDeliveryStructure publicationDeliveryStructure) throws JAXBException {
        JAXBElement<PublicationDeliveryStructure> jaxPublicationDelivery = netexObjectFactory.createPublicationDelivery(publicationDeliveryStructure);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        Marshaller publicationDeliveryMarshaller = publicationDeliveryContext.createMarshaller();

        publicationDeliveryMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        publicationDeliveryMarshaller.marshal(jaxPublicationDelivery, byteArrayOutputStream);
        return byteArrayOutputStream.toString();
    }

    public void stream(PublicationDeliveryStructure publicationDeliveryStructure, ExportParams exportParams, OutputStream outputStream) throws JAXBException, XMLStreamException, IOException, InterruptedException {
        String publicationDeliveryStructureXml = writePublicationDeliverySkeletonToString(publicationDeliveryStructure);
        stream(publicationDeliveryStructureXml, stopPlaceRepository.scrollStopPlaces(exportParams), parkingRepository.scrollParkings(), outputStream);
    }

    public Marshaller createStopPlaceMarshaller() throws JAXBException {
        Marshaller stopPlaceMarshaller = stopPlaceContext.createMarshaller();
        stopPlaceMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        stopPlaceMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        stopPlaceMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "");
        return stopPlaceMarshaller;
    }


    /**
     * In order to not hold all stop places in memory at once, we need to marshal stop places from a queue.
     * Requires a publication delivery xml that contains newlines.
     */
    public void stream(String publicationDeliveryStructureXml, Iterator<StopPlace> stopPlaceIterator, Iterator<Parking> parkingIterator, OutputStream outputStream) throws JAXBException, XMLStreamException, IOException, InterruptedException {

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

        try {
            Marshaller stopPlaceMarshaller = createStopPlaceMarshaller();

            String lineSeparator = System.getProperty("line.separator");
            String[] publicationDeliveryLines = publicationDeliveryStructureXml.split(lineSeparator);

            for (int index = 0; index < publicationDeliveryLines.length; index++) {
                String publicationDeliveryLine = publicationDeliveryLines[index];
                logger.debug("Line: {}", publicationDeliveryLine);

                if (publicationDeliveryLine.contains("<SiteFrame")) {
                    if (publicationDeliveryLine.contains("/>")) {

                        // Handle empty site frame
                        String modifiedLine = publicationDeliveryLine.replace("/>", ">");

                        bufferedWriter.write(modifiedLine);
                        bufferedWriter.write(lineSeparator);

                        marshalStops(stopPlaceIterator, bufferedWriter, stopPlaceMarshaller, lineSeparator);
                        marshalParkings(parkingIterator, bufferedWriter, stopPlaceMarshaller, lineSeparator);

                        bufferedWriter.write("</SiteFrame>");
                        bufferedWriter.write(lineSeparator);

                    } else {
                        bufferedWriter.write(publicationDeliveryLine);
                        bufferedWriter.write(lineSeparator);
                    }
                    continue;
                }
                if (publicationDeliveryLine.contains("</SiteFrame>")) {
                    // Marshal stops after other nodes, such as topographic places
                    marshalStops(stopPlaceIterator, bufferedWriter, stopPlaceMarshaller, lineSeparator);
                    marshalParkings(parkingIterator, bufferedWriter, stopPlaceMarshaller, lineSeparator);
                }
                bufferedWriter.write(publicationDeliveryLine);
                bufferedWriter.write(lineSeparator);
            }
        } finally {
            bufferedWriter.flush();
        }
    }

    public void marshalStops(Iterator<StopPlace> iterableStopPlaces,
                             BufferedWriter bufferedWriter,
                             Marshaller stopPlaceMarshaller,
                             String lineSeparator) throws InterruptedException, JAXBException, IOException {
        logger.info("Marshalling stops");

        int count = 0;

        long startTime = System.currentTimeMillis();

        while (iterableStopPlaces.hasNext()) {
            StopPlace stopPlace = iterableStopPlaces.next();

            if(count == 0) {
                bufferedWriter.write("<stopPlaces>");
                bufferedWriter.write(lineSeparator);
            }

            ++count;

            if(count % 1000 == 0 && logger.isInfoEnabled()) {
                String stopPlacesPerSecond = "NA";

                long duration = System.currentTimeMillis() - startTime;
                if(duration >= 1000) {
                    stopPlacesPerSecond = String.valueOf(count / (duration / 1000f));
                }
                logger.info("Stop places marshalled: {}. Stop places per second: {}", count, stopPlacesPerSecond);
            } else {
                logger.debug("Marshalling stop place {}: {}", count, stopPlace);
            }

            org.rutebanken.netex.model.StopPlace netexStopPlace = netexMapper.mapToNetexModel(stopPlace);
            JAXBElement<org.rutebanken.netex.model.StopPlace> jaxBStopPlace = netexObjectFactory.createStopPlace(netexStopPlace);
            stopPlaceMarshaller.marshal(jaxBStopPlace, bufferedWriter);
            bufferedWriter.write(lineSeparator);
        }
        if(count > 0) {
            bufferedWriter.write("</stopPlaces>");
            bufferedWriter.write(lineSeparator);
        }
    }

    public void marshalParkings(Iterator<Parking> iterableParkings,
                             BufferedWriter bufferedWriter,
                             Marshaller marshaller,
                             String lineSeparator) throws InterruptedException, JAXBException, IOException {
        logger.info("Marshalling parkings");

        int count = 0;

        long startTime = System.currentTimeMillis();

        while (iterableParkings.hasNext()) {
            Parking parking  = iterableParkings.next();

            if(count == 0) {
                bufferedWriter.write("<parkings>");
                bufferedWriter.write(lineSeparator);
            }

            ++count;

            if(count % 1000 == 0 && logger.isInfoEnabled()) {
                String parkingsPerSecond = "NA";

                long duration = System.currentTimeMillis() - startTime;
                if(duration >= 1000) {
                    parkingsPerSecond = String.valueOf(count / (duration / 1000f));
                }
                logger.info("Parkings marshalled: {}. Parkings per second: {}", count, parkingsPerSecond);
            } else {
                logger.debug("Marshalling parking {}: {}", count, parking);
            }

            org.rutebanken.netex.model.Parking netexParking = netexMapper.mapToNetexModel(parking);
            JAXBElement<org.rutebanken.netex.model.Parking> jaxBParking = netexObjectFactory.createParking(netexParking);
            marshaller.marshal(jaxBParking, bufferedWriter);
            bufferedWriter.write(lineSeparator);
        }
        if(count > 0) {
            bufferedWriter.write("</parkings>");
            bufferedWriter.write(lineSeparator);
        }
    }
}

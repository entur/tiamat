package org.rutebanken.tiamat.exporter;

import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
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
    private static final JAXBContext jaxbContext = createContext(org.rutebanken.netex.model.StopPlace.class);
    private static final ObjectFactory netexObjectFactory = new ObjectFactory();

    private final StopPlaceRepository stopPlaceRepository;
    private final ParkingRepository parkingRepository;
    private final PublicationDeliveryExporter publicationDeliveryExporter;
    private final TiamatSiteFrameExporter tiamatSiteFrameExporter;

    private final IterableMarshaller iterableMarshaller;

    private final NetexMapper netexMapper;

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Autowired
    public StreamingPublicationDelivery(StopPlaceRepository stopPlaceRepository, ParkingRepository parkingRepository, PublicationDeliveryExporter publicationDeliveryExporter, TiamatSiteFrameExporter tiamatSiteFrameExporter, IterableMarshaller iterableMarshaller, NetexMapper netexMapper) {
        this.stopPlaceRepository = stopPlaceRepository;
        this.parkingRepository = parkingRepository;
        this.publicationDeliveryExporter = publicationDeliveryExporter;
        this.tiamatSiteFrameExporter = tiamatSiteFrameExporter;
        this.iterableMarshaller = iterableMarshaller;
        this.netexMapper = netexMapper;

    }

    public String writePublicationDeliverySkeletonToString(PublicationDeliveryStructure publicationDeliveryStructure) throws JAXBException {
        JAXBElement<PublicationDeliveryStructure> jaxPublicationDelivery = netexObjectFactory.createPublicationDelivery(publicationDeliveryStructure);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        Marshaller publicationDeliveryMarshaller = publicationDeliveryContext.createMarshaller();

        publicationDeliveryMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        publicationDeliveryMarshaller.marshal(jaxPublicationDelivery, byteArrayOutputStream);
        return byteArrayOutputStream.toString();
    }

    public void stream(ExportParams exportParams, OutputStream outputStream) throws JAXBException, XMLStreamException, IOException, InterruptedException {
        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryExporter.exportPublicationDeliveryWithoutStops();
        String publicationDeliveryStructureXml = writePublicationDeliverySkeletonToString(publicationDeliveryStructure);
        stream(publicationDeliveryStructureXml, stopPlaceRepository.scrollStopPlaces(exportParams), parkingRepository.scrollParkings(), outputStream);
    }


    /**
     * In order to not hold all stop places in memory at once, we need to marshal stop places and parkings from queues.
     * Requires a publication delivery xml that contains newlines.
     * Should be possible to stream without re-reading
     */
    public void stream(String publicationDeliveryStructureXml, Iterator<StopPlace> stopPlaceIterator, Iterator<Parking> parkingIterator, OutputStream outputStream) throws JAXBException, XMLStreamException, IOException, InterruptedException {

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

        try {
            Marshaller marshaller = createMarshaller();

            String[] publicationDeliveryLines = publicationDeliveryStructureXml.split(LINE_SEPARATOR);

            for (int index = 0; index < publicationDeliveryLines.length; index++) {
                String publicationDeliveryLine = publicationDeliveryLines[index];
                logger.debug("Line: {}", publicationDeliveryLine);

                if (publicationDeliveryLine.contains("<SiteFrame")) {
                    if (publicationDeliveryLine.contains("/>")) {

                        // Handle empty site frame
                        String modifiedLine = publicationDeliveryLine.replace("/>", ">");

                        bufferedWriter.write(modifiedLine);
                        bufferedWriter.write(LINE_SEPARATOR);

                        marshalIterableTypes(stopPlaceIterator, parkingIterator, bufferedWriter, marshaller);

                        bufferedWriter.write("</SiteFrame>");
                        bufferedWriter.write(LINE_SEPARATOR);

                    } else {
                        bufferedWriter.write(publicationDeliveryLine);
                        bufferedWriter.write(LINE_SEPARATOR);
                    }
                    continue;
                }
                if (publicationDeliveryLine.contains("</SiteFrame>")) {
                    // Marshal stops after other nodes, such as topographic places
                    marshalIterableTypes(stopPlaceIterator, parkingIterator, bufferedWriter, marshaller);
                }
                bufferedWriter.write(publicationDeliveryLine);
                bufferedWriter.write(LINE_SEPARATOR);
            }
        } finally {
            bufferedWriter.flush();
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

    private void marshalIterableTypes(Iterator<StopPlace> stopPlaceIterator, Iterator<Parking> parkingIterator, BufferedWriter bufferedWriter, Marshaller marshaller) throws IOException, JAXBException {
        iterableMarshaller.marshal(stopPlaceIterator, bufferedWriter, marshaller, org.rutebanken.netex.model.StopPlace.class, "stopPlaces", netexObjectFactory::createStopPlace);
        iterableMarshaller.marshal(parkingIterator, bufferedWriter, marshaller, org.rutebanken.netex.model.Parking.class, "parkings", netexObjectFactory::createParking);
    }

    private Marshaller createMarshaller() throws JAXBException {
        Marshaller stopPlaceMarshaller = jaxbContext.createMarshaller();
        stopPlaceMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        stopPlaceMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        stopPlaceMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "");
        return stopPlaceMarshaller;
    }
}

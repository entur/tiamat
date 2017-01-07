package org.rutebanken.tiamat.exporters;

import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.StopPlaceRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.concurrent.BlockingQueue;

import static javax.xml.bind.JAXBContext.newInstance;

/**
 * Stream data objects inside already serialized publication delivery.
 * To be able to export many stop places wihtout keeping them all in memory.
 */
@Component
public class StreamingPublicationDelivery {

    private static final Logger logger = LoggerFactory.getLogger(StreamingPublicationDelivery.class);

    private static final JAXBContext publicationDeliveryContext = createContext(PublicationDeliveryStructure.class);
    private static final JAXBContext stopPlaceContext = createContext(org.rutebanken.netex.model.StopPlace.class);
    private static final ObjectFactory netexObjectFactory = new ObjectFactory();

    private final NetexMapper netexMapper;

    @Autowired
    public StreamingPublicationDelivery(NetexMapper netexMapper) {
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

    public void stream(PublicationDeliveryStructure publicationDeliveryStructure, BlockingQueue<org.rutebanken.tiamat.model.StopPlace> stopPlacesQueue, OutputStream outputStream) throws JAXBException, XMLStreamException, IOException, InterruptedException {
        String publicationDeliveryStructureXml = writePublicationDeliverySkeletonToString(publicationDeliveryStructure);
        stream(publicationDeliveryStructureXml, stopPlacesQueue, outputStream);
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
    public void stream(String publicationDeliveryStructureXml, BlockingQueue<org.rutebanken.tiamat.model.StopPlace> stopPlacesQueue, OutputStream outputStream) throws JAXBException, XMLStreamException, IOException, InterruptedException {

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

                        marshalStops(stopPlacesQueue, bufferedWriter, stopPlaceMarshaller, lineSeparator);

                        bufferedWriter.write("</SiteFrame>");
                        bufferedWriter.write(lineSeparator);

                    } else {
                        bufferedWriter.write(publicationDeliveryLine);
                        bufferedWriter.write(lineSeparator);
                    }
                    continue;
                }
                if (publicationDeliveryLine.contains("</SiteFrame>")) {
                    // Marhsal stops after other nodes, such as topographic places
                    marshalStops(stopPlacesQueue, bufferedWriter, stopPlaceMarshaller, lineSeparator);
                }
                bufferedWriter.write(publicationDeliveryLine);
                bufferedWriter.write(lineSeparator);
            }
        } finally {
            bufferedWriter.flush();
        }
    }

    public void marshalStops(BlockingQueue<org.rutebanken.tiamat.model.StopPlace> stopPlacesQueue,
                             BufferedWriter bufferedWriter,
                             Marshaller stopPlaceMarshaller,
                             String lineSeparator) throws InterruptedException, JAXBException, IOException {
        logger.info("Marshaling stops");

        int count = 0;
        while (true) {
            org.rutebanken.tiamat.model.StopPlace stopPlace = stopPlacesQueue.take();

            if (stopPlace.getId().equals(StopPlaceRepositoryImpl.POISON_PILL.getId())) {
                logger.info("Got poison pill from stop place queue. Finished marshaling {} stop places.", count);
                break;
            }

            if(count == 0) {
                bufferedWriter.write("<stopPlaces>");
                bufferedWriter.write(lineSeparator);
            }

            ++count;
            logger.debug("Marshalling stop place {}: {}", count, stopPlace);
            StopPlace netexStopPlace = netexMapper.mapToNetexModel(stopPlace);
            JAXBElement<StopPlace> jaxBStopPlace = netexObjectFactory.createStopPlace(netexStopPlace);
            stopPlaceMarshaller.marshal(jaxBStopPlace, bufferedWriter);
            bufferedWriter.write(lineSeparator);
        }
        if(count > 0) {
            bufferedWriter.write("</stopPlaces>");
            bufferedWriter.write(lineSeparator);
        }
    }

//
//    public void nonamespacewriter() throws XMLStreamException {
//
//        ByteArrayOutputStream stopPlaceOutputStream = new ByteArrayOutputStream();
//        XMLStreamWriter writer = new XMLOutputFactoryImpl().createXMLStreamWriter(stopPlaceOutputStream);
//        writer.setNamespaceContext(new NamespaceContext() {
//            public Iterator getPrefixes(String namespaceURI) {
//                return null;
//            }
//
//            public String getPrefix(String namespaceURI) {
//                return "";
//            }
//
//            public String getNamespaceURI(String prefix) {
//                return null;
//            }
//        });
//        writer.setDefaultNamespace("");
//
//    }

}

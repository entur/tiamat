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
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

import static javax.xml.bind.JAXBContext.newInstance;

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
            logger.warn("Could not create instance of jaxb context for class "+ clazz, e);
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
     */
    public void stream(String publicationDeliveryStructureXml, BlockingQueue<org.rutebanken.tiamat.model.StopPlace> stopPlacesQueue, OutputStream outputStream) throws JAXBException, XMLStreamException, IOException, InterruptedException {

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);

        Marshaller stopPlaceMarshaller = createStopPlaceMarshaller();

        String lineSeparator = System.getProperty("line.separator");
        String[] lines = publicationDeliveryStructureXml.split(lineSeparator);

        for(String line : lines) {
            logger.debug("Line: {}", line);
            boolean addClosingSiteFrameTag = false;

            if(line.contains("<SiteFrame")) {
                if(line.contains("/>")) {

                    String modifiedLine = line.replace("/>", ">");

                    bufferedWriter.write(modifiedLine);
                    bufferedWriter.write(lineSeparator);

                    addClosingSiteFrameTag = true;
                }
                else {
                    bufferedWriter.write(line);
                    bufferedWriter.write(lineSeparator);
                }

                bufferedWriter.write("<stopPlaces>");
                bufferedWriter.write(lineSeparator);

                logger.info("Marshalling stops");
                marshalStops(stopPlacesQueue, bufferedWriter, stopPlaceMarshaller, lineSeparator);

                bufferedWriter.write("</stopPlaces>");
                bufferedWriter.write(lineSeparator);

                if(addClosingSiteFrameTag) {
                    bufferedWriter.write("</SiteFrame>");
                    bufferedWriter.write(lineSeparator);
                } else {
                    bufferedWriter.write(line);
                    bufferedWriter.write(lineSeparator);
                }
            } else {
                bufferedWriter.write(line);
                bufferedWriter.write(lineSeparator);
            }
        }
    }

    public void marshalStops(BlockingQueue<org.rutebanken.tiamat.model.StopPlace> stopPlacesQueue,
                              BufferedWriter bufferedWriter,
                              Marshaller stopPlaceMarshaller,
                              String lineSeparator) throws InterruptedException, JAXBException, IOException {

        while(true) {
            org.rutebanken.tiamat.model.StopPlace stopPlace = stopPlacesQueue.take();

            if(stopPlace.getId().equals(StopPlaceRepositoryImpl.POISON_PILL.getId())) {
                logger.info("Got poison pill from stop place queue. Finished marshalling stop places.");
                break;
            }

            logger.debug("Marshalling stop place {}", stopPlace);
            StopPlace netexStopPlace = netexMapper.mapToNetexModel(stopPlace);
            JAXBElement<StopPlace> jaxBStopPlace = netexObjectFactory.createStopPlace(netexStopPlace);
            stopPlaceMarshaller.marshal(jaxBStopPlace, bufferedWriter);
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

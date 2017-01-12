package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import com.google.common.base.MoreObjects;
import com.sun.xml.internal.stream.events.StartElementEvent;
import org.rutebanken.netex.model.NavigationPath;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.netex.validation.NeTExValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.io.ByteStreams.toByteArray;
import static javax.xml.bind.JAXBContext.newInstance;

@Component
public class PublicationDeliveryPartialUnmarshaller {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryPartialUnmarshaller.class);

    private static final JAXBContext jaxbContext = getContext(new Class[]{PublicationDeliveryStructure.class, StopPlace.class, TopographicPlace.class});

    private static JAXBContext getContext(Class[] classes) {
        try {
            return newInstance(classes);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private final NeTExValidator neTExValidator;

    @Value("${publicationDeliveryUnmarshaller.validateAgainstSchema:true}")
    private boolean validateAgainstSchema;

    public PublicationDeliveryPartialUnmarshaller() throws IOException, SAXException {
        this.neTExValidator = new NeTExValidator();
    }

    public UnmarshalResult unmarshal(InputStream inputStream) throws JAXBException, IOException, SAXException, XMLStreamException, InterruptedException, ParserConfigurationException {
        File file = File.createTempFile("tiamat-" + System.currentTimeMillis(), ".xml");
        Files.copy(inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        if (logger.isDebugEnabled()) {
            String xml = new String(toByteArray(inputStream));
            logger.debug("Debug is enabled. Will log the input (this kills performance.):\n{}", xml);
            inputStream = new ByteArrayInputStream(xml.getBytes());
            logger.debug("Valdiation enabled? {}", validateAgainstSchema);
        }

        if (validateAgainstSchema) {
            unmarshaller.setSchema(neTExValidator.getSchema());
        }

        XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();

        logger.debug("Unmarshalling incoming publication delivery structure. Schema validation enabled: {}", validateAgainstSchema);

        PublicationDeliveryStructure publicationDeliveryStructure = readPublicationDeliveryStructure(xmlInputFactory, new FileInputStream(file), unmarshaller);

        // Read the rest from the same file
        UnmarshalResult unmarshalResult = readWithXmlEventReaderAsync(xmlInputFactory, new FileInputStream(file), unmarshaller);
        unmarshalResult.setPublicationDeliveryStructure(publicationDeliveryStructure);

        logger.debug("Done unmarshalling incoming publication delivery structure with schema validation enabled: {}", validateAgainstSchema);
        return unmarshalResult;
    }


    /**
     * Unmarshal publication delivery structure without stop places, topographic places and navigation paths.
     */
    private PublicationDeliveryStructure readPublicationDeliveryStructure(XMLInputFactory xmlInputFactory, InputStream inputStream, Unmarshaller unmarshaller) throws FileNotFoundException, XMLStreamException, JAXBException {

        EventFilter eventFilter = new EventFilter() {
            @Override
            public boolean accept(XMLEvent event) {
                if (event.isStartElement()) {
                    StartElementEvent startElementEvent = (StartElementEvent) event;
                    String localPartOfName = startElementEvent.getName().getLocalPart();

                    if (localPartOfName.equals("StopPlace")) {
                        logger.info("Ignore stop place");
                        return false;
                    } else if (localPartOfName.equals("TopographicPlace")) {
                        logger.info("Ignore topographic place");
                        return false;
                    } else if (localPartOfName.equals("NavigationPath")) {
                        logger.info("Ingore navigation path");
                        return false;
                    }
                }

                return true;
            }
        };

        XMLEventReader xmlEventReader = xmlInputFactory.createFilteredReader(xmlInputFactory.createXMLEventReader(inputStream), eventFilter);
        PublicationDeliveryStructure publicationDeliveryStructure = unmarshaller.unmarshal(xmlEventReader, PublicationDeliveryStructure.class).getValue();

        xmlEventReader.close();
        return publicationDeliveryStructure;
    }

    public static final StopPlace POISON_STOP_PLACE = new StopPlace().withId("-100");
    public static final TopographicPlace POISON_TOPOGRAPHIC_PLACE = new TopographicPlace().withId("-101");
    public static final NavigationPath POISON_NAVIGATION_PATH = new NavigationPath().withId("-102");

    public UnmarshalResult readWithXmlEventReaderAsync(XMLInputFactory xmlInputFactory, InputStream inputStream, Unmarshaller unmarshaller) throws XMLStreamException, JAXBException, InterruptedException, IOException {

        UnmarshalResult unmarshalResult = new UnmarshalResult(100);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                AtomicInteger stops = new AtomicInteger();
                AtomicInteger topographicPlaces = new AtomicInteger();
                AtomicInteger navgiationPaths = new AtomicInteger();

                final XMLEventReader xmlEventReader;
                try {
                    xmlEventReader = xmlInputFactory.createXMLEventReader(inputStream);

                    XMLEvent xmlEvent = null;

//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        OutputStreamWriter writer = new OutputStreamWriter(byteArrayOutputStream);

                    while ((xmlEvent = xmlEventReader.peek()) != null)

                    {
                        if (xmlEvent.isStartElement()) {
                            StartElementEvent startElementEvent = (StartElementEvent) xmlEvent;

                            String localPartOfName = startElementEvent.getName().getLocalPart();

                            if (localPartOfName.equals("StopPlace")) {
                                StopPlace stopPlace = unmarshaller.unmarshal(xmlEventReader, StopPlace.class).getValue();
                                stops.incrementAndGet();
                                unmarshalResult.getStopPlaceQueue().put(stopPlace);
                            } else if (localPartOfName.equals("TopographicPlace")) {
                                TopographicPlace topographicPlace = unmarshaller.unmarshal(xmlEventReader, TopographicPlace.class).getValue();
                                topographicPlaces.incrementAndGet();
                                unmarshalResult.getTopographicPlaceQueue().put(topographicPlace);
                            } else if (localPartOfName.equals("NavigationPath")) {
                                NavigationPath navigationPath = unmarshaller.unmarshal(xmlEventReader, NavigationPath.class).getValue();
                                navgiationPaths.incrementAndGet();
                                unmarshalResult.getNavigationPathsQueue().put(navigationPath);
                            } else {
//                    writeStartElement(startElementEvent, writer);
                            }
                        } else if (xmlEvent.isEndElement()) {
                            EndElement endElement = xmlEvent.asEndElement();
                            String localPartOfName = endElement.getName().getLocalPart();
                            if (localPartOfName.equals("stopPlaces")) {
                                unmarshalResult.getStopPlaceQueue().put(POISON_STOP_PLACE);
                            } else if (localPartOfName.equals("topographicPlaces")) {
                                unmarshalResult.getTopographicPlaceQueue().put(POISON_TOPOGRAPHIC_PLACE);
                            } else if (localPartOfName.equals("navigationPaths")) {
                                unmarshalResult.getNavigationPathsQueue().put(POISON_NAVIGATION_PATH);
                            }
//                else {
//                    writeStartElement(startElementEvent, writer);
//                }
                        }
//            else if(xmlEvent.isCharacters()) {
//                writeCharacters(xmlEvent.asCharacters(), writer);

                        xmlEventReader.next();
                    }
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JAXBException e) {
                    e.printStackTrace();
                }
                logger.info("Unmarshalling thread finished after {} stops, {} topographic places and {} navigation paths", stops.get(), topographicPlaces.get(), navgiationPaths.get());
            }
        });

        thread.setName("unmarshalling-thread");
        logger.info("Starting unmarshalling thread ", thread);
        thread.start();


//        writer.flush();
//        logger.info("Got this xml: {}", byteArrayOutputStream.toString());
//
//        InputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
//
//        JAXBElement<PublicationDeliveryStructure> jaxbElement =
//                (JAXBElement<org.rutebanken.netex.model.PublicationDeliveryStructure>) unmarshaller.unmarshal(byteArrayInputStream);
//        PublicationDeliveryStructure publicationDeliveryStructure = jaxbElement.getValue();
//
        return unmarshalResult;
    }

    public void writeCharacters(Characters characters, Writer writer) throws IOException {
        writer.write(characters.getData());
    }

    public void writeStartElement(StartElement startElement, Writer writer) throws IOException {
        writer.write('<');
        writer.write(startElement.getName().getLocalPart());

        Iterator attributes = startElement.getAttributes();

        if (attributes.hasNext()) {
            writer.write(' ');
        }
        while (attributes.hasNext()) {
            Attribute attribute = (Attribute) attributes.next();
            writer.write(attribute.toString());
            if (attributes.hasNext()) {
                writer.write(' ');
            }
        }

        Iterator namespaces = startElement.getNamespaces();
        if (namespaces.hasNext()) {
            writer.write(' ');
        }
        while (namespaces.hasNext()) {
            Namespace namespace = (Namespace) namespaces.next();
            logger.info("Found name {}", namespace);
            if (namespace.getName().getLocalPart().isEmpty()) {
                writer.write(namespace.getName().getPrefix());
                writer.write('=');
                writer.write('\'');
                writer.write(namespace.getNamespaceURI());
                writer.write('\'');
            } else {
                writer.write(namespace.toString());
            }

            if (namespaces.hasNext()) {
                writer.write(' ');
            }
        }
        writer.write('>');
    }

    public void writeEndElement(EndElement endElement, Writer writer) throws IOException {
        writer.write("</");
        writer.write(endElement.getName().getLocalPart());
        writer.write(">");
    }

    public class UnmarshalResult {

        private final BlockingQueue<StopPlace> stopPlaceQueue;
        private final BlockingQueue<TopographicPlace> topographicPlaceQueue;
        private final BlockingQueue<NavigationPath> navigationPathsQueue;

        private PublicationDeliveryStructure publicationDeliveryStructure;

        public UnmarshalResult(int size) {

            stopPlaceQueue = new ArrayBlockingQueue<>(size);
            topographicPlaceQueue = new ArrayBlockingQueue<>(size);
            navigationPathsQueue = new ArrayBlockingQueue<>(size);
        }


        public BlockingQueue<StopPlace> getStopPlaceQueue() {
            return stopPlaceQueue;
        }

        public BlockingQueue<TopographicPlace> getTopographicPlaceQueue() {
            return topographicPlaceQueue;
        }

        public BlockingQueue<NavigationPath> getNavigationPathsQueue() {
            return navigationPathsQueue;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("publicationDelivery", publicationDeliveryStructure)
                    .add("stopPlaceQueue", stopPlaceQueue.size())
                    .add("topographicPlaceQueue", topographicPlaceQueue.size())
                    .add("navigationPathsQueue", navigationPathsQueue.size())
                    .toString();
        }

        public PublicationDeliveryStructure getPublicationDeliveryStructure() {
            return publicationDeliveryStructure;
        }

        public void setPublicationDeliveryStructure(PublicationDeliveryStructure publicationDeliveryStructure) {
            this.publicationDeliveryStructure = publicationDeliveryStructure;
        }
    }

}

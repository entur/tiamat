package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import com.google.common.base.MoreObjects;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

        EventFilter eventFilter = new TypesEventFilter("stopPlaces", "navigationPaths");

        XMLEventReader xmlEventReader = xmlInputFactory.createFilteredReader(xmlInputFactory.createXMLEventReader(inputStream), eventFilter);
        PublicationDeliveryStructure publicationDeliveryStructure = unmarshaller.unmarshal(xmlEventReader, PublicationDeliveryStructure.class).getValue();

        xmlEventReader.close();
        return publicationDeliveryStructure;
    }

    public class TypesEventFilter implements EventFilter {
        private boolean deleteSection = false;
        private final List<String> ignoreTypes;

        public TypesEventFilter(String ...ignoreTypes) {
            this.ignoreTypes = new ArrayList<>(ignoreTypes.length);
            for(String ignoreType : ignoreTypes) {
                this.ignoreTypes.add(ignoreType);
            }
        }

        @Override
        public boolean accept(XMLEvent event) {
            if(event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                if(doesMatch(endElement.getName().getLocalPart())) {
                    deleteSection = false;
                    logger.trace("NO ACCEPT. Release delete section: {}", event);
                    return false;
                }
            }

            if(deleteSection) {
                logger.trace("NO ACCEPT. Deleting section: {}", event);
                return false;
            }

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                String localPartOfName = startElement.getName().getLocalPart();

                if(doesMatch(localPartOfName)) {
                    deleteSection = true;
                    return false;
                }
            }

            logger.trace("Accept: {}", event);
            return true;
        }

        private boolean doesMatch(String localPartOfName) {
            return ignoreTypes.contains(localPartOfName);
        }
    }

    public static final StopPlace POISON_STOP_PLACE = new StopPlace().withId("-100");

    public UnmarshalResult readWithXmlEventReaderAsync(XMLInputFactory xmlInputFactory, InputStream inputStream, Unmarshaller unmarshaller) throws XMLStreamException, JAXBException, InterruptedException, IOException {

        UnmarshalResult unmarshalResult = new UnmarshalResult(100);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                AtomicInteger stops = new AtomicInteger();

                final XMLEventReader xmlEventReader;
                try {
                    xmlEventReader = xmlInputFactory.createXMLEventReader(inputStream);

                    XMLEvent xmlEvent = null;

                    while ((xmlEvent = xmlEventReader.peek()) != null)

                    {
                        if (xmlEvent.isStartElement()) {
                            StartElement startElement = xmlEvent.asStartElement();
                            String localPartOfName = startElement.getName().getLocalPart();

                            if (localPartOfName.equals("StopPlace")) {
                                StopPlace stopPlace = unmarshaller.unmarshal(xmlEventReader, StopPlace.class).getValue();
                                stops.incrementAndGet();
                                unmarshalResult.getStopPlaceQueue().put(stopPlace);
                            }
                        } else if (xmlEvent.isEndElement()) {
                            EndElement endElement = xmlEvent.asEndElement();
                            String localPartOfName = endElement.getName().getLocalPart();
                            if (localPartOfName.equals("stopPlaces")) {
                                unmarshalResult.getStopPlaceQueue().put(POISON_STOP_PLACE);
                            }
                        }
                        xmlEventReader.next();
                    }
                } catch (XMLStreamException|InterruptedException |JAXBException e) {

                    logger.error("Could not read netex from events. " + e.getMessage(), e);
                }
                logger.info("Unmarshalling thread finished after {} stops", stops.get());
            }
        });

        thread.setName("unmarshalling-thread");
        logger.info("Starting unmarshalling thread ", thread);
        thread.start();
        return unmarshalResult;
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

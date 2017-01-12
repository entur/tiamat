package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import com.google.common.base.MoreObjects;
import com.sun.xml.internal.stream.XMLEventReaderImpl;
import com.sun.xml.internal.stream.events.EndElementEvent;
import com.sun.xml.internal.stream.events.StartElementEvent;
import org.rutebanken.netex.model.*;
import org.rutebanken.netex.validation.NeTExValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Attr;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.google.common.io.ByteStreams.toByteArray;
import static javax.xml.bind.JAXBContext.newInstance;

@Component
public class PublicationDeliveryPartialUnmarshaller {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryPartialUnmarshaller.class);

    private static final JAXBContext jaxbContext = getContext(new Class[] {PublicationDeliveryStructure.class, StopPlace.class, TopographicPlace.class});

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

    public PublicationDeliveryStructure unmarshal(InputStream inputStream) throws JAXBException, IOException, SAXException, XMLStreamException, InterruptedException, ParserConfigurationException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        if(logger.isDebugEnabled()) {
            String xml = new String(toByteArray(inputStream));
            logger.debug("Debug is enabled. Will log the input (this kills performance.):\n{}", xml);
            inputStream = new ByteArrayInputStream(xml.getBytes());
            logger.debug("Valdiation enabled? {}", validateAgainstSchema);
        }

        if(validateAgainstSchema) {
            unmarshaller.setSchema(neTExValidator.getSchema());
        }
        ResultQueues resultQueues = new ResultQueues(1);

        logger.debug("Unmarshalling incoming publication delivery structure. Schema validation enabled: {}", validateAgainstSchema);
        XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
        readWithXmlEventReader(xmlInputFactory, inputStream, unmarshaller, resultQueues);
        logger.debug("Done unmarshalling incoming publication delivery structure with schema validation enabled: {}", validateAgainstSchema);
        return null;
    }

    public PublicationDeliveryStructure readWithXmlEventReader(XMLInputFactory xmlInputFactory, InputStream inputStream, Unmarshaller unmarshaller, ResultQueues resultQueues) throws XMLStreamException, JAXBException, InterruptedException, IOException {

        final XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(inputStream);

        XMLEvent xmlEvent = null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(byteArrayOutputStream);

        while ((xmlEvent = xmlEventReader.peek()) != null) {
            if(xmlEvent.isStartElement()) {
                StartElementEvent startElementEvent = (StartElementEvent) xmlEvent;

                String localPartOfName = startElementEvent.getName().getLocalPart();

                if(localPartOfName.equals("StopPlace")) {
                    logger.debug("xmlEvent: {}", xmlEvent);
                    StopPlace stopPlace = unmarshaller.unmarshal(xmlEventReader, StopPlace.class).getValue();
                    resultQueues.getStopPlaceQueue().put(stopPlace);
                } else if(localPartOfName.equals("TopographicPlace")) {
                    TopographicPlace topographicPlace = unmarshaller.unmarshal(xmlEventReader, TopographicPlace.class).getValue();
                    resultQueues.getTopographicPlaceQueue().put(topographicPlace);
                } else if(localPartOfName.equals("NavigationPath")) {
                    NavigationPath navigationPath = unmarshaller.unmarshal(xmlEventReader, NavigationPath.class).getValue();
                    resultQueues.getNavigationPathsQueue().put(navigationPath);
                } else {
                    writeStartElement(startElementEvent, writer);
                }
            } else if(xmlEvent.isEndElement()) {
                writeEndElement(xmlEvent.asEndElement(), writer);
            } else if(xmlEvent.isCharacters()) {
                writeCharacters(xmlEvent.asCharacters(), writer);
            }
            xmlEventReader.next();
        }

        writer.flush();
        logger.info("Got this xml: {}", byteArrayOutputStream.toString());

        InputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        JAXBElement<PublicationDeliveryStructure> jaxbElement =
                (JAXBElement<org.rutebanken.netex.model.PublicationDeliveryStructure>) unmarshaller.unmarshal(byteArrayInputStream);
        PublicationDeliveryStructure publicationDeliveryStructure = jaxbElement.getValue();

        logger.info("{}", resultQueues);
        return publicationDeliveryStructure;

    }

    public void writeCharacters(Characters characters, Writer writer) throws IOException {
        writer.write(characters.getData());
    }

    public void writeStartElement(StartElement startElement, Writer writer) throws IOException {
        writer.write('<');
        writer.write(startElement.getName().getLocalPart());

        Iterator attributes = startElement.getAttributes();

        if(attributes.hasNext()) {
            writer.write(' ');
        }
        while (attributes.hasNext()) {
            Attribute attribute = (Attribute) attributes.next();
            writer.write(attribute.toString());
            if(attributes.hasNext()) {
                writer.write(' ');
            }
        }

        Iterator namespaces = startElement.getNamespaces();
        if(namespaces.hasNext()) {
            writer.write(' ');
        }
        while (namespaces.hasNext()) {
            Namespace namespace = (Namespace) namespaces.next();
            logger.info("Found name {}", namespace);
            if(namespace.getName().getLocalPart().isEmpty()) {
                writer.write(namespace.getName().getPrefix());
                writer.write('=');
                writer.write('\'');
                writer.write(namespace.getNamespaceURI());
                writer.write('\'');
            } else {
                writer.write(namespace.toString());
            }

            if(namespaces.hasNext()) {
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

    public class IngoreTypesFilter extends XMLFilterImpl {

        public IngoreTypesFilter(XMLReader arg0) {
            super(arg0);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

            if(localName.equals(StopPlace.class.getSimpleName()) || localName.equals(TopographicPlace.class.getSimpleName()) || localName.equals(PathLink.class.getSimpleName())) {
                logger.info("Detected one of stopplace, topographic place or pathlink");
            } else {
                super.startElement(uri, localName, qName, attributes);
            }

        }
    }

    public class ResultQueues {

        private final BlockingQueue<StopPlace> stopPlaceQueue;
        private final BlockingQueue<TopographicPlace> topographicPlaceQueue;
        private final BlockingQueue<NavigationPath> navigationPathsQueue;

        public ResultQueues(int size) {

            stopPlaceQueue = new ArrayBlockingQueue<StopPlace>(size);
            topographicPlaceQueue = new ArrayBlockingQueue<TopographicPlace>(size);
            navigationPathsQueue = new ArrayBlockingQueue<NavigationPath>(size);
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
                    .add("stopPlaceQueue", stopPlaceQueue.size())
                    .add("topographicPlaceQueue", topographicPlaceQueue.size())
                    .add("navigationPathsQueue", navigationPathsQueue.size())
                    .toString();
        }
    }

}

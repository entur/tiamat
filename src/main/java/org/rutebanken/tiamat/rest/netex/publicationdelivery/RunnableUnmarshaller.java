package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.rutebanken.netex.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

public class RunnableUnmarshaller implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RunnableUnmarshaller.class);

    public static final StopPlace POISON_STOP_PLACE = new StopPlace().withId("-100");

    private final InputStream inputStream;
    private final Unmarshaller unmarshaller;
    private final UnmarshalResult unmarshalResult;

    public RunnableUnmarshaller(InputStream inputStream, Unmarshaller unmarshaller, UnmarshalResult unmarshalResult) {
        this.inputStream = inputStream;
        this.unmarshaller = unmarshaller;
        this.unmarshalResult = unmarshalResult;
    }

    @Override
    public void run() {
        final XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
        AtomicInteger stops = new AtomicInteger();

        final XMLEventReader xmlEventReader;
        try {

            xmlEventReader = xmlInputFactory.createXMLEventReader(inputStream);

            while (xmlEventReader.hasNext()) {

                XMLEvent xmlEvent = xmlEventReader.peek();

                logger.trace("XmlEvent {}", xmlEvent);
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    String localPartOfName = startElement.getName().getLocalPart();

                    if (localPartOfName.equals("StopPlace")) {
                        StopPlace stopPlace = unmarshaller.unmarshal(xmlEventReader, StopPlace.class).getValue();
                        stops.incrementAndGet();
                        unmarshalResult.getStopPlaceQueue().put(stopPlace);

                        if (stops.get() % 20 == 0) {
                            logger.info("Unmarshalled stop number {}", stops.get());
                        }
                        continue;
                    }
                } else if (xmlEvent.isEndElement()) {
                    EndElement endElement = xmlEvent.asEndElement();
                    String localPartOfName = endElement.getName().getLocalPart();
                    if (localPartOfName.equals("stopPlaces")) {
                        logger.info("End of stop places in incoming XML. Counter ended at {}. Adding poison pill to the queue.", stops.get());
                        unmarshalResult.getStopPlaceQueue().put(POISON_STOP_PLACE);
                    }
                }
                xmlEventReader.next();
            }
        } catch (XMLStreamException | InterruptedException | JAXBException e) {

            logger.error("Could not read netex from events. Stopping. " + e.getMessage(), e);
            try {
                unmarshalResult.getStopPlaceQueue().put(POISON_STOP_PLACE);
            } catch (InterruptedException e2) {
                logger.warn("Interrupted when adding poison stop place to queue", e2);
            }
        }
        logger.info("Unmarshalling thread finished after {} stops", stops.get());
    }

}

package org.rutebanken.tiamat.rest.write;

import jakarta.ws.rs.core.StreamingOutput;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static jakarta.xml.bind.JAXBContext.newInstance;

@Component
public class StopPlaceXmlWriter {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceXmlWriter.class);

    private static final JAXBContext jaxbContext;

    private final ObjectFactory objectFactory = new ObjectFactory();

    static {
        try {
            jaxbContext = newInstance(StopPlace.class);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to initialize JAXB context for StopPlace", e);
        }
    }

    public StreamingOutput write(StopPlace stopPlace) {
        return outputStream -> {
            try {
                Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                JAXBElement<StopPlace> jaxbElement = objectFactory.createStopPlace(stopPlace);

                logger.debug("Marshalling StopPlace to XML output stream");
                marshaller.marshal(jaxbElement, outputStream);
            } catch (JAXBException e) {
                logger.error("Failed to marshal StopPlace to XML: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to marshal StopPlace to XML", e);
            }
        };
    }
}


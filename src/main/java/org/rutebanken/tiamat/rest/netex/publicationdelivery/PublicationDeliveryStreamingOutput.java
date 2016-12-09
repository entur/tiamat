package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.validation.NeTExValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static javax.xml.bind.JAXBContext.newInstance;

@Component
public class PublicationDeliveryStreamingOutput {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryStreamingOutput.class);

    private static final JAXBContext jaxbContext;

    private final ObjectFactory objectFactory = new ObjectFactory();

    private final NeTExValidator neTExValidator = new NeTExValidator();

    static {
        try {
            jaxbContext = newInstance(PublicationDeliveryStructure.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public PublicationDeliveryStreamingOutput() throws IOException, SAXException {
    }

    public StreamingOutput stream(PublicationDeliveryStructure publicationDelivery) throws JAXBException, IOException, SAXException {
        Marshaller marshaller = jaxbContext.createMarshaller();

        JAXBElement<PublicationDeliveryStructure> jaxPublicationDelivery = objectFactory.createPublicationDelivery(publicationDelivery);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        logXmlIfDebugEnabled(jaxPublicationDelivery, marshaller);

        marshaller.setSchema(neTExValidator.getSchema());

        return outputStream -> {
            try {
                logger.debug("Marshalling publication delivery to outputstream");
                marshaller.marshal(jaxPublicationDelivery, outputStream);
            } catch (JAXBException e) {
                throw new RuntimeException("Could not stream site frame", e);
            }
        };

    }

    private void logXmlIfDebugEnabled(JAXBElement<PublicationDeliveryStructure> jaxPublicationDelivery, Marshaller marshaller) throws JAXBException {
        if(logger.isDebugEnabled()) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            marshaller.marshal(jaxPublicationDelivery, byteArrayOutputStream);
            logger.debug("Logging marshalled NeTEx XML as debug is enabled. \n{}", byteArrayOutputStream.toString());
        }
    }

}

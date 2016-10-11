package no.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.*;
import java.io.InputStream;

import static javax.xml.bind.JAXBContext.newInstance;

@Component
public class PublicationDeliveryStreamingOutput {

    private static final JAXBContext jaxbContext;

    private final ObjectFactory objectFactory = new ObjectFactory();

    static {
        try {
            jaxbContext = newInstance(PublicationDeliveryStructure.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public StreamingOutput stream(PublicationDeliveryStructure publicationDelivery) throws JAXBException {
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        return outputStream -> {
            try {
                marshaller.marshal(objectFactory.createPublicationDelivery(publicationDelivery), outputStream);
            } catch (JAXBException e) {
                throw new RuntimeException("Could not stream site frame", e);
            }
        };

    }

}

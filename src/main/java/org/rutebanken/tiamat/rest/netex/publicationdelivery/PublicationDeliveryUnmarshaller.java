package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.tiamat.netex.validation.NeTExValidator;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;

import static javax.xml.bind.JAXBContext.*;

@Component
public class PublicationDeliveryUnmarshaller {

    private static final JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = newInstance(PublicationDeliveryStructure.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private final NeTExValidator neTExValidator;

    public PublicationDeliveryUnmarshaller() throws IOException, SAXException {
        this.neTExValidator = new NeTExValidator();
    }

    public PublicationDeliveryStructure unmarshal(InputStream inputStream) throws JAXBException, IOException, SAXException {
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        jaxbUnmarshaller.setSchema(neTExValidator.getSchema());
        JAXBElement<PublicationDeliveryStructure> jaxbElement =
                (JAXBElement<org.rutebanken.netex.model.PublicationDeliveryStructure>) jaxbUnmarshaller.unmarshal(inputStream);
        PublicationDeliveryStructure publicationDeliveryStructure = jaxbElement.getValue();
        return publicationDeliveryStructure;
    }
}

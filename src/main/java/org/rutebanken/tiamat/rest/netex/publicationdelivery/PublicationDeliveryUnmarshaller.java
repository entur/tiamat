package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import com.google.common.io.ByteStreams;
import javassist.bytecode.ByteArray;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.validation.NeTExValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.bind.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.io.ByteStreams.toByteArray;
import static javax.xml.bind.JAXBContext.*;

@Component
public class PublicationDeliveryUnmarshaller {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryUnmarshaller.class);

    private static final JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = newInstance(PublicationDeliveryStructure.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private final NeTExValidator neTExValidator;

    @Value("${publicationDeliveryUnmarshaller.validateAgainstSchema:true}")
    private boolean validateAgainstSchema;

    public PublicationDeliveryUnmarshaller() throws IOException, SAXException {
        this.neTExValidator = new NeTExValidator();
    }

    public PublicationDeliveryStructure unmarshal(InputStream inputStream) throws JAXBException, IOException, SAXException {
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        if(logger.isDebugEnabled()) {
            String xml = new String(toByteArray(inputStream));
            logger.debug("Debug is enabled. Will log the input (this kills performance.):\n{}", xml);
            inputStream = new ByteArrayInputStream(xml.getBytes());
            logger.debug("Valdiation enabled? {}", validateAgainstSchema);
        }

        if(validateAgainstSchema) {
            jaxbUnmarshaller.setSchema(neTExValidator.getSchema());
        }

        logger.debug("Unmarshalling incoming publication delivery structure. Schema validation enabled: {}", validateAgainstSchema);

        JAXBElement<PublicationDeliveryStructure> jaxbElement =
                (JAXBElement<org.rutebanken.netex.model.PublicationDeliveryStructure>) jaxbUnmarshaller.unmarshal(inputStream);
        PublicationDeliveryStructure publicationDeliveryStructure = jaxbElement.getValue();
        logger.debug("Done unmarshalling incoming publication delivery structure with schema validation enabled: {}", validateAgainstSchema);
        return publicationDeliveryStructure;
    }
}

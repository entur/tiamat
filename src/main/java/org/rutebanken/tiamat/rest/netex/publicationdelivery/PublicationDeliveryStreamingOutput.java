/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.validation.NeTExValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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


    @Value("${publicationDeliveryStreamingOutput.validateAgainstSchema:true}")
    private boolean validateAgainstSchema = true;

    public PublicationDeliveryStreamingOutput() throws IOException, SAXException {
    }

    public StreamingOutput stream(PublicationDeliveryStructure publicationDelivery) throws JAXBException, IOException, SAXException {
        Marshaller marshaller = jaxbContext.createMarshaller();

        JAXBElement<PublicationDeliveryStructure> jaxPublicationDelivery = objectFactory.createPublicationDelivery(publicationDelivery);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        logXmlIfDebugEnabled(jaxPublicationDelivery, marshaller);

        if(validateAgainstSchema) {
            marshaller.setSchema(neTExValidator.getSchema());
        }

        return outputStream -> {
            try {
                logger.debug("Marshalling publication delivery to outputstream");
                marshaller.marshal(jaxPublicationDelivery, outputStream);
            } catch (JAXBException e) {
                logger.warn("Could not stream site frame. "+e.getMessage(), e);
                throw new RuntimeException("Could not stream site frame" , e);
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

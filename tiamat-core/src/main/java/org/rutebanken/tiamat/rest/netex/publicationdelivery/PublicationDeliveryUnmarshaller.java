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

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.entur.netex2.model.PublicationDeliveryStructure;
import org.entur.netex2.validation.NeTExValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.io.ByteStreams.toByteArray;
import static jakarta.xml.bind.JAXBContext.newInstance;

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

    /**
     * How far into the document the root element's version attribute is looked for. The root
     * start tag comes right after the XML declaration, so this only needs to cover the parser's
     * read-ahead.
     */
    private static final int VERSION_PEEK_LIMIT = 1024 * 1024;

    /**
     * The schema every document was validated against before validation followed the declared
     * version, so a document that does not declare a recognisable version keeps that behaviour.
     */
    static final NeTExValidator.NetexVersion DEFAULT_SCHEMA_VERSION = NeTExValidator.NetexVersion.v1_16;

    private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newFactory();

    static {
        XML_INPUT_FACTORY.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        XML_INPUT_FACTORY.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    }

    @Value("${publicationDeliveryUnmarshaller.validateAgainstSchema:true}")
    private boolean validateAgainstSchema;

    public PublicationDeliveryUnmarshaller() throws IOException, SAXException {
        NeTExValidator.getNeTExValidator();
        NeTExValidator.getNeTExValidator(DEFAULT_SCHEMA_VERSION);
    }

    public PublicationDeliveryStructure unmarshal(InputStream inputStream) throws JAXBException, IOException, SAXException {
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        jaxbUnmarshaller.setEventHandler(new jakarta.xml.bind.helpers.DefaultValidationEventHandler());


        if(logger.isDebugEnabled()) {
            String xml = new String(toByteArray(inputStream));
            logger.debug("Debug is enabled. Will log the input (this kills performance.):\n{}", xml);
            inputStream = new ByteArrayInputStream(xml.getBytes());
            logger.debug("Valdiation enabled? {}", validateAgainstSchema);
        }

        if(validateAgainstSchema) {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            NeTExValidator.NetexVersion schemaVersion = schemaVersionFor(readVersionAttribute(bufferedInputStream));
            inputStream = bufferedInputStream;
            logger.debug("Validating incoming publication delivery against NeTEx {} schema", schemaVersion);
            jaxbUnmarshaller.setSchema(NeTExValidator.getNeTExValidator(schemaVersion).getSchema());
        }

        logger.debug("Unmarshalling incoming publication delivery structure. Schema validation enabled: {}", validateAgainstSchema);

        JAXBElement<PublicationDeliveryStructure> jaxbElement = jaxbUnmarshaller.unmarshal(new StreamSource(inputStream), PublicationDeliveryStructure.class);
        PublicationDeliveryStructure publicationDeliveryStructure = jaxbElement.getValue();
        logger.debug("Done unmarshalling incoming publication delivery structure with schema validation enabled: {}", validateAgainstSchema);
        return publicationDeliveryStructure;
    }

    /**
     * Picks the schema a document is validated against from the version it declares, so that a
     * NeTEx 1.x document is not rejected for constructs only NeTEx 2.0 forbids (e.g. an entity id
     * as the id of a placeEquipments container, which 2.0 restricts to RelationshipId).
     * <p>
     * The version attribute has the form {netexSchemaVersion}:{profileName}:{profileVersion}, e.g.
     * "1.15:NO-NeTEx-stops:1.5", or is just the schema version, e.g. "2.0". Only major.minor is
     * compared, so "1.16.1" is validated as 1.16. An unknown 2.x version is validated against the
     * latest schema, and a missing or other unknown version against {@link #DEFAULT_SCHEMA_VERSION}.
     */
    static NeTExValidator.NetexVersion schemaVersionFor(String versionAttribute) {
        if (versionAttribute == null || versionAttribute.isBlank()) {
            return DEFAULT_SCHEMA_VERSION;
        }
        String[] versionParts = versionAttribute.split(":")[0].trim().split("\\.");
        String majorMinor = versionParts.length > 1 ? versionParts[0] + "." + versionParts[1] : versionParts[0];
        for (NeTExValidator.NetexVersion netexVersion : NeTExValidator.NetexVersion.values()) {
            if (netexVersion.toString().replaceAll("[^0-9.]", "").equals(majorMinor)) {
                return netexVersion;
            }
        }
        NeTExValidator.NetexVersion fallback = versionParts[0].equals("2") ? NeTExValidator.LATEST : DEFAULT_SCHEMA_VERSION;
        logger.info("Unknown NeTEx version '{}' in publication delivery, validating against NeTEx {} schema", versionAttribute, fallback);
        return fallback;
    }

    /**
     * Reads the version attribute of the root element and rewinds the stream, so the document can
     * still be unmarshalled from the start.
     *
     * @return the version attribute, or null if it is absent or the document cannot be read. A
     * document that cannot be read fails in the unmarshaller with the real error.
     */
    private static String readVersionAttribute(BufferedInputStream inputStream) throws IOException {
        inputStream.mark(VERSION_PEEK_LIMIT);
        try {
            XMLStreamReader reader = XML_INPUT_FACTORY.createXMLStreamReader(inputStream);
            try {
                while (reader.hasNext()) {
                    if (reader.next() == XMLStreamConstants.START_ELEMENT) {
                        return reader.getAttributeValue(null, "version");
                    }
                }
                return null;
            } finally {
                reader.close();
            }
        } catch (XMLStreamException e) {
            logger.debug("Could not read version of publication delivery", e);
            return null;
        } finally {
            inputStream.reset();
        }
    }
}

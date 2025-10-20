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

package org.rutebanken.tiamat.rest.netex.publicationdelivery.async;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.netex.validation.NeTExValidator;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static com.google.common.io.ByteStreams.toByteArray;
import static jakarta.xml.bind.JAXBContext.newInstance;
import static org.rutebanken.tiamat.netex.mapping.NetexMappingContextThreadLocal.updateMappingContext;

/**
 * Unmarshal publication delivery in multiple steps:
 * * Write everything to temp file
 * * Unmarshal publication delivery from file, but filter out certain parts
 * * Unmarshal publication delivery again, but unmarshal only stop places
 * To be able to not having to hold everything in memory.
 */
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

    private NeTExValidator neTExValidator;

    /**
     * Validating large incoming XMLs against schema often leads to StackOverFlowException.
     */
    @Value("${publicationDeliveryPartialUnmarshaller.validateAgainstSchema:false}")
    private boolean validateAgainstSchema;

    private final PublicationDeliveryHelper publicationDeliveryHelper;

    @Autowired
    public PublicationDeliveryPartialUnmarshaller(PublicationDeliveryHelper publicationDeliveryHelper) throws IOException, SAXException {
        this.publicationDeliveryHelper = publicationDeliveryHelper;
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
            this.neTExValidator = NeTExValidator.getNeTExValidator();
            unmarshaller.setSchema(neTExValidator.getSchema());
        }

        XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();

        logger.debug("Unmarshalling incoming publication delivery structure. Schema validation enabled: {}", validateAgainstSchema);

        PublicationDeliveryStructure publicationDeliveryStructure = readPublicationDeliveryStructure(xmlInputFactory, new FileInputStream(file), unmarshaller);
        updateMappingContext(publicationDeliveryHelper.findSiteFrame(publicationDeliveryStructure));

        // Read the rest from the same file
        UnmarshalResult unmarshalResult = readWithXmlEventReaderAsync(new FileInputStream(file), unmarshaller);
        unmarshalResult.setPublicationDeliveryStructure(publicationDeliveryStructure);

        logger.debug("Done unmarshalling incoming publication delivery structure with schema validation enabled: {}", validateAgainstSchema);
        return unmarshalResult;
    }


    /**
     * Unmarshal publication delivery structure without stop places, topographic places and navigation paths.
     */
    private PublicationDeliveryStructure readPublicationDeliveryStructure(XMLInputFactory xmlInputFactory, InputStream inputStream, Unmarshaller unmarshaller) throws FileNotFoundException, XMLStreamException, JAXBException {

        EventFilter eventFilter = new TypesEventFilter("stopPlaces", "navigationPaths", "parkings");

        XMLEventReader xmlEventReader = xmlInputFactory.createFilteredReader(xmlInputFactory.createXMLEventReader(inputStream), eventFilter);
        PublicationDeliveryStructure publicationDeliveryStructure = unmarshaller.unmarshal(xmlEventReader, PublicationDeliveryStructure.class).getValue();

        xmlEventReader.close();
        return publicationDeliveryStructure;
    }

    public UnmarshalResult readWithXmlEventReaderAsync(InputStream inputStream, Unmarshaller unmarshaller) throws XMLStreamException, JAXBException, InterruptedException, IOException {

        UnmarshalResult unmarshalResult = new UnmarshalResult(100);

        Thread thread = new Thread(new RunnableUnmarshaller(inputStream, unmarshaller, unmarshalResult));

        thread.setName("unmarshalling-thread");
        logger.info("Starting unmarshalling thread ", thread);
        thread.start();
        return unmarshalResult;
    }

}

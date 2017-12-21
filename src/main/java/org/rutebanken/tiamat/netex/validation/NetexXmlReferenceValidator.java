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

package org.rutebanken.tiamat.netex.validation;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides simple validation for references in netex file.
 */
@Component
public class NetexXmlReferenceValidator {

    private static final Logger logger = LoggerFactory.getLogger(NetexXmlReferenceValidator.class);
    public static final String REF_ELEMENT_NAME_POSTFIX = "Ref";
    public static final String COUNTRY_REF = "CountryRef";
    public static final String REF_ATTRIBUTE = "ref";
    public static final String VERSION_ATTRIBUTE = "version";
    public static final String ID_VERSION_SEPARATOR = "-";
    public static final String ID_ATTRIBUTE = "id";

    private final boolean throwOnValidationError;

    public NetexXmlReferenceValidator(@Value("${netexXmlReferenceValidator.throwOnValidationError:true}") boolean throwOnValidationError) {
        this.throwOnValidationError = throwOnValidationError;
    }

    public void validateNetexReferences(File file) throws NetexReferenceValidatorException {
        try {
            validateNetexReferences(new FileInputStream(file), file.getName());
        } catch (FileNotFoundException e) {
            throw new NetexReferenceValidatorException("Error reading file path " + file.getName(), e);
        }
    }

    public void validateNetexReferences(InputStream inputStream, String xmlNameForLogging) throws NetexReferenceValidatorException {

        long start = System.currentTimeMillis();

        try {
            XMLStreamReader xmlStreamReader = null;
            xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);

            Set<String> references = new HashSet<>();

            // List, because of intentions to detect and report duplicate identificators.
            List<String> identificators = new ArrayList<>();

            traverseXml(xmlStreamReader, references, identificators);

            Set<String> distinctIdentificators = Sets.newHashSet(identificators);
            Set<String> invalidReferences = Sets.difference(references, distinctIdentificators);

            if (invalidReferences.isEmpty()) {
                logger.info("{} is valid. {} distinct identificators. {} references", xmlNameForLogging, distinctIdentificators.size(), references.size());
            } else {
                String message = xmlNameForLogging + " is NOT valid. Invalid references detected: " + invalidReferences.size();
                logger.warn("{}: {}", message, invalidReferences);
                if (throwOnValidationError) {
                    throw new NetexReferenceValidatorException(message);
                }
            }

        } catch (XMLStreamException e) {
            throw new NetexReferenceValidatorException("Error streaming " + xmlNameForLogging, e);
        } finally {
            logger.info("Spent {} ms validating {}", System.currentTimeMillis() - start, xmlNameForLogging);
        }
    }

    private void traverseXml(XMLStreamReader xmlStreamReader, Set<String> references, List<String> identificators) throws XMLStreamException {
        while (xmlStreamReader.hasNext()) {

            int eventCode = xmlStreamReader.next();

            if ((XMLStreamConstants.START_ELEMENT == eventCode)) {
                String localName = xmlStreamReader.getLocalName();
                if (localName.contains(REF_ELEMENT_NAME_POSTFIX) && !localName.contains(COUNTRY_REF)) {
                    processReference(xmlStreamReader, references);
                } else {
                    processId(xmlStreamReader, identificators);
                }
            }
        }
    }

    private void processReference(XMLStreamReader xmlStreamReader, Set<String> references) {
        String value = getAttributeValue(REF_ATTRIBUTE, xmlStreamReader);
        if (value != null) {
            String version = getAttributeValue(VERSION_ATTRIBUTE, xmlStreamReader);
            if (version == null) {
                references.add(value);
            } else {
                references.add(value + ID_VERSION_SEPARATOR + version);
            }
        }
    }

    private void processId(XMLStreamReader xmlStreamReader, List<String> identificators) {
        String id = getAttributeValue(ID_ATTRIBUTE, xmlStreamReader);

        if (id != null) {
            String version = getAttributeValue(VERSION_ATTRIBUTE, xmlStreamReader);
            if (version != null) {
                // It should be possible for a reference to not have version.
                // So both should be added
                identificators.add(id);
                identificators.add(id + ID_VERSION_SEPARATOR + version);
            } else {
                identificators.add(id);
            }
        }
    }

    private String getAttributeValue(String attribute, XMLStreamReader xmlStreamReader) {
        return xmlStreamReader.getAttributeValue(null, attribute);
    }
}


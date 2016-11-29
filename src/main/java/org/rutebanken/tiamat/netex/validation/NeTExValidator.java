package org.rutebanken.tiamat.netex.validation;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

@Service
public class NeTExValidator {
    
    private final Schema netexSchema;

    public NeTExValidator() throws IOException, SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        factory.setResourceResolver(new PredefinedSchemaListClasspathResourceResolver("/netex_schema_list.txt"));
        Source schemaFile = new StreamSource(getClass().getResourceAsStream("/NeTEx-XML-1.04beta/schema/xsd/NeTEx_publication.xsd"));
        netexSchema = factory.newSchema(schemaFile);
    }

    public Schema getSchema() throws SAXException, IOException {
        return netexSchema;
    }

    public void validate(StreamSource streamSource) throws IOException, SAXException {
        Validator validator = netexSchema.newValidator();
        validator.validate(streamSource);
    }
}

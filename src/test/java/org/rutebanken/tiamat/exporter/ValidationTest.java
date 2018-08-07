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

package org.rutebanken.tiamat.exporter;

import com.google.common.collect.Sets;
import org.junit.Ignore;
import org.junit.Test;
import org.rutebanken.netex.validation.NeTExValidator;
import org.springframework.util.CollectionUtils;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Java6Assertions.fail;

/**
 * Test for manual execution. For XSD validation of existing files.
 * Used for checking validation time for large netex files.
 */
public class ValidationTest {


    @Ignore
    @Test
    public void test() throws IOException, SAXException {

        String file = "/home/cristoffer/tiamat-export-20171220-162956-911283.xml";
        System.out.println("loading file "+file);
        Source xmlFile = new StreamSource(new File(file));

        NeTExValidator neTExValidator = new NeTExValidator(NeTExValidator.NetexVersion.v1_0_9);

        try {
            neTExValidator.validate(xmlFile);

            System.out.println(xmlFile.getSystemId() + " is valid");
        } catch (SAXException e) {
            System.out.println(xmlFile.getSystemId() + " is NOT valid reason:" + e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

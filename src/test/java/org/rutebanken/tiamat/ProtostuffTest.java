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

package org.rutebanken.tiamat;

import com.google.api.client.util.IOUtils;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.ProtostuffOutput;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.h2.store.fs.FileUtils;
import org.hsqldb.lib.StringInputStream;
import org.junit.Test;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.validation.NeTExValidator;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.rutebanken.tiamat.diff.TiamatObjectDiffer;
import org.rutebanken.tiamat.diff.generic.Difference;
import org.rutebanken.tiamat.diff.generic.GenericDiffConfig;
import org.rutebanken.tiamat.diff.generic.GenericObjectDiffer;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryUnmarshaller;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.util.List;

import static javax.xml.bind.JAXBContext.newInstance;
import static org.assertj.core.api.Assertions.assertThat;

public final class ProtostuffTest {


    private final ObjectFactory objectFactory = new ObjectFactory();

    protected GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();
    private PublicationDeliveryUnmarshaller publicationDeliveryUnmarshaller = new PublicationDeliveryUnmarshaller();


    public ProtostuffTest() throws IOException, SAXException {
    }

    @Test
    public void netexStopPlace() throws IllegalAccessException {
        Point point = point(10.7096245, 59.9086885);

        StopPlace initialStopPlace = new StopPlace();
        initialStopPlace.setCentroid(point);
        initialStopPlace.setName(new EmbeddableMultilingualString("Filipstad", "no"));
        initialStopPlace.getOrCreateValues(NetexIdMapper.ORIGINAL_ID_KEY).add("original-id-filipstad");
        initialStopPlace.setVersion(1L);

        System.out.println(initialStopPlace);

        Schema<StopPlace> schema = RuntimeSchema.getSchema(StopPlace.class);

        LinkedBuffer buffer = LinkedBuffer.allocate(512);

        final byte[] protostuff;
        try {
            protostuff = ProtostuffIOUtil.toByteArray(initialStopPlace, schema, buffer);
        } finally {
            buffer.clear();
        }
        System.out.println(protostuff.length);

        StopPlace stopPlaceWasProtostuffed = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(protostuff, stopPlaceWasProtostuffed, schema);

        System.out.println(stopPlaceWasProtostuffed);
        assertThat(stopPlaceWasProtostuffed.getName().getValue()).isEqualTo(initialStopPlace.getName().getValue());
        assertThat(stopPlaceWasProtostuffed).isEqualTo(initialStopPlace); // Equal method of stop place
    }


    /**
     * Read netex example file. Convert it to protostuff. Compare the result with netex xml string values.
     * @throws SAXException
     */
    @Test
    public void netexPublicationDelivery() throws IllegalAccessException, IOException, JAXBException, SAXException {

        // To avoid empty collections being serialized to null, the switch
        // -Dprotostuff.runtime.collection_schema_on_repeated_fields=true
        // must be set

        System.setProperty("protostuff.runtime.collection_schema_on_repeated_fields", "true");


        String netexPublicationDeliveryFile = "publication_delivery/tiamat_publication_delivery.xml";
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(netexPublicationDeliveryFile).getFile());

        PublicationDeliveryStructure netexPublicationDelivery = publicationDeliveryUnmarshaller.unmarshal(new FileInputStream(file));


        Schema<PublicationDeliveryStructure> schema = RuntimeSchema.getSchema(PublicationDeliveryStructure.class);


        LinkedBuffer buffer = LinkedBuffer.allocate(512);

        final byte[] protostuff;
        try {
            protostuff = ProtostuffIOUtil.toByteArray(netexPublicationDelivery, schema, buffer);
        } finally {
            buffer.clear();
        }
        System.out.println(protostuff.length);

        PublicationDeliveryStructure netexPublicationDeliveryWasProtostuffed = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(protostuff, netexPublicationDeliveryWasProtostuffed, schema);

        assertThat(netexPublicationDeliveryWasProtostuffed.getParticipantRef()).isEqualTo(netexPublicationDelivery.getParticipantRef());

//        diff(netexPublicationDeliveryWasProtostuffed, netexPublicationDelivery);

        compareXmlStrings(netexPublicationDeliveryWasProtostuffed, netexPublicationDelivery);
    }

    @Test
    public void netexPublicationDeliveryFromExternalFile() throws IllegalAccessException, IOException, JAXBException, SAXException {

        // To avoid empty collections being serialized to null, the switch
        // -Dprotostuff.runtime.collection_schema_on_repeated_fields=true
        // must be set

        System.setProperty("protostuff.runtime.collection_schema_on_repeated_fields", "true");


        // With large files. Must set -Dmx=30g
        File file = new File("/home/cristoffer/rutebanken/tiamat/Current-export-20180906-013339-15656216.xml");

        PublicationDeliveryStructure netexPublicationDelivery = publicationDeliveryUnmarshaller.unmarshal(new FileInputStream(file));


        long schemaStarted = System.currentTimeMillis();
        Schema<PublicationDeliveryStructure> schema = RuntimeSchema.getSchema(PublicationDeliveryStructure.class);
        System.out.println("Schema created in "+ (System.currentTimeMillis()-schemaStarted) + " ms");

        long bufferStarted = System.currentTimeMillis();

        LinkedBuffer buffer = LinkedBuffer.allocate(512);

        final byte[] protostuff;
        try {
            protostuff = ProtostuffIOUtil.toByteArray(netexPublicationDelivery, schema, buffer);
        } finally {
            buffer.clear();
        }
        System.out.println("Written to protostuff in  "+ (System.currentTimeMillis()-bufferStarted) + " ms");

        System.out.println("The byte array length is " + protostuff.length);

        long serializeBack = System.currentTimeMillis();
        PublicationDeliveryStructure netexPublicationDeliveryWasProtostuffed = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(protostuff, netexPublicationDeliveryWasProtostuffed, schema);

        System.out.println("Deserialized from protobuf in  "+ (System.currentTimeMillis()-serializeBack) + " ms");

        assertThat(netexPublicationDeliveryWasProtostuffed.getParticipantRef()).isEqualTo(netexPublicationDelivery.getParticipantRef());

//        diff(netexPublicationDeliveryWasProtostuffed, netexPublicationDelivery);

        compareXmlStrings(netexPublicationDeliveryWasProtostuffed, netexPublicationDelivery);
    }

    /**
     * Does not work well with jaxb elements
     * @param pb1
     * @param pb2
     * @throws IllegalAccessException
     */
    public void diff(PublicationDeliveryStructure pb1, PublicationDeliveryStructure pb2) throws IllegalAccessException {
        GenericObjectDiffer genericObjectDiffer = new GenericObjectDiffer();

        GenericDiffConfig diffConfig = GenericDiffConfig.builder().ignoreFields(Sets.newHashSet()).build();
        List<Difference> differences = genericObjectDiffer.compareObjects(pb1, pb2, diffConfig);

        System.out.println(genericObjectDiffer.diffListToString(differences));
        assertThat(differences).isEmpty();

    }

    private void compareXmlStrings(PublicationDeliveryStructure actual, PublicationDeliveryStructure expected) throws JAXBException, IOException, SAXException {
        String actualXml = xmlify(actual);
        IOUtils.copy(new StringInputStream(actualXml), new FileOutputStream("actual.xml"));
        String expectedXml = xmlify(expected);
        IOUtils.copy(new StringInputStream(expectedXml), new FileOutputStream("expected.xml"));
        System.out.println("Files written to actual.xml and expected.xml");
        assertThat(actualXml).as("Actual xml").isEqualTo(expectedXml);
    }

    private String xmlify(PublicationDeliveryStructure publicationDeliveryStructure) throws JAXBException, IOException, SAXException {
        JAXBElement<PublicationDeliveryStructure> jaxPublicationDelivery = objectFactory.createPublicationDelivery(publicationDeliveryStructure);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        createMarshaller().marshal(jaxPublicationDelivery, byteArrayOutputStream);
        return byteArrayOutputStream.toString();
    }


    private static JAXBContext createContext(Class clazz) {
        try {
            JAXBContext jaxbContext = newInstance(clazz);
            return jaxbContext;
        } catch (JAXBException e) {
            throw new RuntimeException("Could not create instance of jaxb context for class " + clazz, e);
        }
    }

    private Marshaller createMarshaller() throws JAXBException, IOException, SAXException {
        Marshaller marshaller = createContext(PublicationDeliveryStructure.class).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "");


//        marshaller.setSchema(new NeTExValidator().getSchema());


        return marshaller;
    }


    private Point point(double longitude, double latitude) {
        return
                geometryFactory.createPoint(
                        new Coordinate(longitude, latitude));
    }
}